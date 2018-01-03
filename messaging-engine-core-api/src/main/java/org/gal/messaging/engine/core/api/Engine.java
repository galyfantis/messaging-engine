package org.gal.messaging.engine.core.api;

import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.api.Plugin;

public interface Engine {
	
	void start();
	
	void stop();
	
	void registerPlugin(Plugin<?, ?, ?> plugin);
	
	void unregisterPlugin(Plugin<?, ?, ?> plugin);
	
	void registerReducer(Reducer reducer);
	
	void handle(MessageEnvelope messageEnvelope, MessageContext ctx);
}
