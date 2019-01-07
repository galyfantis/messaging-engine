package org.gal.messaging.engine.api;

import java.util.Optional;

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
public interface ErrorOccuredMsg extends EngineMessage, InstanceTrait {
	@Value.Parameter
	ErrorLevel level();

	@Value.Parameter
	String code();

	@Value.Parameter
	@Nullable
	String message();
	
	@Value.Parameter
	@Nullable
	String causedByPlugin();
	
	@Value.Parameter
	@Nullable
	String causedByMessageUuid();
	
	@Override
	@Value.Default
	default String instance() {
		return Optional.ofNullable(causedByPlugin()).orElse("engine");
	}

	static ErrorOccuredMsg of(ErrorLevel level, String code, String message, String causedByPlugin, String causedByMessageUuid) {
		return ImmutableErrorOccuredMsg.of(level, code, message, causedByPlugin, causedByMessageUuid);
	}
	
	public static enum ErrorLevel {ERROR, WARNING};

}
