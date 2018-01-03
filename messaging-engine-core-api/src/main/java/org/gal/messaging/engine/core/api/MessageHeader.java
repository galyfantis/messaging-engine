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
public interface MessageHeader {

	String plugin();
	
	String type();

	String uuid();
	
	@Nullable
	String inResponseTo();
	
	static ImmutableMessageHeader.Builder Builder() {
		return ImmutableMessageHeader.builder();
	}
	
}
