package org.gal.messaging.engine.api;

public interface InstanceTrait extends KeyTrait {
	
	String instance();

	@Override
	default String key() {
		return instance();
	}
	
}
