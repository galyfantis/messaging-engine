package org.gal.messaging.engine.core.api;

import org.gal.messaging.engine.api.MessageContext;

public interface MessageDispatcher {
	
	void dispatch(MessageEnvelope messageEnvelope, MessageContext ctx);
}
