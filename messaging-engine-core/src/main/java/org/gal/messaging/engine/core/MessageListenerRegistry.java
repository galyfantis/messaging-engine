package org.gal.messaging.engine.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gal.messaging.engine.core.api.MessageListener;

class MessageListenerRegistry {
	
	private Map<String, List<MessageListener<?>>> listenersByFormat = new HashMap<>();
	
	void register(MessageListener<?> listener, String format) {
		List<MessageListener<?>> listeners = listenersByFormat.get(format);
		if (listeners == null) {
			listeners = new ArrayList<>();
			listenersByFormat.put(format, listeners);
		}
		listeners.add(listener);
	}
	
	void unregister(MessageListener<?> listener, String format) {
		List<MessageListener<?>> listeners = listenersByFormat.get(format);
		if (listeners == null) {
			return;
		}
		listeners.remove(listener);
	}
	
	List<String> formats() {
		return new ArrayList<>(listenersByFormat.keySet());
	}
	
	List<MessageListener<?>> listenersByFormat(String format) {
		return new ArrayList<>(listenersByFormat.getOrDefault(format, Collections.emptyList()));
	}

}
