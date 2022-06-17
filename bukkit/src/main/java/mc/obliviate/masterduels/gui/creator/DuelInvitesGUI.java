package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.api.invite.InviteState;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.game.GameCreator;
import mc.obliviate.masterduels.invite.Invite;
import mc.obliviate.masterduels.setup.chatentry.ChatEntry;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelInvitesGUI extends Gui {

	private final GameBuilder gameBuilder;
	private final GameCreator gameCreator;

	public DuelInvitesGUI(Player player, GameCreator gameCreator) {
		super(player, "duel-invites-gui", "Duel Invites", 6);
		this.gameBuilder = gameCreator.getBuilder();
		this.gameCreator = gameCreator;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);
		addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
			new DuelGameCreatorGUI(player, gameCreator).open();
		}));
		addItem(4, new Icon(XMaterial.WRITABLE_BOOK.parseItem()).setName(MessageUtils.parseColor("&aInvite a player")).setLore(MessageUtils.parseColor("&7Players: " + gameBuilder.getPlayers().size())).onClick(e -> {
			player.closeInventory();
			new ChatEntry(player.getUniqueId(), getPlugin()).onResponse(chatEvent -> {
				gameCreator.trySendInvite(player, Bukkit.getPlayer(chatEvent.getMessage()), response -> {
					gameBuilder.addPlayer(Bukkit.getPlayer(chatEvent.getMessage()));
				});
				open();
			});
			MessageUtils.sendMessage(player, "enter-player-name-to-invite");
		}));
		int i = 9;
		for (Invite invite : gameCreator.getInvites().values()) {
			addItem(i++, new Icon(XMaterial.MAP.parseItem()).setName(Bukkit.getPlayer(invite.getRecipientUniqueId()).getName()).onClick(e -> {
				invite.response(InviteState.ACCEPTED);
			}));
		}
	}

}
