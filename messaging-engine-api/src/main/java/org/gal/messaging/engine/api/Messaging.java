package org.gal.messaging.engine.api;

import java.util.List;

public interface Messaging<M extends Message> {
	
	void send(M message, List<String> recipients);
	
}
