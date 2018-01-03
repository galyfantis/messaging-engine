package org.gal.messaging.engine.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.api.Plugin;
import org.gal.messaging.engine.api.PluginContext;
import org.gal.messaging.engine.core.api.Engine;
import org.gal.messaging.engine.core.api.EngineException;
import org.gal.messaging.engine.core.api.MessageDispatcher;
import org.gal.messaging.engine.core.api.MessageEnvelope;
import org.gal.messaging.engine.core.api.MessageTypeResolver;
import org.gal.messaging.engine.core.api.MessagingPlugin;
import org.gal.messaging.engine.core.api.Reducer;

class EngineImpl implements Engine, MessageTypeResolver {
	
	private final MessageDispatcher messagingDispatcher;
	
	private final MessagingPlugin<?> messagingPlugin;
	
	private final PluginRegistry pluginRegistry = new PluginRegistry();
	
	private final ScheduledExecutorService scheduledExecutorService;

	private boolean started = false;
	
	private List<Reducer> reducers = new ArrayList<>();
	
	EngineImpl(MessageDispatcher messagingDispatcher, MessagingPlugin<?> messagingPlugin,
			ScheduledExecutorService scheduledExecutorService) {
		this.messagingDispatcher = messagingDispatcher;
		this.messagingPlugin = messagingPlugin;
		this.scheduledExecutorService = scheduledExecutorService;
	}

	@Override
	public synchronized void start() {
		started = true;
	}
	
	@Override
	public synchronized void stop() {
		try {
			this.scheduledExecutorService.shutdown();
			this.messagingDispatcher.stop();			
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
	public void registerReducer(Reducer reducer) {
		this.reducers.add(reducer);
	}

	@Override
	public Class<? extends Message> resolve(String plugin, String type) {
		return Optional.of(getPluginOrThrow(plugin))
							.map(p -> p.messageClass(type))
							.orElseThrow(() -> new EngineException(String.format("No type '%s' could be found for plugin '%s'", type, plugin), "002", null));
	}
	
	@Override
	public void handle(MessageEnvelope messageEnvelope, MessageContext ctx) {
		assertEngineStarted();
		Plugin<?, ?, ?> plugin = getPluginOrThrow(messageEnvelope.header().plugin());
		
		reducers.stream()
				.filter(r -> r.isCompatible(plugin, messageEnvelope, ctx))
				.forEach(r -> doReduce(r, plugin, messageEnvelope, ctx));
	}
	
	private <M extends Message> void doReduce(Reducer reducer, Plugin<M, ?, ?> plugin, MessageEnvelope messageEnvelope, MessageContext ctx) {
		PluginContext<M> context = PluginContextFactory.createPluginContext(messageEnvelope, plugin, messagingPlugin, messagingDispatcher, scheduledExecutorService);
		reducer.reduce(plugin, messageEnvelope, ctx, context);
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
