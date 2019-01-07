package org.messaging.engine.plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.gal.messaging.engine.api.EngineMessage;
import org.gal.messaging.engine.api.ErrorOccuredMsg;
import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.api.Plugin;
import org.gal.messaging.engine.api.PluginContext;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EnginePlugin implements Plugin<EngineMessage, EngineState, EngineGlobalState> {
	
	private final Map<String, Class<? extends EngineMessage>> classes = new HashMap<>();
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	public EnginePlugin() {
		classes.put("error_occured", ErrorOccuredMsg.class);
		classes.put("error", EngineError.class);
		classes.put("warn", EngineWarning.class);
	}

	@Override
	public String name() {
		return "engine";
	}

	@Override
	public Class<? extends EngineMessage> messageClass(String type) {
		return classes.get(type);
	}

	@Override
	public String messageType(EngineMessage message) {
		return classes
				.entrySet()
				.stream()
				.filter(e -> e.getValue().isAssignableFrom(message.getClass()))
				.map(Entry::getKey)
				.findFirst()
				.orElse(null);
	}

	@Override
	public Class<EngineState> stateClass() {
		return EngineState.class;
	}

	@Override
	public Class<EngineGlobalState> globalStateClass() {
		return EngineGlobalState.class;
	}

	@Override
	public EngineState apply(EngineMessage message, EngineState state, MessageContext ctx) {
		if (message instanceof ErrorOccuredMsg) {
			switch (((ErrorOccuredMsg) message).level()) {
			case ERROR:
				return EngineState.increaseErrors(((ErrorOccuredMsg) message).instance(), state);
			case WARNING:
				return EngineState.increaseWarnings(((ErrorOccuredMsg) message).instance(), state);
			}
		}
		return state;
	}
	
	@Override
	public EngineGlobalState apply(EngineState currentState, EngineGlobalState currentGlobalState, MessageContext ctx) {
		return currentGlobalState;
	}

	@Override
	public List<EngineMessage> sideEffects(EngineMessage message, EngineState previousState, EngineState currentState,
			MessageContext ctx, PluginContext<EngineMessage> context) {
		
		Optional.of(message)
				.filter(ErrorOccuredMsg.class::isInstance)
				.map(ErrorOccuredMsg.class::cast)
				.map(EnginePlugin::toErrorMessage)
				.ifPresent(errorMessage -> context.messaging().send(errorMessage, Arrays.asList(ctx.client())));

		return Collections.emptyList();
	}
	
	private static EngineMessage toErrorMessage(ErrorOccuredMsg errorOccured) {
		switch (errorOccured.level()) {
		case ERROR:
			return EngineError.of(errorOccured.code(), errorOccured.message());
		case WARNING:
			return EngineWarning.of(errorOccured.code(), errorOccured.message());
		default:
			throw new IllegalArgumentException("Not expected type " + errorOccured.level());
		}
	}

	@Override
	public List<EngineMessage> sideEffects(EngineMessage message, EngineState state, MessageContext ctx, Exception exception,
			PluginContext<EngineMessage> context) {
		return Collections.emptyList();
	}

	@Override
	public List<EngineMessage> sideEffects(EngineState currentState, EngineGlobalState previousGlobalState,
			EngineGlobalState currentGlobalState, MessageContext ctx, PluginContext<EngineMessage> context) {
		return Collections.emptyList();
	}

	@Override
	public EngineMessage deserialize(String messageAsString, Class<? extends EngineMessage> clazz) {
		try {
			return mapper.readValue(messageAsString, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e); // TODO
		}
	}

	@Override
	public String serialize(EngineMessage message) {
		try {
			return mapper.writeValueAsString(message);
		} catch (Exception e) {
			throw new RuntimeException(e); // TODO
		}
	}

}
