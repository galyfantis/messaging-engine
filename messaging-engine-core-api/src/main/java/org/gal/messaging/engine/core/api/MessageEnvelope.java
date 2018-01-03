package org.gal.messaging.engine.core.api;

import org.gal.messaging.engine.api.Message;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(
		visibility = ImplementationVisibility.PACKAGE,
		builderVisibility = BuilderVisibility.SAME,
		depluralize = true
)
public interface MessageEnvelope {
	
	@Value.Parameter
	MessageHeader header();
	
	@Value.Parameter
	Message payload();
	
	static MessageEnvelope of(MessageHeader header, Message payload) {
		return ImmutableMessageEnvelope.of(header, payload);
	}
}
