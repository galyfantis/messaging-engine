package org.gal.messaging.engine.api;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(
		visibility = ImplementationVisibility.PACKAGE,
		builderVisibility = BuilderVisibility.SAME,
		depluralize = true
)
public interface MessageContext {
	
	@Value.Parameter
	String client();
	
	@Value.Parameter
	String username();
	
	static MessageContext of(String client, String username) {
		return ImmutableMessageContext.of(client, username);
	}
}
