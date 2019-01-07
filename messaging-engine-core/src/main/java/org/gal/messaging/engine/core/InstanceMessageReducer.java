package org.gal.messaging.engine.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gal.messaging.engine.api.GlobalState;
import org.gal.messaging.engine.api.InstanceState;
import org.gal.messaging.engine.api.InstanceTrait;
import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.api.Plugin;
import org.gal.messaging.engine.api.PluginContext;
import org.gal.messaging.engine.api.StateUpdateMessage;
import org.gal.messaging.engine.core.api.MessageEnvelope;
import org.gal.messaging.engine.core.api.MessageHeader;
import org.gal.messaging.engine.core.api.Reducer;

public class InstanceMessageReducer implements Reducer {
	
	private final StateStore<InstanceStateStoreKey, InstanceState> stateStore;

	InstanceMessageReducer(
			StateStore<InstanceStateStoreKey, InstanceState> stateStore) {
		this.stateStore = stateStore;
	}

	@Override
	public boolean isCompatible(Plugin<?, ?, ?> plugin, MessageEnvelope messageEnvelope, MessageContext ctx) {
		return plugin.messageClass(messageEnvelope.header().type()) != null;
	}

	@Override
	public <M extends Message, S extends InstanceState, G extends GlobalState> Collection<MessageEnvelope> reduce(Plugin<M, S, G> plugin,
			MessageEnvelope messageEnvelope, MessageContext ctx, PluginContext<M> context) {
		
		List<MessageEnvelope> feedback = new ArrayList<>();
		
		M m = plugin.messageClass(messageEnvelope.header().type()).cast(messageEnvelope.payload());
		
		final List<M> sideEffects = new ArrayList<>();
			
		S currentState = Optional.of(m)
				.filter(InstanceTrait.class::isInstance)
				.map(InstanceTrait.class::cast)
				.map(InstanceTrait::instance)
				.map(instance -> InstanceStateStoreKey.of(plugin.name(), instance))
				.flatMap(stateStore::lookup)
				.map(state -> plugin.stateClass().cast(state))
				.orElse(null);
		
		try {
			S newState = plugin.apply(m, currentState, ctx);
			
			Optional.ofNullable(newState)
				.filter(s -> !s.equals(currentState))
				.ifPresent(state -> {
					InstanceStateStoreKey stateKey = InstanceStateStoreKey.of(plugin.name(), state.instance());
					stateStore.set(stateKey, state);
					
					StateUpdateMessage stateUpdateMessage = StateUpdateMessage.of(newState.instance(), newState.version());
					MessageHeader header = MessageHeader.Builder()
							.plugin(plugin.name())
							.type("stateUpdate")
							.uuid(UUID.randomUUID().toString())
							.inResponseTo(messageEnvelope.header().uuid())
							.build();
					
					feedback.add(MessageEnvelope.of(header, stateUpdateMessage));
				});
			
			sideEffects.addAll(plugin.sideEffects(m, currentState, newState, ctx, context));
			
		} catch (Exception e) {
			sideEffects.addAll(plugin.sideEffects(m, currentState, ctx, e, context));
			e.printStackTrace();
		}
		
		sideEffects.stream()
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
