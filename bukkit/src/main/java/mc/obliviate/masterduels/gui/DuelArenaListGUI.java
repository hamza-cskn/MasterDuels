package mc.obliviate.masterduels.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.arena.BasicArenaState;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class DuelArenaListGUI extends ConfigurableGui {

	private static Config guiConfig;

	public DuelArenaListGUI(Player player) {
		super(player, "duel-games-list-gui");
		getPaginationManager().getSlots().addAll(guiConfig.pageSlots);
		for (final Map.Entry<Arena, Match> entry : DataHandler.getArenas().entrySet()) {
			getPaginationManager().addIcon(getGameIcon(entry.getKey()));
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		if (getPaginationManager().getPage() != getPaginationManager().getLastPage()) {
			putIcon("previous", e -> {
				getPaginationManager().previousPage();
				getPaginationManager().update();
			});
		}
		if (getPaginationManager().getPage() != 0) {
			putIcon("next", e -> {
				getPaginationManager().nextPage();
				getPaginationManager().update();
			});
		}
		getPaginationManager().update();
	}

	private Icon getGameIcon(final Arena arena) {
		final PlaceholderUtil placeholderUtil = new PlaceholderUtil().add("{arena}", arena.getName()).add("{map}", arena.getMapName());
		final Match game = DataHandler.getArenas().get(arena);
		final BasicArenaState state = Arena.getBasicArenaState(arena);
		if (game != null) {
			final int players = game.getGameDataStorage().getGameTeamManager().getAllMembers().size();
			final int spectators = game.getGameSpectatorManager().getAllSpectators().size();
			placeholderUtil.add("{players}", players + "");
			placeholderUtil.add("{spectators}", spectators + "");
			placeholderUtil.add("{playersandspectators}", (spectators + players) + "");
			placeholderUtil.add("{mode}", MessageUtils.convertMode(game.getGameDataStorage().getGameTeamManager().getTeamSize(), game.getGameDataStorage().getGameTeamManager().getTeamAmount()));
		}
		final Icon icon = new Icon(guiConfig.getIcon(state, placeholderUtil));
		icon.onClick(e -> {
			final Match updatedGameObject = DataHandler.getArenas().get(arena);
			if (updatedGameObject != null) {
				updatedGameObject.getGameSpectatorManager().spectate(player);
			}
		});
		return icon;
	}

	@Override
	public String getSectionPath() {
		return "duel-arenas-gui";
	}

	/**
	 * Purpose of this class is storing duel arena list
	 * gui configuration datas.
	 */
	public static class Config {

		private final Map<BasicArenaState, ItemStack> icons;
		private final List<Integer> pageSlots;

		public Config(final Map<BasicArenaState, ItemStack> icons, List<Integer> pageSlots) {
			this.icons = icons;
			this.pageSlots = pageSlots;
			DuelArenaListGUI.guiConfig = this;
		}

		private ItemStack getIcon(final BasicArenaState state, final PlaceholderUtil placeholderUtil) {
			return SerializerUtils.applyPlaceholdersOnItemStack(icons.get(state).clone(), placeholderUtil);
		}
	}


}
