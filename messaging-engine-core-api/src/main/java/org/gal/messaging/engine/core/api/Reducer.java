package org.gal.messaging.engine.core.api;

import org.gal.messaging.engine.api.GlobalState;
import org.gal.messaging.engine.api.InstanceState;
import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.api.Plugin;
import org.gal.messaging.engine.api.PluginContext;

public interface Reducer {
	
	boolean isCompatible(Plugin<?, ?, ?> plugin, MessageEnvelope messageEnvelope, MessageContext ctx);
	
	<M extends Message, S extends InstanceState, G extends GlobalState> void reduce(Plugin<M, S, G> plugin, MessageEnvelope messageEnvelope, MessageContext ctx, PluginContext<M> context);

}
