package org.gal.messaging.engine.api;

import java.util.List;

public interface Plugin<M extends Message, S extends InstanceState, G extends GlobalState> {
	
	String name();
	
	Class<? extends M> messageClass(String type);
	
	String messageType(M message);
	
	Class<S> stateClass();
	
	Class<G> globalStateClass();
	
	S apply(M message, S state, MessageContext ctx);
	
	G apply(S currentState, G currentGlobalState, MessageContext ctx);
	
	List<M> sideEffects(M message, S previousState, S currentState, MessageContext ctx, PluginContext<M> context);
	
	List<M> sideEffects(M message, S state, MessageContext ctx, Exception exception, PluginContext<M> context);
	
	List<M> sideEffects(S currentState, G previousGlobalState, G currentGlobalState, MessageContext ctx, PluginContext<M> context);
	
	M deserialize(String messageAsString, Class<? extends M> clazz);
	
	String serialize(M message);
	
}
