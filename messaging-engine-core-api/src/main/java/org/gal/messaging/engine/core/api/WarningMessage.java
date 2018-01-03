package org.gal.messaging.engine.core.api;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(
		visibility = ImplementationVisibility.PACKAGE,
		builderVisibility = BuilderVisibility.SAME,
		depluralize = true
)
public interface WarningMessage extends EngineMessage {
	
	@Value.Parameter
	String code();
	
	@Value.Parameter
	@Nullable
	String message();
	
	static WarningMessage of(String code, String message) {
		return ImmutableWarningMessage.of(code, message);
	}

}
