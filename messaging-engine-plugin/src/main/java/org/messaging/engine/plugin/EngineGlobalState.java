package org.messaging.engine.plugin;

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
public interface EngineGlobalState extends GlobalState {

	static ImmutableEngineGlobalState.Builder Builder() {
		return ImmutableEngineGlobalState.builder();
	}
}
