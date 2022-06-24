package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.api.invite.InviteState;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.game.MatchCreator;
import mc.obliviate.masterduels.invite.Invite;
import mc.obliviate.masterduels.setup.chatentry.ChatEntry;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelInvitesGUI extends Gui {

	private final MatchBuilder matchBuilder;
	private final MatchCreator matchCreator;

	public DuelInvitesGUI(Player player, MatchCreator matchCreator) {
		super(player, "duel-invites-gui", "Duel Invites", 6);
		this.matchBuilder = matchCreator.getBuilder();
		this.matchCreator = matchCreator;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);
		addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
			new DuelMatchCreatorGUI(player, matchCreator).open();
		}));
		addItem(4, new Icon(XMaterial.WRITABLE_BOOK.parseItem()).setName(MessageUtils.parseColor("&aInvite a player")).setLore(MessageUtils.parseColor("&7Players: " + matchBuilder.getPlayers().size())).onClick(e -> {
			player.closeInventory();
			new ChatEntry(player.getUniqueId(), getPlugin()).onResponse(chatEvent -> {
				matchCreator.trySendInvite(player, Bukkit.getPlayer(chatEvent.getMessage()), response -> {
					matchBuilder.addPlayer(Bukkit.getPlayer(chatEvent.getMessage()));
				});
				open();
			});
			MessageUtils.sendMessage(player, "enter-player-name-to-invite");
		}));
		int i = 9;
		for (Invite invite : matchCreator.getInvites().values()) {
			addItem(i++, new Icon(XMaterial.MAP.parseItem()).setName(Bukkit.getPlayer(invite.getRecipientUniqueId()).getName()).onClick(e -> {
				invite.response(InviteState.ACCEPTED);
			}));
		}
	}

}
