package org.gal.messaging.engine.demo;

import java.util.List;

import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.api.MessageContext;

public interface DemoMessage extends Message {

	<T> T accept(DemoStateReducerVisitor<T> visitor, T state, MessageContext ctx);
	
	<T, E, C, M> List<M> accept(DemoSideEffectsVisitor<T, E, C, M> visitor, T state, T stateold, MessageContext ctx, C context);

	<T, E, C, M> List<M> accept(DemoSideEffectsVisitor<T, E, C, M> visitor, E error, MessageContext ctx, C context);
}
