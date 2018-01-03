package org.gal.messaging.engine.core;

import java.util.concurrent.ScheduledExecutorService;

import org.gal.messaging.engine.api.GlobalState;
import org.gal.messaging.engine.api.InstanceState;
import org.gal.messaging.engine.core.api.Engine;
import org.gal.messaging.engine.core.api.ExecutorProvider;
import org.gal.messaging.engine.core.api.MessagingPlugin;

public class EngineFactory {

	public static Engine createEngine(MessagingPlugin<?> messagingPlugin, ExecutorProvider executorProvider, int executors) {
		MessageDispatcherImpl messagingDispatcher = new MessageDispatcherImpl(messagingPlugin, new MultiExecutorService(executors, executorProvider));
		
		ScheduledExecutorService scheduledExecutorService = executorProvider.newScheduledExecutorService();
		EngineImpl engine = new EngineImpl(messagingDispatcher, messagingPlugin, scheduledExecutorService);
		
		StateStore<InstanceStateStoreKey, InstanceState> stateStore = new StateStore<>();
		StateStore<String, GlobalState> globalStateStore = new StateStore<>();
		
		engine.registerReducer(new InstanceMessageReducer(messagingDispatcher, stateStore));
		engine.registerReducer(new StateUpdateReducer(messagingDispatcher, globalStateStore, ReadOnlyStateStore.of(stateStore)));
		
		messagingDispatcher.registerEngine(engine);
		messagingPlugin.registerMessageTypeResolver(engine);
		
		return engine;
	}
	
	public static Engine createEngine(MessagingPlugin<?> messagingPlugin, ExecutorProvider executorProvider) {
		return createEngine(messagingPlugin, executorProvider, 10);
	}

}
