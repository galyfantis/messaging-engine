package org.gal.messaging.engine.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import org.gal.messaging.engine.api.ErrorOccuredMsg;
import org.gal.messaging.engine.api.ErrorOccuredMsg.ErrorLevel;
import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.api.Plugin;
import org.gal.messaging.engine.api.PluginContext;
import org.gal.messaging.engine.core.api.Engine;
import org.gal.messaging.engine.core.api.EngineException;
import org.gal.messaging.engine.core.api.ExecutorProvider;
import org.gal.messaging.engine.core.api.MessageDispatcher;
import org.gal.messaging.engine.core.api.MessageEnvelope;
import org.gal.messaging.engine.core.api.MessageHeader;
import org.gal.messaging.engine.core.api.MessageListener;
import org.gal.messaging.engine.core.api.MessageMapper;
import org.gal.messaging.engine.core.api.MessagePluginResolver;
import org.gal.messaging.engine.core.api.Reducer;

class EngineImpl implements Engine {
	
	private final MessageDispatcher messagingDispatcher;
	
	private final MessageListenerRegistry listenersRegistry = new MessageListenerRegistry();

	private final MessageMapperRegistry messageMapperRegistry = new MessageMapperRegistry();
	
	private final PluginRegistry pluginRegistry = new PluginRegistry();
	
	private final ScheduledExecutorService scheduledExecutorService;

	private boolean started = false;
	
	private List<Reducer> reducers = new ArrayList<>();

	public EngineImpl(Integer executors, ExecutorProvider executorProvider) {
		this.scheduledExecutorService = executorProvider.newScheduledExecutorService();
		this.messagingDispatcher = new MessageDispatcherImpl(new MultiExecutorService(executors, executorProvider), this::doHandle);
	}

	@Override
	public synchronized void start() {
		started = true;
	}
	
	@Override
	public synchronized void stop() {
		try {
			this.scheduledExecutorService.shutdown();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			started = false;
		}
	}
	
	@Override
	public void registerPlugin(Plugin<?, ?, ?> plugin) {
		pluginRegistry.register(plugin);
	}

	@Override
	public void unregisterPlugin(Plugin<?, ?, ?> plugin) {
		pluginRegistry.unregister(plugin);
	}
	
	@Override
	public void registerMessageMapper(MessageMapper<?> messageMapper) {
		messageMapperRegistry.register(messageMapper);
	}

	@Override
	public void unregisterMessageMapper(MessageMapper<?> messageMapper) {
		messageMapperRegistry.unregister(messageMapper);
	}

	@Override
	public void registerReducer(Reducer reducer) {
		this.reducers.add(reducer);
	}
	
	@Override
	public MessagePluginResolver pluginResolver() {
		return plugin -> getPluginOrThrow(plugin);
	}

	@Override
	public <T> void registerListener(MessageListener<T> listener, String format) {
		listenersRegistry.register(listener, format);
	}
	
	@Override
	public <T> void unregisterListener(MessageListener<T> listener, String format) {
		listenersRegistry.unregister(listener, format);
	}

	private void send(MessageEnvelope messageEnvelope, List<String> recipients) {
		List<String> formats = listenersRegistry.formats();
		for (String format : formats) {
			MessageMapper<?> mapper = getMessageMapperOrThrow(format);
			Object message = mapper.write(messageEnvelope);
			List<MessageListener<?>> listenersByFormat = listenersRegistry.listenersByFormat(format);
			for (MessageListener<?> listener : listenersByFormat) {
				captureOnMessage(listener, message, recipients);
			}
		}
	}
	
	private static <T> void captureOnMessage(MessageListener<T> listener, Object message, List<String> recipients) {
		listener.onMessage((T) message, recipients);
	}
	
	@Override
	public <T> void handle(T message, String format, MessageContext ctx) {
		try {
			MessageEnvelope messageEnvelope = readToMessageEnvelope(getMessageMapperOrThrow(format), message);
			this.messagingDispatcher.dispatch(messageEnvelope, ctx);
		} catch (EngineException e) {
			error(e.getCode(), e.getMessage(), null, null, ctx);
			e.printStackTrace();
		} catch (Exception e) {
			error("UKN", e.getMessage(), null, null, ctx);
			e.printStackTrace();
		}
	}
	
	// TODO: preferably have the plugin resolver inside the mapper...
	private static <T> MessageEnvelope readToMessageEnvelope(MessageMapper<T> mapper, Object message) {
		return mapper.read((T)message);
	}

	private void doHandle(MessageEnvelope messageEnvelope, MessageContext ctx) {
		try {
			assertEngineStarted();
			Plugin<?, ?, ?> plugin = getPluginOrThrow(messageEnvelope.header().plugin());
			
			reducers.stream()
					.filter(r -> r.isCompatible(plugin, messageEnvelope, ctx))
					.forEach(r -> doReduce(r, plugin, messageEnvelope, ctx));
		} catch (EngineException e) {
			error(e.getCode(), e.getMessage(), messageEnvelope.header().plugin(), messageEnvelope.header().uuid(), ctx);
			e.printStackTrace();
		} catch (Exception e) {
			error("UKN", e.getMessage(), messageEnvelope.header().plugin(), messageEnvelope.header().uuid(), ctx);
			e.printStackTrace();
		}
	}
	
	private void error(String code, String message, String plugin, String causedBy, MessageContext ctx) {
		ErrorOccuredMsg errorOccured = ErrorOccuredMsg.of(ErrorLevel.ERROR, code, message, plugin, causedBy);
		errorOccured(errorOccured, causedBy, ctx);
	}
	
	private void warn(String code, String message, String plugin, String causedBy, MessageContext ctx) {
		ErrorOccuredMsg errorOccured = ErrorOccuredMsg.of(ErrorLevel.WARNING, code, message, plugin, causedBy);
		errorOccured(errorOccured, causedBy, ctx);
	}
	
	private void errorOccured(Message errorOccured, String causedBy, MessageContext ctx) {
		MessageHeader header = MessageHeader.Builder()
				.plugin("engine")
				.type("error_occured")
				.uuid(UUID.randomUUID().toString())
                .inResponseTo(causedBy)
                .build();

		MessageEnvelope errorMessage = MessageEnvelope.of(header, errorOccured);
		messagingDispatcher.dispatch(errorMessage, ctx);
	}

	private <M extends Message> void doReduce(Reducer reducer, Plugin<M, ?, ?> plugin, MessageEnvelope messageEnvelope, MessageContext ctx) {		
		PluginContext<M> context = PluginContextFactory.createPluginContext(messageEnvelope, plugin, this::send , messagingDispatcher, scheduledExecutorService);
		reducer.reduce(plugin, messageEnvelope, ctx, context)
				.stream()
				.forEach(m -> messagingDispatcher.dispatch(m, ctx));
	}
	
	private MessageMapper<?> getMessageMapperOrThrow(String name) {
		return messageMapperRegistry.lookup(name)
				.orElseThrow(() -> new EngineException(String.format("message mapper '%s' not found", name), "002", null));
	}
		
	private Plugin<?, ?, ?> getPluginOrThrow(String plugin) {
		return pluginRegistry.lookup(plugin)
				.orElseThrow(() -> new EngineException(String.format("plugin '%s' not found", plugin), "001", null));
	}
	
	private void assertEngineStarted() {
		if (!started) {
			throw new EngineException("Engine not started", "000", null);
		}
	}
}
