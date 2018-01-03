package org.gal.messaging.engine.core.api;

import org.gal.messaging.engine.api.Message;

public interface MessageTypeResolver {
	
	Class<? extends Message> resolve(String plugin, String type);

}
