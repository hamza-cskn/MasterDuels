package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.game.MatchCreator;
import mc.obliviate.masterduels.gui.ConfigurableGui;
import mc.obliviate.masterduels.invite.Invite;
import mc.obliviate.masterduels.setup.chatentry.ChatEntry;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelInvitesGUI extends ConfigurableGui {

	private final MatchCreator matchCreator;

	public DuelInvitesGUI(Player player, MatchCreator matchCreator) {
		super(player, "duel-invites-gui");
		this.matchCreator = matchCreator;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		putDysfunctionalIcons();

		putIcon("back", e -> {
			new DuelMatchCreatorGUI(player, matchCreator).open();
		});

		putIcon("invite", new PlaceholderUtil().add("{players-amount}", matchCreator.getBuilder().getPlayers().size() + "").add("{pending-invites-amount}", matchCreator.getInvites().size() + ""), e -> {
			player.closeInventory();
			new ChatEntry(player.getUniqueId(), getPlugin()).onResponse(chatEvent -> {
				matchCreator.trySendInvite(player, Bukkit.getPlayer(chatEvent.getMessage()), response -> {
					matchCreator.getBuilder().addPlayer(Bukkit.getPlayer(chatEvent.getMessage()));
				});
				open();
			});
			MessageUtils.sendMessage(player, "enter-player-name-to-invite");
			int i = 9;
			for (Invite invite : matchCreator.getInvites().values()) {
				addItem(i++, new Icon(XMaterial.MAP.parseItem()).setName(Bukkit.getPlayer(invite.getRecipientUniqueId()).getName()).onClick(event2 -> {
					invite.response(Invite.InviteState.ACCEPTED);
				}));
			}
		});
	}

	@Override
	public String getSectionPath() {
		return "duel-creator.invites-gui";
	}
}
