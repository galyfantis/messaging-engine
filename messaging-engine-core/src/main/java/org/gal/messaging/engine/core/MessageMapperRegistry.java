package org.gal.messaging.engine.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.gal.messaging.engine.core.api.MessageMapper;

class MessageMapperRegistry {
	
	private Map<String, MessageMapper<?>> mappers = new HashMap<>();
	
	void register(MessageMapper<?> mapper) {
		mappers.put(mapper.name(), mapper);
	}
	
	void unregister(MessageMapper<?> mapper) {
		mappers.remove(mapper.name());
	}

	Optional<MessageMapper<?>> lookup(String mapper) {
		return Optional.ofNullable(mappers.get(mapper));
	}

}
