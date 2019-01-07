package org.gal.messaging.engine.dist;

import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.api.Plugin;
import org.gal.messaging.engine.core.AbstractMessageMapper;
import org.gal.messaging.engine.core.api.MessageEnvelope;
import org.gal.messaging.engine.core.api.MessageHeader;
import org.gal.messaging.engine.core.api.MessagePluginResolver;

public class PassThroughMessageMapper extends AbstractMessageMapper<MessageEnvelope> {

	private final MessagePluginResolver pluginResolver;
	
	public PassThroughMessageMapper(MessagePluginResolver pluginResolver) {
		super(pluginResolver);
		this.pluginResolver = pluginResolver;
	}

	@Override
	public String name() {
		return "PassThrough";
	}

	@Override
	protected String plugin(MessageEnvelope raw) {
		return raw.header().plugin();
	}

	@Override
	protected String type(MessageEnvelope raw) {
		return raw.header().type();
	}

	@Override
	protected String uuid(MessageEnvelope raw) {
		return raw.header().uuid();
	}

	@Override
	protected String inResponseTo(MessageEnvelope raw) {
		return raw.header().inResponseTo();
	}

	@Override
	protected String payloadAsString(MessageEnvelope raw) {
		Plugin<?, ?, ?> plugin = pluginResolver.resolve(raw.header().plugin());
		String serialized = serialize(plugin, raw.payload());
		return serialized;
	}

	@Override
	protected MessageEnvelope doWrite(MessageHeader header, String payloadAsString) {
		Plugin<?, ?, ?> plugin = pluginResolver.resolve(header.plugin());
		Message msg = deserialize(plugin, header.type(), payloadAsString);
		return MessageEnvelope.of(header, msg);
	}

	@Override
	protected MessageEnvelope doWrite(MessageHeader header, Message message) {
		return MessageEnvelope.of(header, message);
	}
	
}
