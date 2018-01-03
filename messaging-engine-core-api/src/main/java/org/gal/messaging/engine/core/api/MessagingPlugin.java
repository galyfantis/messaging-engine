package org.gal.messaging.engine.core.api;

import java.util.List;

import org.gal.messaging.engine.api.MessageContext;

public interface MessagingPlugin<T> {
	
	void addMessageListener(IncomingMessageListener messageListener);
	
	void removeMessageListener(IncomingMessageListener messageListener);
	
	void receive(T message, MessageContext ctx);
	
	void addMessageListener(OutgoingMessageListener<T> messageListener);
	
	void removeMessageListener(OutgoingMessageListener<T> messageListener);
	
	void send(MessageEnvelope messageEnvelope, List<String> recipients);
	
	void error(ErrorMessage error, String inResponseTo, MessageContext ctx);
	
	void warn(WarningMessage warn, String inResponseTo, MessageContext ctx);
	
	void registerMessageTypeResolver(MessageTypeResolver messageTypeResolver);
}
