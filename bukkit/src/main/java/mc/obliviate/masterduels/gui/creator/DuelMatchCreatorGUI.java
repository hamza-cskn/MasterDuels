package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.VaultUtil;
import mc.obliviate.masterduels.data.YamlStorageHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.game.MatchCreator;
import mc.obliviate.masterduels.gui.GUISerializerUtils;
import mc.obliviate.masterduels.kit.gui.KitSelectionGUI;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;

public class DuelMatchCreatorGUI extends Gui {

	private final MatchBuilder matchBuilder;
	private final MatchCreator matchCreator;
	private final MasterDuels plugin;
	private final PlaceholderUtil placeholderUtil;

	//todo open team manager gui to non-owners
	public DuelMatchCreatorGUI(Player player, MatchCreator matchCreator) {
		super(player, "duel-match-creator-gui", "Loading...", 5);
		this.matchBuilder = matchCreator.getBuilder();
		this.matchCreator = matchCreator;
		plugin = (MasterDuels) getPlugin();

		placeholderUtil = new PlaceholderUtil()
				.add("{team-amount}", matchBuilder.getTeamAmount() + "")
				.add("{team-size}", matchBuilder.getTeamSize() + "")
				.add("{round-amount}", matchBuilder.getTotalRounds() + "")
				.add("{kit}", matchBuilder.getKit() == null ? "" : matchBuilder.getKit().getKitName())
				.add("{game-time}", TimerUtils.formatTimeAsTime(matchBuilder.getMatchDuration().toMillis()))
				.add("{game-timer}", TimerUtils.formatTimeAsTimer(matchBuilder.getMatchDuration().toMillis()))
				.add("{invited-players}", matchCreator.getInvites().size() + "")
				.add("{total-players}", matchBuilder.getPlayers().size() + "");
	}

	@Override
	public void open() {
		setTitle(MessageUtils.parseColor(MessageUtils.applyPlaceholders(YamlStorageHandler.getConfig().getString(getGuiSectionPath() + ".title"),
				new PlaceholderUtil().add("{mode}", MessageUtils.convertMode(matchBuilder.getTeamSize(), matchBuilder.getTeamAmount())))));
		setSize(YamlStorageHandler.getConfig().getInt(getGuiSectionPath() + ".size", 5) * 9);
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
		return YamlStorageHandler.getConfig().getConfigurationSection(sectionName);
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
			if (matchBuilder.getTeamSize() * matchBuilder.getTeamAmount() != matchBuilder.getPlayers().size()) {
				MessageUtils.sendMessage(player, "game-builder.wrong-player-amount", new PlaceholderUtil().add("{expected}", (matchBuilder.getTeamSize() * matchBuilder.getTeamAmount()) + "").add("{found}", matchBuilder.getPlayers().size() + ""));
				return;
			}

			final Match game = matchCreator.create();
			if (game == null) {
				MessageUtils.sendMessage(player, "no-arena-found");
				return;
			}
			game.start();
		});
	}

	public Icon getTeamManagerIcon() {
		return new Icon(getConfigItem("manage-teams")).onClick(e -> {
			new DuelTeamManagerGUI(player, matchCreator).open();
		});
	}

	private Icon getRulesIcon() {
		return new Icon(getConfigItem("rules")).onClick(e -> {
			new DuelSettingsGUI(player, matchCreator).open();
		});
	}

	private Icon getInvitesIcon() {
		return new Icon(getConfigItem("invites", new PlaceholderUtil().add("{invited-players}", matchCreator.getInvites().size() + "").add("{total-players}", matchBuilder.getPlayers().size() + ""))).onClick(e -> {
			new DuelInvitesGUI(player, matchCreator).open();
		});
	}

	private Icon getKitIcon() {
		final Icon kitIcon = new Icon(getConfigItem("kit", new PlaceholderUtil().add("{kit}", matchBuilder.getKit() != null ? matchBuilder.getKit().getKitName() : MessageUtils.parseColor(MessageUtils.getMessage("game-creator.none-kit-name")))));
		return kitIcon.onClick(e -> {
			new KitSelectionGUI(player, matchBuilder, kit -> {
				open();
			}, MatchCreator.ALLOWED_KITS).open();
		});
	}

	private Icon getRoundAmountIcon() {
		final Icon roundAmountIcon = new Icon(getConfigItem("round-amount", new PlaceholderUtil().add("{round-amount}", matchBuilder.getTotalRounds() + "")));
		return roundAmountIcon.onClick(e -> {
			if (e.isRightClick()) {
				matchBuilder.setTotalRounds(Math.max(matchBuilder.getTotalRounds() - 2, MatchCreator.MIN_ROUNDS));
			} else if (e.isLeftClick()) {
				matchBuilder.setTotalRounds(Math.min(matchBuilder.getTotalRounds() + 2, MatchCreator.MAX_ROUNDS));
			}
			open();
		});
	}

	private Icon getFinishTimeIcon() {
		final Icon finishTimeIcon = new Icon(getConfigItem("game-time", new PlaceholderUtil()
				.add("{game-timer}", TimerUtils.formatTimeAsTimer(matchBuilder.getMatchDuration().toMillis()))
				.add("{game-time}", TimerUtils.formatTimeAsTime(matchBuilder.getMatchDuration().toMillis()))
		));
		return finishTimeIcon.onClick(e -> {
			if (e.isRightClick()) {
				matchBuilder.setMatchDuration(Duration.ofSeconds(Math.max(matchBuilder.getMatchDuration().toSeconds() - 30, MatchCreator.MIN_GAME_TIME)));
			} else if (e.isLeftClick()) {
				matchBuilder.setMatchDuration(Duration.ofSeconds(Math.min(matchBuilder.getMatchDuration().toSeconds() + 30, MatchCreator.MIN_GAME_TIME)));
			}
			open();
		});
	}

	private Icon getTeamAmountIcon() {
		final Icon teamAmountIcon = new Icon(getConfigItem("team-amount", new PlaceholderUtil().add("{team-amount}", matchBuilder.getTeamAmount() + "")));
		return teamAmountIcon.onClick(e -> {
			if (e.isRightClick()) {
				matchBuilder.setTeamAmount(Math.max(matchBuilder.getTeamAmount() - 1, MatchCreator.MAX_TEAM_AMOUNT));
			} else if (e.isLeftClick()) {
				matchBuilder.setTeamAmount(Math.min(matchBuilder.getTeamAmount() + 1, MatchCreator.MIN_TEAM_AMOUNT));
			}
			open();
		});
	}

	private Icon getTeamSizeIcon() {
		final Icon teamAmountIcon = new Icon(getConfigItem("team-size", new PlaceholderUtil().add("{team-size}", matchBuilder.getTeamSize() + "")));
		return teamAmountIcon.onClick(e -> {
			if (e.isRightClick()) {
				matchBuilder.setTeamSize(Math.max(matchBuilder.getTeamSize() - 1, MatchCreator.MIN_TEAM_SIZE));
			} else if (e.isLeftClick()) {
				matchBuilder.setTeamSize(Math.min(matchBuilder.getTeamSize() + 1, MatchCreator.MAX_TEAM_SIZE));
			}
			open();
		});
	}


}
