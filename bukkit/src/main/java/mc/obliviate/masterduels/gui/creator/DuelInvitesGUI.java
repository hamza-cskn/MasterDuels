package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.game.MatchCreator;
import mc.obliviate.masterduels.gui.ConfigurableGui;
import mc.obliviate.masterduels.queue.DuelQueue;
import mc.obliviate.masterduels.setup.chatentry.ChatEntry;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
				final Player receiver = Bukkit.getPlayer(chatEvent.getMessage());
				matchCreator.trySendInvite(player, receiver, response -> {
					matchCreator.getBuilder().addPlayer(receiver);
				});
				open();
			});
			MessageUtils.sendMessage(player, "enter-player-name-to-invite");
		});

		int i = 9;
		for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
			if (matchCreator.getBuilder().getPlayers().contains(loopPlayer.getUniqueId())) break;
			if (matchCreator.getInvites().containsKey(loopPlayer.getUniqueId())) break;
			final IUser user = UserHandler.getUser(loopPlayer.getUniqueId());
			if (user instanceof Member) break;
			if (user.inviteReceiving()) break;
			if (DuelQueue.findQueueOfPlayer(loopPlayer) != null) break;

			if (i >= getSize()) return;
			addItem(i++, new Icon(XMaterial.PLAYER_HEAD.parseItem()).setName(ChatColor.GRAY + Utils.getDisplayName(loopPlayer)).setLore("", ChatColor.YELLOW + "Click to invite").onClick(ev -> {
				matchCreator.trySendInvite(this.player, loopPlayer, response -> {
					matchCreator.getBuilder().addPlayer(loopPlayer);
				});
			}));
		}
	}

	@Override
	public String getSectionPath() {
		return "duel-creator.invites-gui";
	}
}
