package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.VaultUtil;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.game.bet.Bet;
import mc.obliviate.masterduels.gui.kit.KitSelectionGUI;
import mc.obliviate.masterduels.setup.chatentry.ChatEntry;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class DuelGameCreatorGUI extends GUI {

	private final GameBuilder gameBuilder;
	private final MasterDuels plugin;
	private final PlaceholderUtil placeholderUtil;

	//todo open team manager gui to non-owners
	public DuelGameCreatorGUI(Player player, GameBuilder gameBuilder) {
		super(player, "duel-game-creator-gui", "Loading...", 5);
		this.gameBuilder = gameBuilder;
		plugin = (MasterDuels) getPlugin();

		placeholderUtil = new PlaceholderUtil()
				.add("{team-amount}", gameBuilder.getTeamAmount() + "")
				.add("{team-size}", gameBuilder.getTeamSize() + "")
				.add("{round-amount}", gameBuilder.getTotalRounds() + "")
				.add("{kit}", gameBuilder.getKit() == null ? "" : gameBuilder.getKit().getKitName())
				.add("{bet}", gameBuilder.getBet().getMoney() + "")
				.add("{game-time}", TimerUtils.formatDifferentTime(gameBuilder.getFinishTime()))
				.add("{game-timer}", TimerUtils.formatDifferentTimer(gameBuilder.getFinishTime()))
				.add("{invited-players}", gameBuilder.getInvites().size() + "")
				.add("{total-players}", gameBuilder.getPlayers().size() + "");
	}

	@Override
	public void open() {
		setTitle(MessageUtils.parseColor(MessageUtils.applyPlaceholders(plugin.getDatabaseHandler().getConfig().getString("duel-creator.gui.title"),
				new PlaceholderUtil().add("{mode}", MessageUtils.convertMode(gameBuilder.getTeamSize(), gameBuilder.getTeamAmount())))));
		super.open();
	}

	private void putDysfunctionalIcons() {
		final ConfigurationSection configurationSection = plugin.getDatabaseHandler().getConfig().getConfigurationSection("duel-creator.gui.icons");
		for (String sectionName : configurationSection.getKeys(false)) {
			final ConfigurationSection section = configurationSection.getConfigurationSection(sectionName);

			int slotNo = section.getInt("slot", -1);
			if (slotNo != -1) {
				addItem(slotNo, getConfigItem(sectionName));
				return;
			}


			final String slotString = section.getString("slot", "");
			if (slotString.contains("-")) {
				final String[] slots = slotString.split("-");
				if (slots.length != 2) continue;
				int from, to;
				try {
					from = Integer.parseInt(slots[0]);
					to = Integer.parseInt(slots[1]);
				} catch (NumberFormatException e) {
					continue;
				}
				if (from > to) continue;
				for (; from <= to; from++) {
					addItem(from, getConfigItem(sectionName));
				}
				return;
			}
			if (slotString.contains(",")) {
				final String[] slots = slotString.split(",");
				if (slots.length < 2) continue;

				for (final String slotText : slots) {
					try {
						addItem(Integer.parseInt(slotText), getConfigItem(sectionName));
					} catch (NumberFormatException ignore) {
					}
				}
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		putDysfunctionalIcons();

		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.teamsize"))
			addItem(getConfigSlot("team-size"), getTeamSizeIcon());
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.invite"))
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
		if (Bet.betsEnabled) {
			addItem(getConfigSlot("bet"), getBetIcon());
		}
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

			final Game game = gameBuilder.build();
			if (game == null) {
				MessageUtils.sendMessage(player, "no-arena-found");
				return;
			}
			game.startGame();
		});
	}

	public Icon getTeamManagerIcon() {
		return new Icon(getConfigItem("manage-teams")).onClick(e -> {
			new DuelTeamManagerGUI(player, gameBuilder).open();
		});
	}

	private Icon getRulesIcon() {
		return new Icon(getConfigItem("rules")).onClick(e -> {
			new DuelSettingsGUI(player, gameBuilder).open();
		});
	}

	private Icon getInvitesIcon() {
		return new Icon(getConfigItem("invites")).onClick(e -> {
			new DuelInvitesGUI(player, gameBuilder).open();
		});
	}

	private Icon getBetIcon() {
		final Icon betIcon = new Icon(getConfigItem("bet"));
		return betIcon.onClick(e -> {
			player.closeInventory();
			new ChatEntry(player.getUniqueId(), getPlugin()).onResponse(event -> {
				try {
					gameBuilder.getBet().setMoney(Integer.parseInt(event.getMessage()));
					MessageUtils.sendMessage(player, "enter-bet-amount");
				} catch (NumberFormatException exception) {
					MessageUtils.sendMessage(player, "invalid-number", new PlaceholderUtil().add("{entry}", event.getMessage()));
				} finally {
					open();
				}
			});
		});
	}

	private Icon getKitIcon() {
		final Icon kitIcon = new Icon(getConfigItem("kit"));
		return kitIcon.onClick(e -> {
			new KitSelectionGUI(player, gameBuilder, kit -> {
				open();
			}).open();
		});
	}

	private Icon getRoundAmountIcon() {
		final Icon roundAmountIcon = new Icon(getConfigItem("round-amount"));
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
		final Icon finishTimeIcon = new Icon(getConfigItem("game-time"));
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
		final Icon teamAmountIcon = new Icon(getConfigItem("team-amount"));
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
		final Icon teamAmountIcon = new Icon(getConfigItem("team-size"));
		return teamAmountIcon.onClick(e -> {
			if (e.isRightClick()) {
				gameBuilder.setTeamSize(Math.max(gameBuilder.getTeamSize() - 1, 1));
			} else if (e.isLeftClick()) {
				gameBuilder.setTeamSize(Math.min(gameBuilder.getTeamSize() + 1, 8));
			}
			open();
		});
	}

	private ItemStack getConfigItem(String itemName) {
		return getConfigItem(itemName, placeholderUtil);
	}

	private ItemStack getConfigItem(String itemName, PlaceholderUtil placeholderUtil) {
		return SerializerUtils.deserializeItemStack(plugin.getDatabaseHandler().getConfig().getConfigurationSection("duel-creator.gui.icons." + itemName), placeholderUtil);
	}

	private int getConfigSlot(String itemName) {
		return plugin.getDatabaseHandler().getConfig().getConfigurationSection("duel-creator.gui.icons." + itemName).getInt("slot");
	}


}
