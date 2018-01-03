package org.gal.messaging.engine.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.api.PluginContext;

public class DemoSideEffectsVisitorImpl implements DemoSideEffectsVisitor<DemoState, Exception, PluginContext<DemoMessage>, DemoMessage> {
	
	@Override
	public List<DemoMessage> visit(DemoMsgCreateInstance message, DemoState state, DemoState stateold,
			MessageContext ctx, PluginContext<DemoMessage> context) {
		
		DemoMsgCreate createInstance = DemoMsgCreate.Builder()
				.instance(UUID.randomUUID().toString())
				.build();

		return Arrays.asList(createInstance);
	}
	
	@Override
	public List<DemoMessage> visit(DemoMsgCreateInstance message, Exception error, MessageContext ctx,
			PluginContext<DemoMessage> context) {
		return fallback(message, error, ctx, context);
	}
	
	@Override
	public List<DemoMessage> visit(DemoMsgCreate message, DemoState state, DemoState stateold, MessageContext ctx,
			PluginContext<DemoMessage> context) {
		CountStateProjection countMsg = CountStateProjection.from(state);
		context.messaging().send(countMsg, new ArrayList<>(state.participants()));
		
		return fallback(message, state, stateold, ctx, context);
	}

	@Override
	public List<DemoMessage> visit(DemoMsgCreate message, Exception error, MessageContext ctx,
			PluginContext<DemoMessage> context) {
		return fallback(message, error, ctx, context);
	}

	@Override
	public List<DemoMessage> visit(DemoMsg1 message, DemoState state, DemoState stateold, MessageContext ctx, PluginContext<DemoMessage> context) {
		CountStateProjection countMsg = CountStateProjection.from(state);
		context.messaging().send(countMsg, new ArrayList<>(state.participants()));
		
		return fallback(message, state, stateold, ctx, context);
	}

	@Override
	public List<DemoMessage> visit(DemoMsg1 message, Exception error, MessageContext ctx, PluginContext<DemoMessage> context) {
		return fallback(message, error, ctx, context);
	}

	@Override
	public List<DemoMessage> visit(DemoMsg2 message, DemoState state, DemoState stateold, MessageContext ctx, PluginContext<DemoMessage> context) {
		CountStateProjection countMsg = CountStateProjection.from(state);
		context.messaging().send(countMsg, new ArrayList<>(state.participants()));
		
		DemoMsg1 mWithDelay = DemoMsg1.Builder().instance(message.instance()).incr(5).build();
		
		IntStream.rangeClosed(1, 10)
			.forEach(i -> context.scheduling().scheduleDispatch(mWithDelay, ctx, i*1, TimeUnit.SECONDS));
		
		List<DemoMessage> effects = new ArrayList<>();
		DemoMsg1 m = DemoMsg1.Builder().instance(message.instance()).incr(1).build();
		effects.add(m);
		return effects;
	}

	@Override
	public List<DemoMessage> visit(DemoMsg2 message, Exception error, MessageContext ctx, PluginContext<DemoMessage> context) {
		return fallback(message, error, ctx, context);
	}

	@Override
	public List<DemoMessage> visit(CountStateProjection message, DemoState state, DemoState stateold,
			MessageContext ctx, PluginContext<DemoMessage> context) {
		return fallback(message, state, stateold, ctx, context);
	}

	@Override
	public List<DemoMessage> visit(CountStateProjection message, Exception error, MessageContext ctx, PluginContext<DemoMessage> context) {
		return fallback(message, error, ctx, context);
	}
	
	@Override
	public List<DemoMessage> visit(DemoError message, DemoState state, DemoState stateold, MessageContext ctx, PluginContext<DemoMessage> context) {
		return fallback(message, state, stateold, ctx, context);
	}

	@Override
	public List<DemoMessage> visit(DemoError message, Exception error, MessageContext ctx, PluginContext<DemoMessage> context) {
		return fallback(message, error, ctx, context);
	}
	
	@Override
	public List<DemoMessage> visit(DemoClientLeaveMsg message, DemoState state, DemoState stateold,
			MessageContext ctx, PluginContext<DemoMessage> context) {
		CountStateProjection countMsg = CountStateProjection.from(state);
		context.messaging().send(countMsg, new ArrayList<>(state.participants()));
		
		return fallback(message, state, stateold, ctx, context);
	}

	@Override
	public List<DemoMessage> visit(DemoClientLeaveMsg message, Exception error, MessageContext ctx, PluginContext<DemoMessage> context) {
		return fallback(message, error, ctx, context);
	}
	
	@Override
	public List<DemoMessage> visit(CountGlobalStateProjection message, DemoState state, DemoState stateold,
			MessageContext ctx, PluginContext<DemoMessage> context) {
		return fallback(message, state, stateold, ctx, context);
	}

	@Override
	public List<DemoMessage> visit(CountGlobalStateProjection message, Exception error, MessageContext ctx,
			PluginContext<DemoMessage> context) {
		return fallback(message, error, ctx, context);
	}

	private List<DemoMessage> fallback(DemoMessage message, DemoState state, DemoState stateold,
			MessageContext ctx, PluginContext<DemoMessage> context) {
		return Collections.emptyList();
	}
	
	private List<DemoMessage> fallback(DemoMessage message, Exception error, MessageContext ctx, PluginContext<DemoMessage> context) {
		return Collections.emptyList();
	}

}
