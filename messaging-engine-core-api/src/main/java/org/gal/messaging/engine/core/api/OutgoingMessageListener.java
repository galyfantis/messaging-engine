package org.gal.messaging.engine.core.api;

import java.util.List;

public interface OutgoingMessageListener<T> {
	
	void onMessage(T message, List<String> recipients);

}
