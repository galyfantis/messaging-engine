package org.messaging.engine.plugin;

import javax.annotation.Nullable;

import org.gal.messaging.engine.api.EngineMessage;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(
		visibility = ImplementationVisibility.PACKAGE,
		builderVisibility = BuilderVisibility.SAME,
		depluralize = true
)
@JsonSerialize(as = ImmutableEngineWarning.class)
@JsonDeserialize(as = ImmutableEngineWarning.class, builder = ImmutableEngineWarning.Builder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface EngineWarning extends EngineMessage {
	@Value.Parameter
	String code();

	@Value.Parameter
	@Nullable
	String message();
	
	static EngineWarning of(String code, String message) {
		return ImmutableEngineWarning.of(code, message);
	}
}
