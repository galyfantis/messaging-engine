package org.gal.messaging.engine.utils.executors;

import java.util.concurrent.Executors;

import org.gal.messaging.engine.core.api.ExecutorProvider;

public class ExecutorProviderFactory {
	
	public static ExecutorProvider defaultExecutorProvider() {
		return new DefaultExecutorProvider(Executors::newSingleThreadExecutor, Executors::newSingleThreadScheduledExecutor);
	}
	
	public static ExecutorProvider directExecutorProvider() {
		return new DefaultExecutorProvider(DirectExecutorService::new, DirectScheduledExecutorService::new);
	}

}
