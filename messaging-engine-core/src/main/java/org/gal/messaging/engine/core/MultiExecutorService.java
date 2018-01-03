package org.gal.messaging.engine.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.gal.messaging.engine.core.api.ExecutorProvider;

class MultiExecutorService {
	
	private final int execs;
	private final ExecutorService[] executors;

	public MultiExecutorService(int execs, ExecutorProvider executorProvider) {
		this.execs = execs;
		executors = new ExecutorService[execs];
		for (int i = 0; i < execs; i++) {
			executors[i] = executorProvider.newExecutorService();
		}
	}
	
	<T> Future<T> submit(String key, Callable<T> task) {
		return executors[h(key, execs)].submit(task);
	}
    
    <T> Future<T> submit(String key, Runnable task, T result) {
		return executors[h(key, execs)].submit(task, result);
	}

    Future<?> submit(String key, Runnable task) {
    	return executors[h(key, execs)].submit(task);
	}
    
    public void shutdown() {
    	Stream.of(executors)
    		.forEach(ExecutorService::shutdown);
    }
    
	private int h(String x, int M) {
		char ch[] = x.toCharArray();
		int sum = 0;
		for (char c : ch) {
			sum += c;
		}
		return sum % M;
	}
	
}
