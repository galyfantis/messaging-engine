package org.gal.messaging.engine.utils.executors;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class DirectScheduledExecutorService extends DirectExecutorService implements ScheduledExecutorService {

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		RunnableScheduledFuture<?> f = new DirectScheduledFuture<>(command);
		f.run();
		return f;
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		RunnableScheduledFuture<V> f = new DirectScheduledFuture<>(callable);
		f.run();
		return f;
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		RunnableScheduledFuture<?> f = new DirectScheduledFuture<>(command);
		f.run();
		return f;
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		RunnableScheduledFuture<?> f = new DirectScheduledFuture<>(command);
		f.run();
		return f;
	}
	
	private static class DirectScheduledFuture<T> implements RunnableScheduledFuture<T> {
		
		private final Callable<T> callable;
		
		private final Runnable command;

		private T value;

		DirectScheduledFuture(Callable<T> callable) {
			this.callable = callable;
			this.command = null;
		}
		
		DirectScheduledFuture(Runnable command) {
			this.callable = null;
			this.command = command;
		}
		
		@Override
		public void run() {
			try {
				if (callable != null) {					
					value = callable.call();
				} else if (command != null) {
					command.run();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public T get() throws InterruptedException, ExecutionException {
			return value;
		}

		@Override
		public T get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			return value;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return true;
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return 0;
		}

		@Override
		public int compareTo(Delayed o) {
			return 0;
		}

		@Override
		public boolean isPeriodic() {
			return false;
		}
	}

}
