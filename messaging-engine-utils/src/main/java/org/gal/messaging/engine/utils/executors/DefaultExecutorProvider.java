package org.gal.messaging.engine.utils.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

import org.gal.messaging.engine.core.api.ExecutorProvider;

class DefaultExecutorProvider implements ExecutorProvider {
	
	private final Supplier<ExecutorService> executorService;
	private final Supplier<ScheduledExecutorService> scheduledExecutorService;
	
	DefaultExecutorProvider(Supplier<ExecutorService> executorService, Supplier<ScheduledExecutorService> scheduledExecutorService) {
		this.executorService = executorService;
		this.scheduledExecutorService = scheduledExecutorService;
	}

	@Override
	public ExecutorService newExecutorService() {
		return this.executorService.get();
	}
	
	@Override
	public ScheduledExecutorService newScheduledExecutorService() {
		return this.scheduledExecutorService.get();
	}

}
