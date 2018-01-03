package org.gal.messaging.engine.core.api;

import org.gal.messaging.engine.api.Message;

public interface MessageMapper<T> {
	
	MessageEnvelope read(T raw, MessageTypeResolver messageTypeResolver) throws EngineException;
	
	T write(MessageEnvelope messageEnvelope);
	
	Message wrapError(ErrorMessage error);
	
	Message wrapWarning(WarningMessage warn);
}
