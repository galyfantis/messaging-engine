package org.gal.messaging.engine.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.gal.messaging.engine.api.Plugin;

class PluginRegistry {
	
	private Map<String, Plugin<?, ?, ?>> plugins = new HashMap<>();
	
	void register(Plugin<?, ?, ?> plugin) {
		plugins.put(plugin.name(), plugin);
	}
	
	void unregister(Plugin<?, ?, ?> plugin) {
		plugins.remove(plugin.name());
	}

	Optional<Plugin<?, ?, ?>> lookup(String plugin) {
		return Optional.ofNullable(plugins.get(plugin));
	}

}
