package org.gal.messaging.engine.core;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiConsumer;

import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.api.Messaging;
import org.gal.messaging.engine.api.Plugin;
import org.gal.messaging.engine.api.PluginContext;
import org.gal.messaging.engine.api.Scheduling;
import org.gal.messaging.engine.core.api.MessageDispatcher;
import org.gal.messaging.engine.core.api.MessageEnvelope;
import org.gal.messaging.engine.core.api.MessageHeader;

class PluginContextFactory {
	
	public static <M extends Message> PluginContext<M> createPluginContext(MessageEnvelope currentMessage,
			Plugin<M, ?, ?> plugin,
			BiConsumer<MessageEnvelope, List<String>> messageSender, 
			MessageDispatcher messagingDispatcher,
			ScheduledExecutorService scheduledExecutorService) {
		
		Messaging<M> messaging = (message, recipients) -> {
			MessageHeader header = MessageHeader.Builder()
					.plugin(plugin.name())
					.type(plugin.messageType(message))
					.uuid(UUID.randomUUID().toString())
					.inResponseTo(currentMessage.header().uuid())
					.build();

			messageSender.accept(MessageEnvelope.of(header, message), recipients);
		};
		
		Scheduling<M> scheduling = (message, ctx, delay, unit) -> {
			MessageHeader header = MessageHeader.Builder()
					.plugin(plugin.name())
					.type(plugin.messageType(message))
					.uuid(UUID.randomUUID().toString())
					.inResponseTo(currentMessage.header().uuid())
					.build();

			System.out.println("Scheduling to dispatch message in " + delay + " " + unit);
			scheduledExecutorService.schedule(() -> {
				messagingDispatcher.dispatch(MessageEnvelope.of(header, message), ctx);
			}, delay, unit);
		};
		
		return new PluginContextImpl<M>(messaging, scheduling);
	}
}
