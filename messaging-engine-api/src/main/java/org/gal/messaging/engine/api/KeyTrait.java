package org.gal.messaging.engine.api;

import org.immutables.value.Value;

public interface KeyTrait {
	
	@Value.Auxiliary
	String key();

}
