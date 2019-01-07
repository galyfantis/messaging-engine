package org.gal.messaging.engine.dist;

import java.util.Optional;
import java.util.UUID;

import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.core.AbstractMessageMapper;
import org.gal.messaging.engine.core.api.MessageHeader;
import org.gal.messaging.engine.core.api.MessagePluginResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class JsonMessageMapper extends AbstractMessageMapper<JsonNode> {
	
	private static final String HEADER_FIELD = "header";
	private static final String PAYLOAD_FIELD = "payload";
	private static final String PLUGIN_FIELD = "plugin";
	private static final String TYPE_FIELD = "type";
	private static final String UUID_FIELD = "uuid";
	private static final String IN_RESPONSE_TO_FIELD = "inResponseTo";
	
	private final ObjectMapper mapper;
	
	public JsonMessageMapper(MessagePluginResolver pluginResolver) {
		super(pluginResolver);
		this.mapper = new ObjectMapper();
		this.mapper.registerModule(new Jdk8Module());
	}
	
	@Override
	public String name() {
		return "JsonNode";
	}
	
	@Override
	protected String plugin(JsonNode raw) {
		return raw.get(HEADER_FIELD).get(PLUGIN_FIELD).asText();
	}

	@Override
	protected String type(JsonNode raw) {
		return raw.get(HEADER_FIELD).get(TYPE_FIELD).asText();
	}

	@Override
	protected String uuid(JsonNode raw) {
		return Optional.ofNullable(raw.get(HEADER_FIELD).get(UUID_FIELD)).map(JsonNode::asText).orElse(UUID.randomUUID().toString());
	}

	@Override
	protected String inResponseTo(JsonNode raw) {
		return Optional.ofNullable(raw.get(HEADER_FIELD).get(IN_RESPONSE_TO_FIELD)).map(JsonNode::asText).orElse(null);
	}

	@Override
	protected String payloadAsString(JsonNode raw) {
		return Optional.ofNullable(raw.get(PAYLOAD_FIELD)).map(JsonNode::asText).orElse(null);
	}

	@Override
	protected JsonNode doWrite(MessageHeader messageHeader, String payloadAsString) {
		ObjectNode header = mapper.createObjectNode();
		header.put(PLUGIN_FIELD, messageHeader.plugin());
		header.put(TYPE_FIELD, messageHeader.type());
		header.put(UUID_FIELD, messageHeader.uuid());
		header.put(IN_RESPONSE_TO_FIELD, messageHeader.inResponseTo());
		
		ObjectNode json = mapper.createObjectNode();
		json.set(HEADER_FIELD, header);
		json.put(PAYLOAD_FIELD, payloadAsString);
		return json;
	}
	
	@Override
	protected JsonNode doWrite(MessageHeader header, Message message) {
		try {
			String payloadAsString = mapper.writeValueAsString(message);
			return doWrite(header, payloadAsString);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
