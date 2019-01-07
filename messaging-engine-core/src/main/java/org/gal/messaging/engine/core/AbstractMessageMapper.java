package org.gal.messaging.engine.core;

import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.api.Plugin;
import org.gal.messaging.engine.core.api.EngineException;
import org.gal.messaging.engine.core.api.MessageEnvelope;
import org.gal.messaging.engine.core.api.MessageHeader;
import org.gal.messaging.engine.core.api.MessageMapper;
import org.gal.messaging.engine.core.api.MessagePluginResolver;

public abstract class AbstractMessageMapper<T> implements MessageMapper<T> {
	
	private final MessagePluginResolver pluginResolver;
	
	public AbstractMessageMapper(MessagePluginResolver pluginResolver) {
		this.pluginResolver = pluginResolver;
	}

	@Override
	public MessageEnvelope read(T raw) throws EngineException {
		String plugin = plugin(raw);
		String type = type(raw);
		String uuid = uuid(raw);
		String inResponseTo = inResponseTo(raw);
		String payloadAsString = payloadAsString(raw);
		
		try {
			MessageHeader header = MessageHeader.Builder()
										.plugin(plugin)
										.type(type)
										.uuid(uuid)
										.inResponseTo(inResponseTo)
										.build();
			
			Message msg = payload(header.plugin(), header.type(), payloadAsString);
			
			return MessageEnvelope.of(header, msg);
		} catch (EngineException e) {
			throw new EngineException("Error converting raw message to MessageEnvelope", e.getCode(), uuid, e);
		} catch (Exception e) {
			throw new EngineException("Error converting raw message to MessageEnvelope", "005", uuid, e);
		}
	}
	
	protected abstract String plugin(T raw);
	
	protected abstract String type(T raw);
	
	protected abstract String uuid(T raw);
	
	protected abstract String inResponseTo(T raw);
	
	protected abstract String payloadAsString(T raw);
	
	private Message payload(String plugin, String messageType, String payloadAsString) {
		Plugin<?, ?, ?> messagePlugin = pluginResolver.resolve(plugin);
		return deserialize(messagePlugin, messageType, payloadAsString);
	}
	
	protected static <M extends Message> M deserialize(Plugin<M, ?, ?> plugin, String messageType, String messagePayload) {
		Class<? extends M> clazz = plugin.messageClass(messageType);
		if (clazz == null) {
			throw new EngineException(
					String.format("Could not find registered message type '%s' provided by plugin '%s'", messageType,
							plugin.name()),
					"006", null);
		}
		return plugin.deserialize(messagePayload, clazz);
	}

	@Override
	public T write(MessageEnvelope messageEnvelope) {
		// TODO: should probably use a plugin for this purpose...
		if ("engine".equals(messageEnvelope.header().plugin())) {
			return doWrite(messageEnvelope.header(), messageEnvelope.payload());
		}
		
		Plugin<?, ?, ?> messagePlugin = pluginResolver.resolve(messageEnvelope.header().plugin());
		String payloadAsString = serialize(messagePlugin, messageEnvelope.payload());
		return doWrite(messageEnvelope.header(), payloadAsString);
	}
	
	protected static <M extends Message> String serialize(Plugin<M, ?, ?> plugin, Message message) {
		return plugin.serialize((M)message);
	}
	
	protected abstract T doWrite(MessageHeader header, String payloadAsString);
	
	protected abstract T doWrite(MessageHeader header, Message message);
	
}
