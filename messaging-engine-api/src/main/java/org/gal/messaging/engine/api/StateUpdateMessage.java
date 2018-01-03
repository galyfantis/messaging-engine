package org.gal.messaging.engine.api;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(
		visibility = ImplementationVisibility.PACKAGE,
		builderVisibility = BuilderVisibility.SAME,
		depluralize = true
)
public interface StateUpdateMessage extends Message, InstanceTrait, VersionTrait {
	
	static StateUpdateMessage of(String instance, long version) {
		return ImmutableStateUpdateMessage.builder()
				.instance(instance)
				.version(version)
				.build();
	}
	
}
