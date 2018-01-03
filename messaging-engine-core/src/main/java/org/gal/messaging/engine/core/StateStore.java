package org.gal.messaging.engine.core;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.gal.messaging.engine.api.State;

class StateStore<K, V extends State> {
	private Map<K, V> store = new ConcurrentHashMap<>();
	
	void set(K key, V state) {
		Optional.ofNullable(state)
			.ifPresent(st -> store.put(key, st));
	}

	Optional<V> lookup(K key) {
		return Optional.ofNullable(store.get(key));
	}
}
