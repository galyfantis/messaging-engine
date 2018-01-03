package org.gal.messaging.engine.dist;

import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.core.EngineFactory;
import org.gal.messaging.engine.core.api.Engine;
import org.gal.messaging.engine.core.api.ExecutorProvider;
import org.gal.messaging.engine.core.api.MessagingPlugin;
import org.gal.messaging.engine.core.api.OutgoingMessageListener;
import org.gal.messaging.engine.demo.DemoPlugin;
import org.gal.messaging.engine.messaging.MessagingPluginFactory;
import org.gal.messaging.engine.utils.executors.ExecutorProviderFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class EngineRunner {
	
	public void run() {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		
		// Create a messaging plugin to send / receive messages of type JsonNode
		MessagingPlugin<JsonNode> messagingPlugin = MessagingPluginFactory.createMessagingPlugin(new JsonMessageMapper(mapper));
		
		OutgoingMessageListener<JsonNode> listener = (message, recipients) -> System.out.println("Sending message: " + message + " to " + recipients);
		messagingPlugin.addMessageListener(listener);
		//
		
		// Create an executor provider for the engine
		ExecutorProvider executorProvider = ExecutorProviderFactory.directExecutorProvider();
		
		// Create the engine
		Engine engine = EngineFactory.createEngine(messagingPlugin, executorProvider, 10);
		
		// register the Demo plugin
		engine.registerPlugin(new DemoPlugin());
		
		// Start the engine
		engine.start();
		
		try {
			// a DemoMsgCreateInstance to create a new instance (instance id assigned by the plugin)
			JsonNode json_init = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"test_create\"}, \"payload\": {} }");
			MessageContext ctx_init = MessageContext.of("client_1", "user_1");
			messagingPlugin.receive(json_init, ctx_init);
			
			// DemoMsg for an instance that does not exist. Shall be rejected by the plugin   
			JsonNode json1 = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"test_type_1\"}, \"payload\": {\"instance\": \"instance_id\", \"incr\": 4} }");
			MessageContext ctx1 = MessageContext.of("client_1", "user_1");
			messagingPlugin.receive(json1, ctx1);
			
			// a DemoMsgCreate to create a new instance with instance id provided by the client
			JsonNode json_init_with_id = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"test_create_with_id\"}, \"payload\": {\"instance\": \"instance_id\"} }");
			MessageContext ctx_init_with_id = MessageContext.of("client_1", "user_1");
			messagingPlugin.receive(json_init_with_id, ctx_init_with_id);
			
			// Demo message that will be accepted by the plugin
			JsonNode json2 = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"test_type_1\"}, \"payload\": {\"instance\": \"instance_id\", \"incr\": 7} }");
			MessageContext ctx2 = MessageContext.of("client_1", "user_1");
			messagingPlugin.receive(json2, ctx2);
			
			// Demo message that will be accepted by the plugin
			JsonNode json3 = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"test_type_2\"}, \"payload\": {\"instance\": \"instance_id\", \"decr\": 3} }");
			MessageContext ctx3 = MessageContext.of("client_2", "user_2");
			messagingPlugin.receive(json3, ctx3);
			
			// Demo message that will be rejected by the plugin, due to invalid data (incr = -8)
			JsonNode json4 = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"test_type_1\"}, \"payload\": {\"instance\": \"instance_id\", \"incr\": -8} }");
			MessageContext ctx4 = MessageContext.of("client_1", "user_1");
			messagingPlugin.receive(json4, ctx4);
			
			// Demo message that will be accepted by the plugin
			JsonNode json5 = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"leave\"}, \"payload\": {\"instance\": \"instance_id\"} }");
			MessageContext ctx5 = MessageContext.of("client_1", "user_1");
			messagingPlugin.receive(json5, ctx5);
			
			// Demo message that will be accepted by the plugin
			JsonNode json6 = mapper.readTree("{ \"header\": {\"plugin\": \"demo_foo\", \"type\": \"leave\"}, \"payload\": {\"instance\": \"instance_id\"} }");
			MessageContext ctx6 = MessageContext.of("client_1", "user_1");
			messagingPlugin.receive(json6, ctx6);
		
			// Demo message that will be accepted by the plugin
			JsonNode json7 = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"foo_bar_type\"}, \"payload\": {\"instance\": \"instance_id\"} }");
			MessageContext ctx7 = MessageContext.of("client_1", "user_1");
			messagingPlugin.receive(json7, ctx7);
			
			Thread.sleep(15000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		engine.stop();
		
	}
	
	public static void main(String [] args) {
		EngineRunner runner = new EngineRunner();
		runner.run();
	}

}
