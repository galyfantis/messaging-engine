package org.gal.messaging.engine.api;

public interface PluginContext<M extends Message> {
	
	Messaging<M> messaging();
	
	Scheduling<M> scheduling();

}
