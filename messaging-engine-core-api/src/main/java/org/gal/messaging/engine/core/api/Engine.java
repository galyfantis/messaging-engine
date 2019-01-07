package org.gal.messaging.engine.core.api;

import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.api.Plugin;

public interface Engine {
	
	void start();
	
	void stop();
	
	void registerPlugin(Plugin<?, ?, ?> plugin);
	
	void unregisterPlugin(Plugin<?, ?, ?> plugin);
	
	void registerMessageMapper(MessageMapper<?> messageMapper);
	
	void unregisterMessageMapper(MessageMapper<?> messageMapper);
	
	void registerReducer(Reducer reducer);
	
	<T> void handle(T message, String format, MessageContext ctx);
	
	<T> void registerListener(MessageListener<T> listener, String format);
	
	<T> void unregisterListener(MessageListener<T> listener, String format);
	
	MessagePluginResolver pluginResolver();
}
