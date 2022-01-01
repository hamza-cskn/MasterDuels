package mc.obliviate.blokduels.gui.room;

import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameBuilder;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.xmaterial.XMaterial;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.List;

public class DuelGameCreatorGUI extends GUI {

	private final GameBuilder gameBuilder;

	public DuelGameCreatorGUI(Player player, GameBuilder gameBuilder) {
		super(player, "duel-game-creator-gui", "Loading...", 5);
		this.gameBuilder = gameBuilder;
	}

	@Override
	public void open() {
		setTitle("Duel Creator: " + MessageUtils.convertMode(gameBuilder.getTeamSize(), gameBuilder.getTeamAmount()));
		super.open();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()).setDamage(15),4);
		addItem(10, getTeamAmountIcon());
		addItem(12, getTeamSizeIcon());
		addItem(14, getInvitesIcon());
		addItem(16, getSettingsIcon());
		addItem(40, getStartGameIcon());
	}

	private Icon getStartGameIcon() {
		return new Icon(XMaterial.EMERALD_BLOCK.parseItem()).setName("Click to start game").onClick(e -> {
			if (gameBuilder.getTeamSize() * gameBuilder.getTeamAmount() != gameBuilder.getPlayers().size()) return;
			for (int i = gameBuilder.getTeamAmount(); i > 0; i--) {
				final List<Player> playerList = gameBuilder.getPlayers().subList((i - 1) * gameBuilder.getTeamSize(), i * gameBuilder.getTeamAmount());
				gameBuilder.createTeam(playerList);
			}

			final Game game = gameBuilder.build();
			if (game == null) {
				MessageUtils.sendMessage(player,"no-arena-found");
				return;
			}
			game.startGame();
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
		final Icon teamAmountIcon = new Icon(XMaterial.MINECART.parseItem()).setName(MessageUtils.parseColor("&aTeam Amount")).setLore(MessageUtils.parseColor("&bCurrently: &f" + gameBuilder.getTeamAmount()), "", MessageUtils.parseColor("&eLeft click to increase"), MessageUtils.parseColor("&eRight click to decrease"));
		return teamAmountIcon.onClick(e -> {
			if (e.isRightClick()) {
				gameBuilder.teamAmount(Math.max(gameBuilder.getTeamAmount() - 1, 2));
			} else if (e.isLeftClick()) {
				gameBuilder.teamAmount(gameBuilder.getTeamAmount() + 1);
			}
			open();
		});
	}

	private Icon getTeamSizeIcon() {
		final Icon teamAmountIcon = new Icon(XMaterial.PLAYER_HEAD.parseItem()).setName(MessageUtils.parseColor("&aTeam Size")).setLore(MessageUtils.parseColor("&bCurrently: &f" + gameBuilder.getTeamSize()), "", MessageUtils.parseColor("&eLeft click to increase"), MessageUtils.parseColor("&eRight click to decrease"));
		return teamAmountIcon.onClick(e -> {
			if (e.isRightClick()) {
				gameBuilder.teamSize(Math.max(gameBuilder.getTeamSize() - 1, 1));
			} else if (e.isLeftClick()) {
				gameBuilder.teamSize(gameBuilder.getTeamSize() + 1);
			}
			open();
		});
	}


}
