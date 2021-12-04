package mc.obliviate.blokduels.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class BlokDuelsProxy extends Plugin {

	@Override
	public void onEnable() {
		getLogger().info("Blok Duels is in proxy mode.");
		getLogger().info("Running in " + getProxy().getName() + " v" + getProxy().getVersion());
	}
}
