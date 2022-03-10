package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.game.GameCreator;
import mc.obliviate.masterduels.game.TeamBuilder;
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

public class DuelTeamManagerGUI extends GUI {

	private final GameBuilder gameBuilder;
	private final GameCreator gameCreator;

	public DuelTeamManagerGUI(Player player, GameCreator gameCreator) {
		super(player, "duel-team-manage-gui", "Manage Teams", gameCreator.getBuilder().getTeamAmount() + 1);
		this.gameBuilder = gameCreator.getBuilder();
		this.gameCreator = gameCreator;
	}

	@Override
	public void onOpen(final InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);
		addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
			new DuelGameCreatorGUI(player, gameCreator).open();
		}));

		for (int team = 0; team < gameBuilder.getTeamAmount(); team++) {
			final Icon icon = new Icon(Utils.teamIcons.get(team).clone());
			addItem((team + 1) * 9, icon.setName("§f" + (team + 1) + ". team")
					.setLore("§7(" + gameBuilder.getTeamBuilders().get(team + 1).getMembers().size() + "/" + gameBuilder.getTeamAmount() + ")"));
			for (int member = 0; member < gameBuilder.getTeamSize(); member++) {
				final int slot = ((team + 1) * 9 + 1 + member);

				final TeamBuilder teamBuilder = gameBuilder.getTeamBuilders().get(team + 1);
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
							final TeamBuilder requesterTeamBuilder = gameBuilder.getTeamBuilder(requester);
							final TeamBuilder targetTeamBuilder = gameBuilder.getTeamBuilder(target);

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

	private Icon getNullMemberSlotIcon(TeamBuilder teamBuilder) {
		return new Icon(XMaterial.BARRIER.parseItem()).setName("§cEmpty!").setLore("", "§7Put a player's head here", "§7to add him to team.").onClick(e -> {
			if (!isValidPlayerHead(e.getCursor())) {
				return;
			}
			final Player target = Bukkit.getPlayerExact(ChatColor.stripColor(e.getCursor().getItemMeta().getDisplayName()));
			e.setCursor(null);
			if (target == null) {
				return;
			}
			for (final TeamBuilder builder : gameBuilder.getTeamBuilders().values()) {
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
	public void onClick(InventoryClickEvent e) {
		//fixme: renamed skulls can be inserted on gui
		if (e.getSlot() != e.getRawSlot()) e.setCancelled(true);
		switch (e.getAction()) {
			case PICKUP_ALL:
			case PICKUP_ONE:
			case PICKUP_HALF:
			case PICKUP_SOME:
			case SWAP_WITH_CURSOR:
				return;
			default:
				e.setCancelled(true);
		}
	}
}
