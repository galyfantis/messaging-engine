package org.gal.messaging.engine.core.api;

import org.gal.messaging.engine.api.MessageContext;

public interface MessageDispatcher {
	
	void stop();
	
	void registerEngine(Engine engine);

	void dispatch(MessageEnvelope messageEnvelope, MessageContext ctx);
}
