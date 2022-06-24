package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.api.arena.ITeamBuilder;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.game.MatchCreator;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class DuelTeamManagerGUI extends Gui {

	private final MatchBuilder matchBuilder;
	private final MatchCreator matchCreator;

	public DuelTeamManagerGUI(Player player, MatchCreator matchCreator) {
		super(player, "duel-team-manage-gui", "Manage Teams", matchCreator.getBuilder().getTeamAmount() + 1);
		this.matchBuilder = matchCreator.getBuilder();
		this.matchCreator = matchCreator;
	}

	@Override
	public void onOpen(final InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);
		addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
			new DuelMatchCreatorGUI(player, matchCreator).open();
		}));

		for (int team = 0; team < matchBuilder.getTeamAmount(); team++) {
			final Icon icon = new Icon(Utils.teamIcons.get(team).clone());
			addItem((team + 1) * 9, icon.setName("§f" + (team + 1) + ". team")
					.setLore("§7(" + matchBuilder.getTeamBuilders().get(team + 1).getMembers().size() + "/" + matchBuilder.getTeamAmount() + ")"));
			for (int member = 0; member < matchBuilder.getTeamSize(); member++) {
				final int slot = ((team + 1) * 9 + 1 + member);

				final ITeamBuilder teamBuilder = matchBuilder.getTeamBuilders().get(team + 1);
				if (teamBuilder.getMembers().size() <= member) {
					addItem(slot, getNullMemberSlotIcon(teamBuilder));
					continue;
				}

				final Player player = teamBuilder.getMembers().get(member);
				if (player == null) {
					break;
				}

				if (slot >= getSize()) {
					player.sendMessage("§cThere are toooo many member!");
					return;
				}

				final ItemStack playerHead = XMaterial.PLAYER_HEAD.parseItem();
				assert playerHead != null;
				final SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
				if (skullMeta != null) {
					skullMeta.setOwner(player.getName());
					playerHead.setItemMeta(skullMeta);
				}

				addItem(slot, new Icon(playerHead).setName("§e" + player.getName()).onClick(e -> {
					switch (e.getAction()) {
						case PICKUP_HALF:
						case PICKUP_ONE:
						case PICKUP_ALL:
						case PICKUP_SOME:
							Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
								addItem(slot, new Icon(XMaterial.BARRIER.parseItem()).onClick(ev -> {
									ev.setCursor(null);
									open();
								}));
							}, 1);
							e.setCancelled(false);
							break;
						case SWAP_WITH_CURSOR:
							final ItemStack cursor = e.getCursor();
							if (!isValidPlayerHead(cursor)) break;

							//get players that will swap
							final Player requester = Bukkit.getPlayerExact(ChatColor.stripColor(cursor.getItemMeta().getDisplayName()));
							final Player target = Bukkit.getPlayerExact(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));

							//and their teams
							final ITeamBuilder requesterTeamBuilder = matchBuilder.getTeamBuilder(requester);
							final ITeamBuilder targetTeamBuilder = matchBuilder.getTeamBuilder(target);

							//swap!
							targetTeamBuilder.remove(target);
							targetTeamBuilder.add(requester);

							requesterTeamBuilder.remove(requester);
							requesterTeamBuilder.add(target);

							e.setCursor(null);
							open();
							break;
						default:

					}
				}));

			}
		}
	}

	private boolean isValidPlayerHead(ItemStack item) {
		if (item == null) return false;
		if (item.getAmount() != 1) return false;
		if (!item.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) return false;
		return true;

	}

	private Icon getNullMemberSlotIcon(ITeamBuilder teamBuilder) {
		return new Icon(XMaterial.BARRIER.parseItem()).setName("§cEmpty!").setLore("", "§7Put a player's head here", "§7to add him to team.").onClick(e -> {
			if (!isValidPlayerHead(e.getCursor())) {
				return;
			}
			final Player target = Bukkit.getPlayerExact(ChatColor.stripColor(e.getCursor().getItemMeta().getDisplayName()));
			e.setCursor(null);
			if (target == null) {
				return;
			}
			for (final ITeamBuilder builder : matchBuilder.getTeamBuilders().values()) {
				builder.remove(target);
			}
			teamBuilder.add(target);
			open();

		});

	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		event.getPlayer().setItemOnCursor(null);
	}

	@Override
	public boolean onClick(InventoryClickEvent e) {
		if (e.getSlot() != e.getRawSlot()) return true;
		switch (e.getAction()) {
			case PICKUP_ALL:
			case PICKUP_ONE:
			case PICKUP_HALF:
			case PICKUP_SOME:
			case SWAP_WITH_CURSOR:
				return false;
			default:
				return true;
		}
	}
}
