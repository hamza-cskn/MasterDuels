package mc.obliviate.blokduels.gui;

import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameState;
import mc.obliviate.blokduels.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.Map;

public class DuelArenaListGUI extends GUI {

	public DuelArenaListGUI(Player player) {
		super(player, "duel-games-list-gui", "Oynanan Düellolar (/duel)", 6);
	}

	private static String convertMode(int size, int amount) {
		final StringBuilder sb = new StringBuilder();
		for (; amount > 0; amount--) {
			sb.append(size);
			if (amount != 1) {
				sb.append("v");
			}
		}

		return sb.toString();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		int slot = 0;
		for (Map.Entry<Arena, Game> entry : DataHandler.getArenas().entrySet()) {
			if (entry.getValue() != null) {

				addItem(slot++, getArenaIcon(entry.getValue()));
			}
		}
	}

	private Icon getArenaIcon(final Game game) {
		return new Icon(getStateMaterial(game.getGameState())).onClick(e -> {
			if (game.getGameState().equals(GameState.BATTLE) || game.getGameState().equals(GameState.ROUND_STARTING)) {
				player.closeInventory();
				game.getSpectatorData().spectate(player);
			}
		}).setName(MessageUtils.parseColor("&6" + game.getArena().getName())).setLore(MessageUtils.parseColor(Arrays.asList("","&aDurum: &6" + game.getGameState(), "&aHarita: " + game.getArena().getMapName(),"","&e&oTıkla ve izle!")));
	}

	private ItemStack getStateMaterial(GameState state) {
		switch (state) {
			case BATTLE:
				return new MaterialData(Material.WOOL, (byte) 14).toItemStack(1);
			case GAME_STARING:
				return new MaterialData(Material.WOOL, (byte) 5).toItemStack(1);
			case GAME_ENDING:
				return new MaterialData(Material.WOOL, (byte) 1).toItemStack(1);
			default:
				return new MaterialData(Material.WOOL, (byte) 0).toItemStack(1);
		}
	}
}
