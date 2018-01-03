package org.gal.messaging.engine.api;

import org.immutables.value.Value;

public interface VersionTrait {
	
	@Value.Auxiliary
	long version();
}
