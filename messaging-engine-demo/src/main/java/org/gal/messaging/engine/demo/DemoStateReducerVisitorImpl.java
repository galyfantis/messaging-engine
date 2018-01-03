package org.gal.messaging.engine.demo;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.demo.ImmutableDemoState.Builder;

public class DemoStateReducerVisitorImpl implements DemoStateReducerVisitor<DemoState> {
	
	@Override
	public DemoState visit(DemoState state, DemoMsgCreateInstance message, MessageContext ctx) {
		return fallback(state, message);
	}
	
	@Override
	public DemoState visit(DemoState state, DemoMsgCreate message, MessageContext ctx) {
		if (state != null) {
			throw new IllegalArgumentException("instance with id '" + message.instance() + "' already exists");
		}
		
		return stateBuilder(state, message.instance(), ctx).build();
	}

	@Override
	public DemoState visit(DemoState state, DemoMsg1 message, MessageContext ctx) {
		if (state == null) {
			throw new IllegalArgumentException("no instance with id '" + message.instance() + "' found");
		}
		if (message.incr() < 0) {
			throw new IllegalArgumentException("Invalid increment " + message.incr());
		}
		
		Builder builder = stateBuilder(state, message.instance(), ctx);

		int currentCount = Optional.ofNullable(state)
						.map(DemoState::count)
						.orElse(0);
		
		builder.count(currentCount + message.incr());
		
		return builder.build();
	}

	@Override
	public DemoState visit(DemoState state, DemoMsg2 message, MessageContext ctx) {
		if (state == null) {
			throw new IllegalArgumentException("no instance with id '" + message.instance() + "' found");
		}
		if (message.decr() < 0) {
			throw new IllegalArgumentException("Invalid decrement " + message.decr());
		}
		
		Builder builder = stateBuilder(state, message.instance(), ctx);

		int currentCount = Optional.ofNullable(state)
						.map(DemoState::count)
						.orElse(0);
		
		builder.count(currentCount - message.decr());
		
		return builder.build();
	}

	@Override
	public DemoState visit(DemoState state, CountStateProjection message, MessageContext ctx) {
		return fallback(state, message);
	}
	
	@Override
	public DemoState visit(DemoState state, DemoError message, MessageContext ctx) {
		return fallback(state, message);
	}
	
	@Override
	public DemoState visit(DemoState state, DemoClientLeaveMsg message, MessageContext ctx) {
		if (state == null) {
			throw new IllegalArgumentException("no instance with id '" + message.instance() + "' found");
		}
		Builder builder = stateBuilder(state, message.instance(), ctx);
		
		Set<String> participants = new HashSet<>();
		participants.addAll(state.participants());
		participants.remove(ctx.client());
		
		builder.participants(participants);
		
		return builder.build();
	}
	
	@Override
	public DemoState visit(DemoState state, CountGlobalStateProjection message, MessageContext ctx) {
		return fallback(state, message);
	}

	private Builder stateBuilder(DemoState state, String instance, MessageContext ctx) {
		return Optional.ofNullable(state)
				.map(s -> DemoState.Builder().from(s))
				.map(sb -> sb.version(state.version() + 1))
				.orElse(DemoState.Builder().instance(instance).version(0))
				.addParticipant(ctx.client())
				.since(OffsetDateTime.now());
	}
	
	private DemoState fallback(DemoState state, DemoMessage message) {
		return state;
	}

}
