package mc.obliviate.blokduels.gui.room;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.game.GameBuilder;
import mc.obliviate.blokduels.invite.Invite;
import mc.obliviate.blokduels.setup.chatentry.ChatEntry;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
		fillRow(new Icon(Material.STAINED_GLASS_PANE).setDamage(15), 0);
		addItem(0, new Icon(Material.ARROW).onClick(e -> {
			new DuelGameCreatorGUI(player, gameBuilder).open();
		}));
		addItem(4, new Icon(Material.BOOK_AND_QUILL).setName(MessageUtils.parseColor("&aInvite a player")).setLore(MessageUtils.parseColor("&7Players: " + gameBuilder.getPlayers().size())).onClick(e -> {
			player.closeInventory();
			new ChatEntry(player.getUniqueId()).onResponse(chatEvent -> {
				gameBuilder.sendInvite(player, Bukkit.getPlayer(chatEvent.getMessage()), response -> {
					gameBuilder.addPlayer(Bukkit.getPlayer(chatEvent.getMessage()));
				});
				open();
			});
		}));
		int i = 9;
		for (Invite invite : gameBuilder.getInvites().values()) {
			addItem(i++, new Icon(Material.EMPTY_MAP).setName(invite.getTarget().getName()).onClick(e -> {
				invite.setResult(true);
			}));
		}
	}

}
