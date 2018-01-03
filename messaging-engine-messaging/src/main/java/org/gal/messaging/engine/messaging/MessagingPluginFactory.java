package org.gal.messaging.engine.messaging;

import org.gal.messaging.engine.core.api.MessageMapper;
import org.gal.messaging.engine.core.api.MessagingPlugin;

public class MessagingPluginFactory {
		
	public static <T> MessagingPlugin<T> createMessagingPlugin(MessageMapper<T> mapper) {
		return new DefaultMessagingPlugin<T>(mapper);
	}
}
