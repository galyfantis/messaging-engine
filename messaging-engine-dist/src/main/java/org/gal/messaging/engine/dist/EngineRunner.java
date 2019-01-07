package org.gal.messaging.engine.dist;

import org.gal.messaging.engine.api.MessageContext;
import org.gal.messaging.engine.core.EngineBuilder;
import org.gal.messaging.engine.core.api.Engine;
import org.gal.messaging.engine.core.api.ExecutorProvider;
import org.gal.messaging.engine.core.api.MessageEnvelope;
import org.gal.messaging.engine.core.api.MessageListener;
import org.gal.messaging.engine.demo.DemoPlugin;
import org.gal.messaging.engine.utils.executors.ExecutorProviderFactory;
import org.messaging.engine.plugin.EnginePlugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class EngineRunner {
	
	public void run() {
		// Create an executor provider for the engine
		ExecutorProvider executorProvider = ExecutorProviderFactory.directExecutorProvider();
		//ExecutorProvider executorProvider = ExecutorProviderFactory.defaultExecutorProvider();
		
		// Create the engine
		Engine engine = EngineBuilder.newInstance()
								.withExecutorProvider(executorProvider)
								.withNumberOfExecutors(10)
								.build();
		
		engine.registerMessageMapper(new JsonMessageMapper(engine.pluginResolver()));
		
		engine.registerMessageMapper(new PassThroughMessageMapper(engine.pluginResolver()));
		
		// register the Demo plugin
		engine.registerPlugin(new DemoPlugin());
		
		engine.registerPlugin(new EnginePlugin());
		
		MessageListener<JsonNode> listener = (message, recipients) -> System.out.println("Sending message: " + message + " to " + recipients);
		engine.registerListener(listener, "JsonNode");
		
//		MessageListener<MessageEnvelope> listener2 = (message, recipients) -> System.out.println("Sending message2: " + message + " to " + recipients);
//		engine.registerListener(listener2, "PassThrough");
		
		// Start the engine
		engine.start();
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new Jdk8Module());
			
			// a DemoMsgCreateInstance to create a new instance (instance id assigned by the plugin)
			JsonNode json_init = mapper.readTree("{\"header\":{\"plugin\":\"demo\",\"type\":\"test_create\"},\"payload\":\"{}\"}");
			MessageContext ctx_init = MessageContext.of("client_1", "user_1");
			engine.handle(json_init, "JsonNode", ctx_init);
			
			// DemoMsg for an instance that does not exist. Shall be rejected by the plugin   
			JsonNode json1 = mapper.readTree("{\"header\":{\"plugin\":\"demo\",\"type\":\"test_type_1\"},\"payload\":\"{\\\"instance\\\":\\\"instance_id\\\",\\\"incr\\\":4}\"}");
			MessageContext ctx1 = MessageContext.of("client_1", "user_1");
			engine.handle(json1, "JsonNode", ctx1);
			
			// a DemoMsgCreate to create a new instance with instance id provided by the client
			JsonNode json_init_with_id = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"test_create_with_id\"}, \"payload\": \"{\\\"instance\\\": \\\"instance_id\\\"}\" }");
			MessageContext ctx_init_with_id = MessageContext.of("client_1", "user_1");
			engine.handle(json_init_with_id, "JsonNode", ctx_init_with_id);
			
			// Demo message that will be accepted by the plugin
			JsonNode json2 = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"test_type_1\"}, \"payload\": \"{\\\"instance\\\": \\\"instance_id\\\", \\\"incr\\\": 7}\" }");
			MessageContext ctx2 = MessageContext.of("client_1", "user_1");
			engine.handle(json2, "JsonNode", ctx2);
			
			// Demo message that will be accepted by the plugin
			JsonNode json3 = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"test_type_2\"}, \"payload\": \"{\\\"instance\\\": \\\"instance_id\\\", \\\"decr\\\": 3}\" }");
			MessageContext ctx3 = MessageContext.of("client_2", "user_2");
			engine.handle(json3, "JsonNode", ctx3);
			
			// Demo message that will be rejected by the plugin, due to invalid data (incr = -8)
			JsonNode json4 = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"test_type_1\"}, \"payload\": \"{\\\"instance\\\": \\\"instance_id\\\", \\\"incr\\\": -8}\" }");
			MessageContext ctx4 = MessageContext.of("client_1", "user_1");
			engine.handle(json4, "JsonNode", ctx4);
			
			// Demo message that will be accepted by the plugin
			JsonNode json5 = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"leave\"}, \"payload\": \"{\\\"instance\\\": \\\"instance_id\\\"}\" }");
			MessageContext ctx5 = MessageContext.of("client_1", "user_1");
			engine.handle(json5, "JsonNode", ctx5);
			
			// Demo message that will be accepted by the plugin
			JsonNode json6 = mapper.readTree("{ \"header\": {\"plugin\": \"demo_foo\", \"type\": \"leave\"}, \"payload\": \"{\\\"instance\\\": \\\"instance_id\\\"}\" }");
			MessageContext ctx6 = MessageContext.of("client_1", "user_1");
			engine.handle(json6, "JsonNode", ctx6);
		
			// Demo message that will be accepted by the plugin
			JsonNode json7 = mapper.readTree("{ \"header\": {\"plugin\": \"demo\", \"type\": \"foo_bar_type\"}, \"payload\": \"{\\\"instance\\\": \\\"instance_id\\\"}\" }");
			MessageContext ctx7 = MessageContext.of("client_1", "user_1");
			engine.handle(json7, "JsonNode", ctx7);
			
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
