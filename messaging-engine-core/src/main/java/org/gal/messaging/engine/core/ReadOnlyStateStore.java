package org.gal.messaging.engine.core;

import java.util.Optional;

import org.gal.messaging.engine.api.State;

class ReadOnlyStateStore<K, V extends State> extends StateStore<K, V> {
	
	private final StateStore<K, V> stateSore;
	
	private ReadOnlyStateStore(StateStore<K, V> stateSore) {
		this.stateSore = stateSore;
	}

	@Override
	void set(K key, V state) {
		throw new UnsupportedOperationException("Not allowed to 'set' a new state to a ReadOnlyStateStore");
	}

	@Override
	Optional<V> lookup(K key) {
		return stateSore.lookup(key);
	}
	
	static <K, V extends State> StateStore<K, V> of(StateStore<K, V> stateStore) {
		return new ReadOnlyStateStore<>(stateStore);
	}
	
}
