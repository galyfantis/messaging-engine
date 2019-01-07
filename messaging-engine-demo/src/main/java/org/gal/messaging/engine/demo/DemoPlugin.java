package org.gal.messaging.engine.demo;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.gal.messaging.engine.api.InstanceTrait;
import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.api.Plugin;
import org.gal.messaging.engine.api.PluginContext;
import org.gal.messaging.engine.demo.ImmutableDemoGlobalState.Builder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DemoPlugin implements Plugin<DemoMessage, DemoState, DemoGlobalState> {
	
	private final Map<String, Class<? extends DemoMessage>> classes = new HashMap<>();
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	public DemoPlugin() {
		classes.put("test_create", DemoMsgCreateInstance.class);
		classes.put("test_create_with_id", DemoMsgCreate.class);
		classes.put("test_type_1", DemoMsg1.class);
		classes.put("test_type_2", DemoMsg2.class);
		classes.put("count_state", CountStateProjection.class);
		classes.put("error", DemoError.class);
		classes.put("leave", DemoClientLeaveMsg.class);
		classes.put("count_global_state", CountGlobalStateProjection.class);
	}
	
	@Override
	public String name() {
		return "demo";
	}

	@Override
	public Class<? extends DemoMessage> messageClass(String type) {
		return classes.get(type);
	}
	
	@Override
	public String messageType(DemoMessage message) {
		return classes
				.entrySet()
				.stream()
				.filter(e -> e.getValue().isAssignableFrom(message.getClass()))
				.map(Entry::getKey)
				.findFirst()
				.orElse(null);
	}

	@Override
	public Class<DemoState> stateClass() {
		return DemoState.class;
	}
	
	@Override
	public Class<DemoGlobalState> globalStateClass() {
		return DemoGlobalState.class;
	}

	@Override
	public DemoState apply(DemoMessage message, DemoState state, MessageContext ctx) {
		return message.accept(new DemoStateReducerVisitorImpl(), state, ctx);
	}

	@Override
	public List<DemoMessage> sideEffects(DemoMessage message, DemoState previousState, DemoState currentState,
			MessageContext ctx, PluginContext<DemoMessage> context) {
		
		return message.accept(new DemoSideEffectsVisitorImpl(), currentState, previousState, ctx, context);
	}

	@Override
	public List<DemoMessage> sideEffects(DemoMessage message, DemoState state, MessageContext ctx, Exception exception,
			PluginContext<DemoMessage> context) {
		
		Optional.of(message)
				.filter(InstanceTrait.class::isInstance)
				.map(InstanceTrait.class::cast)
				.map(InstanceTrait::instance)
				.ifPresent(instance -> {
					context.messaging().send(DemoError.Builder().instance(((InstanceTrait) message).instance()).message(exception.getMessage() + "").build(), Arrays.asList(ctx.client()));	
				});
		
		return message.accept(new DemoSideEffectsVisitorImpl(), exception, ctx, context);
	}

	@Override
	public DemoGlobalState apply(DemoState currentState, DemoGlobalState currentGlobalState, MessageContext ctx) {
		Builder builder = Optional.ofNullable(currentState).map(s -> DemoGlobalState.Builder().from(s)).orElse(DemoGlobalState.Builder()).since(OffsetDateTime.now());
		
		HashSet<String> instances = Optional.ofNullable(currentGlobalState).map(DemoGlobalState::instances).map(HashSet<String>::new).orElse(new HashSet<>());
		HashSet<String> participants = Optional.ofNullable(currentGlobalState).map(DemoGlobalState::participants).map(HashSet<String>::new).orElse(new HashSet<>());
		long version  = Optional.ofNullable(currentGlobalState).map(DemoGlobalState::version).map(v -> v + 1).orElse(0L);
		
		instances.add(currentState.instance());
		participants.add(ctx.client());
		
		builder
			.participants(participants)
			.numberOfParticipants(participants.size())
			.instances(instances)
			.numberOfInstances(instances.size())
			.since(OffsetDateTime.now())
			.version(version);
	
		return builder.build();
	}

	@Override
	public List<DemoMessage> sideEffects(DemoState currentState, DemoGlobalState previousGlobalState,
			DemoGlobalState currentGlobalState, MessageContext ctx, PluginContext<DemoMessage> context) {
		if (!currentGlobalState.equals(previousGlobalState)) {
			CountGlobalStateProjection countMsg = CountGlobalStateProjection.from(currentGlobalState);
			context.messaging().send(countMsg, new ArrayList<>(currentGlobalState.participants()));
		}
		
		return Collections.emptyList();
	}

	@Override
	public DemoMessage deserialize(String messageAsString, Class<? extends DemoMessage> clazz) {
		try {
			return mapper.readValue(messageAsString, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e); // TODO
		}
	}
	
	@Override
	public String serialize(DemoMessage message) {
		try {
			return mapper.writeValueAsString(message);
		} catch (Exception e) {
			throw new RuntimeException(e); // TODO
		}
	}


}
