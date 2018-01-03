package org.gal.messaging.engine.core.api;

import org.gal.messaging.engine.api.MessageContext;

public interface IncomingMessageListener {
	
	void onMessage(MessageEnvelope messageEnvelope, MessageContext ctx);

}
