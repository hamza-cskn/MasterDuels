package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.game.bet.Bet;
import mc.obliviate.masterduels.setup.chatentry.ChatEntry;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelGameCreatorGUI extends GUI {

	private final GameBuilder gameBuilder;

	//todo open team manager gui to non-owners
	public DuelGameCreatorGUI(Player player, GameBuilder gameBuilder) {
		super(player, "duel-game-creator-gui", "Loading...", 5);
		this.gameBuilder = gameBuilder;
	}

	@Override
	public void open() {
		setTitle("Duel Creator: " + MessageUtils.convertMode(gameBuilder.getTeamSize(), gameBuilder.getTeamAmount()) + " §c§l BETA");
		super.open();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()).setDamage(15), 4);
		addItem(10, getTeamAmountIcon());
		addItem(11, getTeamSizeIcon());
		addItem(12, getInvitesIcon());
		addItem(13, getSettingsIcon());
		addItem(14, getTeamManagerIcon());
		addItem(15, getRoundAmountIcon());
		if (Bet.betsEnabled) {
			addItem(22, getBetIcon());
		}
		addItem(16, getFinishTimeIcon());
		addItem(40, getStartGameIcon());
	}

	private Icon getStartGameIcon() {
		return new Icon(XMaterial.EMERALD_BLOCK.parseItem()).setName("§aClick to start game").onClick(e -> {
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
		return new Icon(XMaterial.PAINTING.parseItem()).setName(MessageUtils.parseColor("&aManage Teams")).onClick(e -> {
			new DuelTeamManagerGUI(player, gameBuilder).open();
		});
	}

	private Icon getSettingsIcon() {
		return new Icon(XMaterial.COMPARATOR.parseItem()).setName(MessageUtils.parseColor("&aSettings")).onClick(e -> {
			new DuelSettingsGUI(player, gameBuilder).open();
		});
	}

	private Icon getInvitesIcon() {
		return new Icon(XMaterial.WRITABLE_BOOK.parseItem()).setName(MessageUtils.parseColor("&aInvites")).onClick(e -> {
			new DuelInvitesGUI(player, gameBuilder).open();
		});
	}

	private Icon getBetIcon() {
		final Icon betIcon = new Icon(XMaterial.EMERALD.parseItem()).setName(MessageUtils.parseColor("&aBet")).setLore(MessageUtils.parseColor("&bCurrently: &f" + gameBuilder.getBet().getMoney() + " money"), "", MessageUtils.parseColor("&eClick to change bet"));
		return betIcon.onClick(e -> {
			player.closeInventory();
			new ChatEntry(player.getUniqueId(), getPlugin()).onResponse(event -> {
				try {
					gameBuilder.getBet().setMoney(Integer.parseInt(event.getMessage()));
				} catch (NumberFormatException exception) {
					MessageUtils.sendMessage(event.getPlayer(), "invalid-number", new PlaceholderUtil().add("{entry}", event.getMessage()));
				} finally {
					open();
				}
			});
		});
	}

	private Icon getRoundAmountIcon() {
		final Icon roundAmountIcon = new Icon(XMaterial.DIAMOND_HORSE_ARMOR.parseItem()).setName(MessageUtils.parseColor("&aRound Amount")).setLore(MessageUtils.parseColor("&bCurrently: &f" + gameBuilder.getTotalRounds() + " rounds"), "", MessageUtils.parseColor("&eLeft click to increase"), MessageUtils.parseColor("&eRight click to decrease"));
		return roundAmountIcon.onClick(e -> {
			if (e.isRightClick()) {
				gameBuilder.setTotalRounds(Math.max(gameBuilder.getTotalRounds() - 1, 1));
			} else if (e.isLeftClick()) {
				gameBuilder.setTotalRounds(Math.min(gameBuilder.getTotalRounds() + 1, 5));
			}
			open();
		});
	}

	private Icon getFinishTimeIcon() {
		final Icon finishTimeIcon = new Icon(XMaterial.CLOCK.parseItem()).setName(MessageUtils.parseColor("&aGame Time")).setLore(MessageUtils.parseColor("&bCurrently: &f" + MessageUtils.getFirstDigits(gameBuilder.getFinishTime() / 60d, 1) + " minutes"), "", MessageUtils.parseColor("&eLeft click to increase"), MessageUtils.parseColor("&eRight click to decrease"));
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
		final Icon teamAmountIcon = new Icon(XMaterial.MINECART.parseItem()).setName(MessageUtils.parseColor("&aTeam Amount")).setLore(MessageUtils.parseColor("&bCurrently: &f" + gameBuilder.getTeamAmount()), "", MessageUtils.parseColor("&c&lWARNING:&c If you change team"), MessageUtils.parseColor("&camount, all teams will reset"), "", MessageUtils.parseColor("&eLeft click to increase"), MessageUtils.parseColor("&eRight click to decrease"));
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
		final Icon teamAmountIcon = new Icon(XMaterial.PLAYER_HEAD.parseItem()).setName(MessageUtils.parseColor("&aTeam Size")).setLore(MessageUtils.parseColor("&bCurrently: &f" + gameBuilder.getTeamSize()), "", MessageUtils.parseColor("&c&lWARNING:&c If you change team"), MessageUtils.parseColor("&camount, all teams will reset"), "", MessageUtils.parseColor("&eLeft click to increase"), MessageUtils.parseColor("&eRight click to decrease"));
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
