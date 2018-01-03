package org.gal.messaging.engine.api;

import java.util.concurrent.TimeUnit;

public interface Scheduling<M extends Message> {
	
	void scheduleDispatch(M message, MessageContext ctx, long delay, TimeUnit unit);
	
}
