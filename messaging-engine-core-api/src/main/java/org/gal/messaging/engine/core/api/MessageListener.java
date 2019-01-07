package org.gal.messaging.engine.core.api;

import java.util.List;

public interface MessageListener<T> {
	
	void onMessage(T message, List<String> recipients);

}
