package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.invite.Invite;
import mc.obliviate.masterduels.setup.chatentry.ChatEntry;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelInvitesGUI extends GUI {

	private final GameBuilder gameBuilder;

	public DuelInvitesGUI(Player player, GameBuilder gameBuilder) {
		super(player, "duel-invites-gui", "Duel Invites", 6);
		this.gameBuilder = gameBuilder;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);
		addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
			new DuelGameCreatorGUI(player, gameBuilder).open();
		}));
		addItem(4, new Icon(XMaterial.WRITABLE_BOOK.parseItem()).setName(MessageUtils.parseColor("&aInvite a player")).setLore(MessageUtils.parseColor("&7Players: " + gameBuilder.getPlayers().size())).onClick(e -> {
			player.closeInventory();
			new ChatEntry(player.getUniqueId(), getPlugin()).onResponse(chatEvent -> {
				gameBuilder.sendInvite(player, Bukkit.getPlayer(chatEvent.getMessage()), response -> {
					gameBuilder.addPlayer(Bukkit.getPlayer(chatEvent.getMessage()));
				});
				open();
			});
		}));
		int i = 9;
		for (Invite invite : gameBuilder.getInvites().values()) {
			addItem(i++, new Icon(XMaterial.MAP.parseItem()).setName(invite.getTarget().getName()).onClick(e -> {
				invite.setResult(true);
			}));
		}
	}

}
