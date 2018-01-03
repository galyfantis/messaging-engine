package org.gal.messaging.engine.messaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.core.api.EngineException;
import org.gal.messaging.engine.core.api.ErrorMessage;
import org.gal.messaging.engine.core.api.IncomingMessageListener;
import org.gal.messaging.engine.core.api.MessageEnvelope;
import org.gal.messaging.engine.core.api.MessageHeader;
import org.gal.messaging.engine.core.api.MessageMapper;
import org.gal.messaging.engine.core.api.MessageTypeResolver;
import org.gal.messaging.engine.core.api.MessagingPlugin;
import org.gal.messaging.engine.core.api.OutgoingMessageListener;
import org.gal.messaging.engine.core.api.WarningMessage;

class DefaultMessagingPlugin<T> implements MessagingPlugin<T> {
	
	private final List<IncomingMessageListener> incomingMessageListeners = new ArrayList<>();
	
	private final List<OutgoingMessageListener<T>> outgoingMessageListeners = new ArrayList<>();
	
	private final MessageMapper<T> mapper;

	private MessageTypeResolver messageTypeResolver;
	
	DefaultMessagingPlugin(MessageMapper<T> mapper) {
		this.mapper = mapper;
	}
	
	@Override
	public void addMessageListener(IncomingMessageListener messageListener) {
		incomingMessageListeners.add(messageListener);
	}	
	
	@Override
	public void removeMessageListener(IncomingMessageListener messageListener) {
		incomingMessageListeners.remove(messageListener);
	}

	@Override
	public void receive(T message, MessageContext ctx) {
		try {
			incomingMessageListeners.forEach(listener -> listener.onMessage(mapper.read(message, messageTypeResolver), ctx));
		} catch (EngineException e) {
			String errorMessage = getChainedMessages(e).stream().collect(Collectors.joining(" <- "));
			this.error(ErrorMessage.of(e.getCode(), errorMessage), e.getInResponseTo(), ctx);
		}
	}
	
	@Override
	public void send(MessageEnvelope messageEnvelope, List<String> recipients) {
		T raw = mapper.write(messageEnvelope);
		outgoingMessageListeners.forEach(listener -> listener.onMessage(raw, recipients));
	}
	
	@Override
	public void addMessageListener(OutgoingMessageListener<T> messageListener) {
		outgoingMessageListeners.add(messageListener);
	}

	@Override
	public void removeMessageListener(OutgoingMessageListener<T> messageListener) {
		outgoingMessageListeners.remove(messageListener);
	}
	
	@Override
	public void error(ErrorMessage error, String inResponseTo, MessageContext ctx) {
		MessageHeader header = MessageHeader.Builder()
				.plugin("engine")
				.type("error")
				.uuid(UUID.randomUUID().toString())
				.inResponseTo(inResponseTo)
				.build();
		
		this.send(MessageEnvelope.of(header, mapper.wrapError(error)), Arrays.asList(ctx.client()));
	}

	@Override
	public void warn(WarningMessage warn, String inResponseTo, MessageContext ctx) {
		MessageHeader header = MessageHeader.Builder()
				.plugin("engine")
				.type("warn")
				.uuid(UUID.randomUUID().toString())
				.inResponseTo(inResponseTo)
				.build();
		
		this.send(MessageEnvelope.of(header, mapper.wrapWarning(warn)), Arrays.asList(ctx.client()));
	}

	@Override
	public void registerMessageTypeResolver(MessageTypeResolver messageTypeResolver) {
		this.messageTypeResolver = messageTypeResolver;
	}
	
	private List<String> getChainedMessages(EngineException e) {
		List<String> messages = new ArrayList<>();
		messages.add(e.getMessage());
		Throwable ex = e;
		while (ex.getCause() != null) {
			ex = e.getCause();
			if (ex instanceof EngineException) {
				messages.add(ex.getMessage());
			}
		}
		return messages;
	}
	
}
