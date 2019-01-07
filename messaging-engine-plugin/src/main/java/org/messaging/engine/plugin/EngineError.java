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
@JsonSerialize(as = ImmutableEngineError.class)
@JsonDeserialize(as = ImmutableEngineError.class, builder = ImmutableEngineError.Builder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface EngineError extends EngineMessage {
	@Value.Parameter
	String code();

	@Value.Parameter
	@Nullable
	String message();
	
	static EngineError of(String code, String message) {
		return ImmutableEngineError.of(code, message);
	}
}
