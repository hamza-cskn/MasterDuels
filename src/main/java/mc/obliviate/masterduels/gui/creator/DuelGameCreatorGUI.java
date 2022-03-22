package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.VaultUtil;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.game.GameCreator;
import mc.obliviate.masterduels.gui.GUISerializerUtils;
import mc.obliviate.masterduels.kit.gui.KitSelectionGUI;
import mc.obliviate.masterduels.setup.chatentry.ChatEntry;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class DuelGameCreatorGUI extends GUI {

	private final GameBuilder gameBuilder;
	private final GameCreator gameCreator;
	private final MasterDuels plugin;
	private final PlaceholderUtil placeholderUtil;

	//todo open team manager gui to non-owners
	public DuelGameCreatorGUI(Player player, GameCreator gameCreator) {
		super(player, "duel-game-creator-gui", "Loading...", 5);
		this.gameBuilder = gameCreator.getBuilder();
		this.gameCreator = gameCreator;
		plugin = (MasterDuels) getPlugin();

		placeholderUtil = new PlaceholderUtil()
				.add("{team-amount}", gameBuilder.getTeamAmount() + "")
				.add("{team-size}", gameBuilder.getTeamSize() + "")
				.add("{round-amount}", gameBuilder.getTotalRounds() + "")
				.add("{kit}", gameBuilder.getKit() == null ? "" : gameBuilder.getKit().getKitName())
				.add("{game-time}", TimerUtils.formatTimeAsTime(gameBuilder.getFinishTime()))
				.add("{game-timer}", TimerUtils.formatTimeAsTimer(gameBuilder.getFinishTime()))
				.add("{invited-players}", gameCreator.getInvites().size() + "")
				.add("{total-players}", gameBuilder.getPlayers().size() + "");
	}

	@Override
	public void open() {
		setTitle(MessageUtils.parseColor(MessageUtils.applyPlaceholders(plugin.getDatabaseHandler().getConfig().getString(getGuiSectionPath() + ".title"),
				new PlaceholderUtil().add("{mode}", MessageUtils.convertMode(gameBuilder.getTeamSize(), gameBuilder.getTeamAmount())))));
		setSize(plugin.getDatabaseHandler().getConfig().getInt(getGuiSectionPath() + ".size", 5) * 9);
		super.open();
	}

	private String getSectionPath() {
		return "duel-creator";
	}

	private String getGuiSectionPath() {
		return getSectionPath() + ".gui";
	}

	private String getIconsSectionPath() {
		return getGuiSectionPath() + ".icons";
	}

	private ConfigurationSection getConfigSection(String sectionName) {
		return plugin.getDatabaseHandler().getConfig().getConfigurationSection(sectionName);
	}

	private int getConfigSlot(String sectionName) {
		return GUISerializerUtils.getConfigSlot(getConfigSection(getIconsSectionPath() + "." + sectionName));
	}

	private ItemStack getConfigItem(String sectionName) {
		return GUISerializerUtils.getConfigItem(getConfigSection(getIconsSectionPath() + "." + sectionName));
	}

	private ItemStack getConfigItem(String sectionName, PlaceholderUtil placeholderUtil) {
		return GUISerializerUtils.getConfigItem(getConfigSection(getIconsSectionPath() + "." + sectionName), placeholderUtil);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		GUISerializerUtils.putDysfunctionalIcons(this, getConfigSection(getIconsSectionPath()));

		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.teamsize"))
			addItem(getConfigSlot("team-size"), getTeamSizeIcon());
		//game creator is dysfunctional if owner can't invite anyone.
		addItem(getConfigSlot("invites"), getInvitesIcon());
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.rules"))
			addItem(getConfigSlot("rules"), getRulesIcon());
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.teamamount"))
			addItem(getConfigSlot("team-amount"), getTeamAmountIcon());
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.teammanager"))
			addItem(getConfigSlot("manage-teams"), getTeamManagerIcon());
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.roundamount"))
			addItem(getConfigSlot("round-amount"), getRoundAmountIcon());
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.kit"))
			addItem(getConfigSlot("kit"), getKitIcon());
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.finishtime"))
			addItem(getConfigSlot("game-time"), getFinishTimeIcon());
		addItem(getConfigSlot("start-game"), getStartGameIcon());
	}

	private Icon getStartGameIcon() {
		return new Icon(getConfigItem("start-game")).onClick(e -> {
			if (gameBuilder.getTeamSize() * gameBuilder.getTeamAmount() != gameBuilder.getPlayers().size()) {
				MessageUtils.sendMessage(player, "game-builder.wrong-player-amount", new PlaceholderUtil().add("{expected}", (gameBuilder.getTeamSize() * gameBuilder.getTeamAmount()) + "").add("{found}", gameBuilder.getPlayers().size() + ""));
				return;
			}

			final Game game = gameCreator.create();
			if (game == null) {
				MessageUtils.sendMessage(player, "no-arena-found");
				return;
			}
			game.startGame();
		});
	}

	public Icon getTeamManagerIcon() {
		return new Icon(getConfigItem("manage-teams")).onClick(e -> {
			new DuelTeamManagerGUI(player, gameCreator).open();
		});
	}

	private Icon getRulesIcon() {
		return new Icon(getConfigItem("rules")).onClick(e -> {
			new DuelSettingsGUI(player, gameCreator).open();
		});
	}

	private Icon getInvitesIcon() {
		return new Icon(getConfigItem("invites", new PlaceholderUtil().add("{invited-players}", gameCreator.getInvites().size() + "").add("{total-players}", gameBuilder.getPlayers().size() + ""))).onClick(e -> {
			new DuelInvitesGUI(player, gameCreator).open();
		});
	}

	private Icon getKitIcon() {
		final Icon kitIcon = new Icon(getConfigItem("kit", new PlaceholderUtil().add("{kit}", gameBuilder.getKit() != null ? gameBuilder.getKit().getKitName() : MessageUtils.parseColor(MessageUtils.getMessage("game-creator.none-kit-name")))));
		return kitIcon.onClick(e -> {
			new KitSelectionGUI(player, gameBuilder, kit -> {
				open();
			}).open();
		});
	}

	private Icon getRoundAmountIcon() {
		final Icon roundAmountIcon = new Icon(getConfigItem("round-amount", new PlaceholderUtil().add("{round-amount}", gameBuilder.getTotalRounds() + "")));
		return roundAmountIcon.onClick(e -> {
			if (e.isRightClick()) {
				gameBuilder.setTotalRounds(Math.max(gameBuilder.getTotalRounds() - 2, 1));
			} else if (e.isLeftClick()) {
				gameBuilder.setTotalRounds(Math.min(gameBuilder.getTotalRounds() + 2, 5));
			}
			open();
		});
	}

	private Icon getFinishTimeIcon() {
		final Icon finishTimeIcon = new Icon(getConfigItem("game-time", new PlaceholderUtil()
				.add("{game-timer}", TimerUtils.formatTimeAsTimer(gameBuilder.getFinishTime()))
				.add("{game-time}", TimerUtils.formatTimeAsTime(gameBuilder.getFinishTime()))
		));
		return finishTimeIcon.onClick(e -> {
			if (e.isRightClick()) {
				gameBuilder.setFinishTime(Math.max(gameBuilder.getFinishTime() - 30, 60));
			} else if (e.isLeftClick()) {
				gameBuilder.setFinishTime(Math.min(gameBuilder.getFinishTime() + 30, 600));
			}
			open();
		});
	}

	private Icon getTeamAmountIcon() {
		final Icon teamAmountIcon = new Icon(getConfigItem("team-amount", new PlaceholderUtil().add("{team-amount}", gameBuilder.getTeamAmount() + "")));
		return teamAmountIcon.onClick(e -> {
			if (e.isRightClick()) {
				gameBuilder.setTeamAmount(Math.max(gameBuilder.getTeamAmount() - 1, 2));
			} else if (e.isLeftClick()) {
				gameBuilder.setTeamAmount(Math.min(gameBuilder.getTeamAmount() + 1, 10));
			}
			open();
		});
	}

	private Icon getTeamSizeIcon() {
		final Icon teamAmountIcon = new Icon(getConfigItem("team-size", new PlaceholderUtil().add("{team-size}", gameBuilder.getTeamSize() + "")));
		return teamAmountIcon.onClick(e -> {
			if (e.isRightClick()) {
				gameBuilder.setTeamSize(Math.max(gameBuilder.getTeamSize() - 1, 1));
			} else if (e.isLeftClick()) {
				gameBuilder.setTeamSize(Math.min(gameBuilder.getTeamSize() + 1, 8));
			}
			open();
		});
	}


}
