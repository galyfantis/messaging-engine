package org.gal.messaging.engine.core;

import java.util.ArrayList;
import java.util.List;

import org.gal.messaging.engine.api.GlobalState;
import org.gal.messaging.engine.api.InstanceState;
import org.gal.messaging.engine.core.api.Engine;
import org.gal.messaging.engine.core.api.ExecutorProvider;
import org.gal.messaging.engine.core.api.Reducer;

public class EngineBuilder {
	
	private final List<Reducer> reducers = new ArrayList<>();
	
	private ExecutorProvider executorProvider;

	private Integer executors;
	
	private EngineBuilder() {}
	
	public static EngineBuilder newInstance() {
		return new EngineBuilder();
	}
	
	public EngineBuilder withExecutorProvider(ExecutorProvider executorProvider) {
		this.executorProvider = executorProvider;
		return this;
	}
	
	public EngineBuilder withNumberOfExecutors(Integer executors) {
		this.executors = executors;
		return this;
	}
	
	public EngineBuilder addReducer(Reducer reducer) {
		reducers.add(reducer);
		return this;
	}
	
	public Engine build() {
		if (executorProvider == null) {
			throw new IllegalStateException("ExecutorProvider is mandatory");
		}
		if (executors == null) {
			executors = 10;
		}
		
		EngineImpl engine = new EngineImpl(executors, executorProvider);
		
		StateStore<InstanceStateStoreKey, InstanceState> stateStore = new StateStore<>();
		StateStore<String, GlobalState> globalStateStore = new StateStore<>();
		
		engine.registerReducer(new InstanceMessageReducer(stateStore));
		engine.registerReducer(new StateUpdateReducer(globalStateStore, ReadOnlyStateStore.of(stateStore)));
		
		reducers.forEach(r -> engine.registerReducer(r));
		
		return engine;
	}

}
