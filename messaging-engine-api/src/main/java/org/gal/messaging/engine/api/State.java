package org.gal.messaging.engine.api;

import java.time.OffsetDateTime;

import org.immutables.value.Value;

public interface State extends VersionTrait {
	
	@Value.Auxiliary
	OffsetDateTime since();
	
}
