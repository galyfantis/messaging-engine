package org.gal.messaging.engine.demo;

import org.gal.messaging.engine.api.MessageContext;

public interface DemoStateReducerVisitor<S> {
	
	S visit(S state, DemoMsgCreateInstance message, MessageContext ctx);
	
	S visit(S state, DemoMsgCreate message, MessageContext ctx);
	
	S visit(S state, DemoMsg1 message, MessageContext ctx);
	
	S visit(S state, DemoMsg2 message, MessageContext ctx);
	
	S visit(S state, CountStateProjection message, MessageContext ctx);
	
	S visit(S state, DemoError message, MessageContext ctx);
	
	S visit(S state, DemoClientLeaveMsg message, MessageContext ctx);
	
	S visit(S state, CountGlobalStateProjection message, MessageContext ctx);

}
