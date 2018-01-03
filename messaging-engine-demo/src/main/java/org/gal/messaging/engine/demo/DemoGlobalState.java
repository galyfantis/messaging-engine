package org.gal.messaging.engine.demo;

import java.util.Set;

import org.gal.messaging.engine.api.GlobalState;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(
		visibility = ImplementationVisibility.PACKAGE,
		builderVisibility = BuilderVisibility.SAME,
		depluralize = true
)
public interface DemoGlobalState extends GlobalState {
	
	int numberOfParticipants();
	
	Set<String> participants();
	
	int numberOfInstances();
	
	Set<String> instances();
	
	static ImmutableDemoGlobalState.Builder Builder() {
		return ImmutableDemoGlobalState.builder();
	}
}
