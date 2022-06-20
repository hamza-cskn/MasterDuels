package mc.obliviate.masterduels.gui;

import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.arena.BasicArenaState;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

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
		final PlaceholderUtil placeholderUtil = new PlaceholderUtil().add("{arena}", arena.getName()).add("{map}", arena.getMapName());
		final Game game = DataHandler.getArenas().get(arena);
		final BasicArenaState state = Arena.getBasicArenaState(arena);
		if (game != null) {
			final int players = game.getGameBuilder().getPlayers().size();
			final int spectators = game.getSpectatorManager().getAllSpectators().size();
			placeholderUtil.add("{players}", players + "");
			placeholderUtil.add("{spectators}", spectators + "");
			placeholderUtil.add("{playersandspectators}", (spectators + players) + "");
			placeholderUtil.add("{kit}", game.getKit() == null ? "" : game.getKit().getKitName());
			placeholderUtil.add("{mode}", MessageUtils.convertMode(game.getGameBuilder().getTeamSize(), game.getGameBuilder().getTeamAmount()));
		}
		final Icon icon = new Icon(guiConfig.getIcon(state, placeholderUtil));
		icon.onClick(e -> {
			final Game g = DataHandler.getArenas().get(arena);
			if (g != null) {
				g.spectate(player);
			}
		});
		return icon;
	}

	/**
	 * Purpose of this class is storing duel arena list
	 * gui configuration datas.
	 */
	public static class DuelArenaListGUIConfig {

		protected final Map<BasicArenaState, ItemStack> icons;
		protected final String guiTitle;

		public DuelArenaListGUIConfig(final Map<BasicArenaState, ItemStack> icons, final String guiTitle) {
			this.icons = icons;
			this.guiTitle = guiTitle;
		}

		protected ItemStack getIcon(final BasicArenaState state, final PlaceholderUtil placeholderUtil) {
			return SerializerUtils.applyPlaceholdersOnItemStack(icons.get(state).clone(), placeholderUtil);
		}
	}


}
