package org.gal.messaging.engine.core.api;

public interface MessageMapper<T> {
	
	String name();
	
	MessageEnvelope read(T raw) throws EngineException;
	
	T write(MessageEnvelope messageEnvelope);
}
