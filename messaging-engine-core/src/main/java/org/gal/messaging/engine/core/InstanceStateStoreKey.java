package org.gal.messaging.engine.core;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(
		visibility = ImplementationVisibility.PACKAGE,
		builderVisibility = BuilderVisibility.SAME,
		depluralize = true
)
interface InstanceStateStoreKey {
	
	@Value.Parameter
	String plugin();
	
	@Value.Parameter
	String instance();
	
	static InstanceStateStoreKey of(String plugin, String instance) {
		return ImmutableInstanceStateStoreKey.of(plugin, instance);
	}

}
