package org.gal.messaging.engine.core.api;

import org.gal.messaging.engine.api.Plugin;

public interface MessagePluginResolver {
	
	Plugin<?, ?, ?> resolve(String plugin);

}
