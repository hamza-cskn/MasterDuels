package mc.obliviate.masterduels.gui.room;

import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.List;

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
		addItem(12, getTeamSizeIcon());
		addItem(14, getInvitesIcon());
		addItem(16, getSettingsIcon());
		addItem(31, getTeamManagerIcon());
		addItem(40, getStartGameIcon());
	}

	private Icon getStartGameIcon() {
		return new Icon(XMaterial.EMERALD_BLOCK.parseItem()).setName("Click to start game").onClick(e -> {
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
		return new Icon(XMaterial.PAINTING.parseItem()).onClick(e -> {
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

	private Icon getTeamAmountIcon() {
		final Icon teamAmountIcon = new Icon(XMaterial.MINECART.parseItem()).setName(MessageUtils.parseColor("&aTeam Amount")).setLore(MessageUtils.parseColor("&bCurrently: &f" + gameBuilder.getTeamAmount()), "", MessageUtils.parseColor("&c&lWARNING:&c If you change team"), MessageUtils.parseColor("&camount, all teams will reset"), "", MessageUtils.parseColor("&eLeft click to increase"), MessageUtils.parseColor("&eRight click to decrease"));
		return teamAmountIcon.onClick(e -> {
			if (e.isRightClick()) {
				gameBuilder.setTeamAmount(Math.max(gameBuilder.getTeamAmount() - 1, 2));
			} else if (e.isLeftClick()) {
				gameBuilder.setTeamAmount(gameBuilder.getTeamAmount() + 1);
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
				gameBuilder.setTeamSize(gameBuilder.getTeamSize() + 1);
			}
			open();
		});
	}


}
