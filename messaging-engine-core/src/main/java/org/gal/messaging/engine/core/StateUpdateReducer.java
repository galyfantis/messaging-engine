package org.gal.messaging.engine.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gal.messaging.engine.api.GlobalState;
import org.gal.messaging.engine.api.InstanceState;
import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.api.Plugin;
import org.gal.messaging.engine.api.PluginContext;
import org.gal.messaging.engine.api.StateUpdateMessage;
import org.gal.messaging.engine.core.api.MessageEnvelope;
import org.gal.messaging.engine.core.api.MessageHeader;
import org.gal.messaging.engine.core.api.Reducer;

public class StateUpdateReducer implements Reducer {
	
	private final StateStore<String, GlobalState> globalStateStore;
	
	private final StateStore<InstanceStateStoreKey, InstanceState> stateStore;
	
	StateUpdateReducer(
			StateStore<String, GlobalState> globalStateStore,
			StateStore<InstanceStateStoreKey, InstanceState> stateStore) {
		this.globalStateStore = globalStateStore;
		this.stateStore = stateStore;
	}

	@Override
	public boolean isCompatible(Plugin<?, ?, ?> plugin, MessageEnvelope messageEnvelope, MessageContext ctx) {
		return messageEnvelope.payload() instanceof StateUpdateMessage;
	}

	@Override
	public <M extends Message, S extends InstanceState, G extends GlobalState> Collection<MessageEnvelope> reduce(Plugin<M, S, G> plugin,
			MessageEnvelope messageEnvelope, MessageContext ctx, PluginContext<M> context) {
		
		List<MessageEnvelope> feedback = new ArrayList<>();
		
		StateUpdateMessage stateUpdateMessage = StateUpdateMessage.class.cast(messageEnvelope.payload());
		InstanceStateStoreKey instanceStateStoreKey = InstanceStateStoreKey.of(plugin.name(), stateUpdateMessage.instance());
		S currentState = stateStore.lookup(instanceStateStoreKey)
									.map(state -> plugin.stateClass().cast(state))
									.orElse(null);
		
		// return if stateUpdateMessage is obsolete
		if (currentState.version() > stateUpdateMessage.version()) {
			return feedback;
		}

		G currentGlobalState = globalStateStore.lookup(plugin.name())
				.map(state -> plugin.globalStateClass().cast(state))
				.orElse(null);
		
		G newGlobalState = plugin.apply(currentState, currentGlobalState, ctx);
		
		Optional.ofNullable(newGlobalState)
					.filter(state -> !state.equals(currentGlobalState))
					.ifPresent(state -> {
						globalStateStore.set(plugin.name(), newGlobalState);
					});
		
		plugin.sideEffects(currentState, currentGlobalState, newGlobalState, ctx, context)
				.stream()
				.map(msg -> {
					MessageHeader h = MessageHeader.Builder()
													.plugin(plugin.name())
													.type(plugin.messageType(msg))
													.uuid(UUID.randomUUID().toString())
													.inResponseTo(messageEnvelope.header().uuid())
													.build();
					return MessageEnvelope.of(h, msg);
				}) 
				.forEach(feedback::add);
		
		return feedback;
		
	}

}
