package org.gal.messaging.engine.core;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.gal.messaging.engine.api.KeyTrait;
import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.core.api.MessageDispatcher;
import org.gal.messaging.engine.core.api.MessageEnvelope;

class MessageDispatcherImpl implements MessageDispatcher {

	private final BiConsumer<MessageEnvelope, MessageContext> messageHandler;
	
	private final MultiExecutorService multiExecutorService;
	
	public MessageDispatcherImpl(MultiExecutorService multiExecutorService, BiConsumer<MessageEnvelope, MessageContext> messageHandler) {
		this.multiExecutorService = multiExecutorService;
		this.messageHandler = messageHandler;
	}
	
	@Override
	public void dispatch(MessageEnvelope messageEnvelope, MessageContext ctx) {
		String key = Optional.of(messageEnvelope.payload())
							.filter(KeyTrait.class::isInstance)
							.map(KeyTrait.class::cast)
							.map(KeyTrait::key)
							.orElse(messageEnvelope.header().plugin());
		
		multiExecutorService.submit(key, () -> messageHandler.accept(messageEnvelope, ctx));
	}
	
}
