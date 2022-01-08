package mc.obliviate.masterduels.gui;

import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.arena.BasicArenaState;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class DuelArenaListGUI extends GUI {

	public static DuelArenaListGUIConfig guiConfig;

	public DuelArenaListGUI(Player player) {
		super(player, "duel-games-list-gui", "Duel Arenas", 6);
		setTitle(guiConfig.guiTitle);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		int slot = 0;
		for (final Map.Entry<Arena, Game> entry : DataHandler.getArenas().entrySet()) {
			addItem(slot++, getGameIcon(entry.getKey()));
		}
	}

	private Icon getGameIcon(final Arena arena) {
		final PlaceholderUtil placeholderUtil = new PlaceholderUtil().add("{arena}", arena.getName()).add("{map}", arena.getName());
		final Game game = DataHandler.getArenas().get(arena);
		final BasicArenaState state = Arena.getBasicArenaState(arena);
		if (game != null) {
			final int players = game.getGameBuilder().getPlayers().size();
			final int spectators = game.getSpectatorData().getSpectators().size();
			placeholderUtil.add("{players}", players + "");
			placeholderUtil.add("{spectators}", spectators + "");
			placeholderUtil.add("{playersandspectators}", (spectators + players) + "");
			placeholderUtil.add("{kit}", game.getKit() == null ? "" : game.getKit().getKitName());
			placeholderUtil.add("{mode}", MessageUtils.convertMode(game.getGameBuilder().getTeamSize(), game.getGameBuilder().getTeamAmount()));
		}
		final Icon icon = new Icon(guiConfig.gameStateMaterial.get(state).clone());
		icon.setName(MessageUtils.parseColor(MessageUtils.applyPlaceholders(guiConfig.gameIconDescription.get(state).get(0), placeholderUtil)));

		for (final String line : guiConfig.gameIconDescription.get(state).subList(1, guiConfig.gameIconDescription.size() - 1)) {
			icon.appendLore(MessageUtils.parseColor(MessageUtils.applyPlaceholders(line, placeholderUtil)));
		}
		return icon;
	}

	/**
	 * Purpose of this class is storing duel arena list
	 * gui configuration datas.
	 */
	public static class DuelArenaListGUIConfig {
		protected final Map<BasicArenaState, ItemStack> gameStateMaterial;
		protected final Map<BasicArenaState, List<String>> gameIconDescription;
		protected final String guiTitle;

		public DuelArenaListGUIConfig(Map<BasicArenaState, ItemStack> gameStateMaterial, Map<BasicArenaState, List<String>> gameIconDescription, String guiTitle) {
			this.gameStateMaterial = gameStateMaterial;
			this.gameIconDescription = gameIconDescription;
			this.guiTitle = guiTitle;
		}

	}
}
