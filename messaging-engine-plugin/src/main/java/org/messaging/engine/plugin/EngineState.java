package org.messaging.engine.plugin;

import java.time.OffsetDateTime;
import java.util.Optional;

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
public interface EngineState extends InstanceState {
	int errorCount();
	
	int warnCount();
	
	static EngineState increaseErrors(String instance, EngineState state) {
		int count = state != null ? state.errorCount() : 0;
		return Optional.ofNullable(state)
						.map(ImmutableEngineState.builder()::from)
						.orElseGet(() -> initial(instance))
						.errorCount(count + 1)
						.since(OffsetDateTime.now())
						.build();
	}
	
	static EngineState increaseWarnings(String instance, EngineState state) {
		int count = state != null ? state.warnCount(): 0;
		return Optional.ofNullable(state)
				.map(ImmutableEngineState.builder()::from)
				.orElseGet(() -> initial(instance))
				.warnCount(count + 1)
				.since(OffsetDateTime.now())
				.build();
	}
	
	static ImmutableEngineState.Builder initial(String instance) {
		return ImmutableEngineState.builder()
									.instance(instance)
									.errorCount(0)
									.warnCount(0)
									.version(0);
	}
}
