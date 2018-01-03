package org.gal.messaging.engine.core;

import java.util.Optional;

import org.gal.messaging.engine.api.KeyTrait;
import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.core.api.Engine;
import org.gal.messaging.engine.core.api.EngineException;
import org.gal.messaging.engine.core.api.ErrorMessage;
import org.gal.messaging.engine.core.api.IncomingMessageListener;
import org.gal.messaging.engine.core.api.MessageDispatcher;
import org.gal.messaging.engine.core.api.MessageEnvelope;
import org.gal.messaging.engine.core.api.MessagingPlugin;

class MessageDispatcherImpl implements MessageDispatcher {

	private Engine engine;
	
	private final MultiExecutorService multiExecutorService;
	
	private final MessagingPlugin<?> messagingPlugin;
	
	public MessageDispatcherImpl(MessagingPlugin<?> messagingPlugin, MultiExecutorService multiExecutorService) {
		IncomingMessageListener messageListener = (messageEnvelope, ctx) -> dispatch(messageEnvelope, ctx);
		messagingPlugin.addMessageListener(messageListener);
		this.messagingPlugin = messagingPlugin;
		this.multiExecutorService = multiExecutorService;
	}
	
	@Override
	public void stop() {
		multiExecutorService.shutdown();
	}

	@Override
	public void registerEngine(Engine engine) {
		this.engine = engine;
	}

	@Override
	public void dispatch(MessageEnvelope messageEnvelope, MessageContext ctx) {
		String key = Optional.of(messageEnvelope.payload())
							.filter(KeyTrait.class::isInstance)
							.map(KeyTrait.class::cast)
							.map(KeyTrait::key)
							.orElse(messageEnvelope.header().plugin());
		
		multiExecutorService.submit(key, () -> {
			try {
				engine.handle(messageEnvelope, ctx);
			} catch (EngineException e) {
				messagingPlugin.error(ErrorMessage.of(e.getCode(), e.getMessage()), messageEnvelope.header().uuid(), ctx);
				e.printStackTrace();
			} catch (Exception e) {
				messagingPlugin.error(ErrorMessage.of("UKN", e.getMessage()), messageEnvelope.header().uuid(), ctx);
				e.printStackTrace();
			}
		});
	}
	
}
