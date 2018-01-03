package org.gal.messaging.engine.demo;

import java.util.List;

import org.gal.messaging.engine.api.MessageContext;

public interface DemoSideEffectsVisitor<T, E, C, M> {
	
	List<M> visit(DemoMsgCreateInstance message, T state, T stateold, MessageContext ctx, C context);
	
	List<M> visit(DemoMsgCreateInstance message, E error, MessageContext ctx, C context);
	
	List<M> visit(DemoMsgCreate message, T state, T stateold, MessageContext ctx, C context);
	
	List<M> visit(DemoMsgCreate message, E error, MessageContext ctx, C context);
	
	List<M> visit(DemoMsg1 message, T state, T stateold, MessageContext ctx, C context);

	List<M> visit(DemoMsg1 message, E error, MessageContext ctx, C context);
	
	List<M> visit(DemoMsg2 message, T state, T stateold, MessageContext ctx, C context);

	List<M> visit(DemoMsg2 message, E error, MessageContext ctx, C context);
	
	List<M> visit(CountStateProjection message, T state, T stateold, MessageContext ctx, C context);

	List<M> visit(CountStateProjection message, E error, MessageContext ctx, C context);
	
	List<M> visit(DemoError message, T state, T stateold, MessageContext ctx, C context);

	List<M> visit(DemoError message, E error, MessageContext ctx, C context);
	
	List<M> visit(DemoClientLeaveMsg message, T state, T stateold, MessageContext ctx, C context);

	List<M> visit(DemoClientLeaveMsg message, E error, MessageContext ctx, C context);
	
	List<M> visit(CountGlobalStateProjection message, T state, T stateold, MessageContext ctx, C context);

	List<M> visit(CountGlobalStateProjection message, E error, MessageContext ctx, C context);
}
