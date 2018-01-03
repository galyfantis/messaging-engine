package org.gal.messaging.engine.demo;

import java.util.Set;

import org.gal.messaging.engine.api.InstanceState;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(
		visibility = ImplementationVisibility.PACKAGE,
		builderVisibility = BuilderVisibility.SAME,
		depluralize = true
)
public interface DemoState extends InstanceState {
	
	@Value.Default
	default int count() {
		return 0;
	}
	
	Set<String> participants();
	
	static ImmutableDemoState.Builder Builder() {
		return ImmutableDemoState.builder();
	}
}
