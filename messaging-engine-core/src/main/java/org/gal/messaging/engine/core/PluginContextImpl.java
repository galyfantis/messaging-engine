package org.gal.messaging.engine.core;

import org.gal.messaging.engine.api.Message;
import org.gal.messaging.engine.api.Messaging;
import org.gal.messaging.engine.api.PluginContext;
import org.gal.messaging.engine.api.Scheduling;

class PluginContextImpl<M extends Message> implements PluginContext<M> {

	private final Messaging<M> messaging;
	private final Scheduling<M> scheduling;
	
	PluginContextImpl(Messaging<M> messaging, Scheduling<M> scheduling) {
		this.messaging = messaging;
		this.scheduling = scheduling;
	}
	
	@Override
	public Messaging<M> messaging() {
		return messaging;
	}

	@Override
	public Scheduling<M> scheduling() {
		return scheduling;
	}
}
