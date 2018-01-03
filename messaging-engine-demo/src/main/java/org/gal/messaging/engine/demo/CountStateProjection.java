package org.gal.messaging.engine.demo;

import java.util.List;

import org.gal.messaging.engine.api.InstanceTrait;
import org.gal.messaging.engine.api.MessageContext;
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
@JsonSerialize(as = ImmutableCountStateProjection.class)
@JsonDeserialize(as = ImmutableCountStateProjection.class, builder = ImmutableCountStateProjection.Builder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface CountStateProjection extends DemoMessage, InstanceTrait {
	
	int count();
	
	List<String> participants();
	
	@Override
	default <T> T accept(DemoStateReducerVisitor<T> visitor, T state, MessageContext ctx) {
		return visitor.visit(state, this, ctx);
	}
	
	@Override
	default <T, E, C, M> List<M> accept(DemoSideEffectsVisitor<T, E, C, M> visitor, T state, T stateold, MessageContext ctx, C context) {
		return visitor.visit(this, state, stateold, ctx, context);
	}

	@Override
	default <T, E, C, M> List<M> accept(DemoSideEffectsVisitor<T, E, C, M> visitor, E error, MessageContext ctx, C context) {
		return visitor.visit(this, error, ctx, context);
	}
	
	static CountStateProjection from(DemoState state) {
		return ImmutableCountStateProjection.builder()
				.instance(state.instance())
				.count(state.count())
				.participants(state.participants())
				.build();
	}

}
