package org.gal.messaging.engine.core.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface ExecutorProvider {
	
	ExecutorService newExecutorService();
	
	ScheduledExecutorService newScheduledExecutorService();
}
