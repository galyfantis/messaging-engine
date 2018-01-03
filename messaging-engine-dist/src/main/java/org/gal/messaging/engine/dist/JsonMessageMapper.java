package org.gal.messaging.engine.dist;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.core.api.EngineException;
import org.gal.messaging.engine.core.api.ErrorMessage;
import org.gal.messaging.engine.core.api.MessageEnvelope;
import org.gal.messaging.engine.core.api.MessageHeader;
import org.gal.messaging.engine.core.api.MessageMapper;
import org.gal.messaging.engine.core.api.MessageTypeResolver;
import org.gal.messaging.engine.core.api.WarningMessage;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonMessageMapper implements MessageMapper<JsonNode> {
	
	private static final String HEADER_FIELD = "header";
	private static final String PAYLOAD_FIELD = "payload";
	private static final String PLUGIN_FIELD = "plugin";
	private static final String TYPE_FIELD = "type";
	private static final String UUID_FIELD = "uuid";
	private static final String IN_RESPONSE_TO_FIELD = "inResponseTo";
	
	private final ObjectMapper mapper;
	
	public JsonMessageMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public MessageEnvelope read(JsonNode raw, MessageTypeResolver messageTypeResolver) throws EngineException {
		String uuid = Optional.ofNullable(raw.get(HEADER_FIELD).get(UUID_FIELD)).map(JsonNode::asText).orElse(UUID.randomUUID().toString());
		String inResponseTo = Optional.ofNullable(raw.get(HEADER_FIELD).get(IN_RESPONSE_TO_FIELD)).map(JsonNode::asText).orElse(null);
		try {
			MessageHeader header = MessageHeader.Builder()
					.plugin(raw.get(HEADER_FIELD).get(PLUGIN_FIELD).asText())
					.type(raw.get(HEADER_FIELD).get(TYPE_FIELD).asText())
					.uuid(uuid)
					.inResponseTo(inResponseTo)
					.build();
			
			Message msg = mapper.treeToValue(raw.get(PAYLOAD_FIELD), messageTypeResolver.resolve(header.plugin(), header.type()));
			
			return MessageEnvelope.of(header, msg);
		} catch (EngineException e) {
			throw new EngineException("Error converting raw message to MessageEnvelope", e.getCode(), uuid, e);
		} catch (Exception e) {
			throw new EngineException("Error converting raw message to MessageEnvelope", "005", uuid, e);
		}
	}

	@Override
	public JsonNode write(MessageEnvelope messageEnvelope) {
		ObjectNode header = mapper.createObjectNode();
		header.put(PLUGIN_FIELD, messageEnvelope.header().plugin());
		header.put(TYPE_FIELD, messageEnvelope.header().type());
		header.put(UUID_FIELD, messageEnvelope.header().uuid());
		header.put(IN_RESPONSE_TO_FIELD, messageEnvelope.header().inResponseTo());
		
		JsonNode payload = mapper.valueToTree(messageEnvelope.payload());

		ObjectNode json = mapper.createObjectNode();
		json.set(HEADER_FIELD, header);
		json.set(PAYLOAD_FIELD, payload);
		
		return json;
	}

	@Override
	public Message wrapError(ErrorMessage error) {
		return JsonError.from(error);
	}
	
	@Override
	public Message wrapWarning(WarningMessage warn) {
		return JsonWarning.from(warn);
	}

	@Value.Immutable
	@Value.Style(
			visibility = ImplementationVisibility.PACKAGE,
			builderVisibility = BuilderVisibility.SAME,
			depluralize = true
	)
	@JsonSerialize(as = ImmutableJsonError.class)
	@JsonDeserialize(as = ImmutableJsonError.class, builder = ImmutableJsonError.Builder.class)
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@JsonIgnoreProperties(ignoreUnknown = true)
	interface JsonError extends Message {
		@Value.Parameter
		String code();

		@Value.Parameter
		@Nullable
		String message();
		
		static JsonError from(ErrorMessage error) {
			return ImmutableJsonError.of(error.code(), error.message());
		}
		
	}
	
	@Value.Immutable
	@Value.Style(
			visibility = ImplementationVisibility.PACKAGE,
			builderVisibility = BuilderVisibility.SAME,
			depluralize = true
	)
	@JsonSerialize(as = ImmutableJsonWarning.class)
	@JsonDeserialize(as = ImmutableJsonWarning.class, builder = ImmutableJsonWarning.Builder.class)
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@JsonIgnoreProperties(ignoreUnknown = true)
	interface JsonWarning extends Message {
		@Value.Parameter
		String code();

		@Value.Parameter
		@Nullable
		String message();
		
		static JsonWarning from(WarningMessage warn) {
			return ImmutableJsonWarning.of(warn.code(), warn.message());
		}
	}

}
