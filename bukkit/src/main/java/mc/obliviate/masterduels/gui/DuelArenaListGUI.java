package mc.obliviate.masterduels.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.inventory.configurable.util.ItemStackSerializer;
import mc.obliviate.inventory.pagination.PaginationManager;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.arena.BasicArenaState;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class DuelArenaListGUI extends ConfigurableGui {

	private static Config guiConfig;
	private final PaginationManager paginationManager = new PaginationManager(this);

	public DuelArenaListGUI(Player player) {
		super(player, "duel-games-list-gui");
		this.paginationManager.getSlots().addAll(guiConfig.pageSlots);
		for (final Map.Entry<Arena, Match> entry : DataHandler.getArenas().entrySet()) {
			this.paginationManager.addItem(getGameIcon(entry.getKey()));
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		if (this.paginationManager.getCurrentPage() != this.paginationManager.getLastPage()) {
			putIcon("previous", e -> {
				this.paginationManager.goPreviousPage().update();
			});
		}
		if (this.paginationManager.getCurrentPage() != 0) {
			putIcon("next", e -> {
				this.paginationManager.goNextPage().update();
			});
		}
		this.paginationManager.update();
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
			final ItemStack item = icons.get(state).clone();
			ItemStackSerializer.applyPlaceholdersToItemStack(item, placeholderUtil);
			return item;
		}
	}


}
