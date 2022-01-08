package mc.obliviate.masterduels.gui.room;

import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.game.TeamBuilder;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class DuelTeamManagerGUI extends GUI {

	final GameBuilder gameBuilder;

	public DuelTeamManagerGUI(Player player, GameBuilder gameBuilder) {
		super(player, "duel-team-manage-gui", "Manage Teams", gameBuilder.getTeamAmount() + 1);
		this.gameBuilder = gameBuilder;
	}

	@Override
	public void onOpen(final InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);
		addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
			new DuelGameCreatorGUI(player, gameBuilder).open();
		}));

		for (int team = 0; team < gameBuilder.getTeamAmount(); team++) {
			addItem((team + 1) * 9, Utils.teamIcons.get(team));
			for (int member = 0; member < gameBuilder.getTeamSize(); member++) {
				final int slot = ((team + 1) * 9 + 1 + member);

				final TeamBuilder teamBuilder = gameBuilder.getTeamBuilders().get(team);
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
						case PLACE_ALL:
						case PICKUP_ALL:
						case HOTBAR_MOVE_AND_READD:
							e.setCancelled(false);
					}
				}));

			}
		}
	}

	private Icon getNullMemberSlotIcon(TeamBuilder teamBuilder) {
		return new Icon(XMaterial.BARRIER.parseItem()).setName("§cEmpty!").setLore("","§7Put a player's head here","§7to add him to team.").onClick(e -> {
			if (e.getCursor() == null) return;
			if (e.getCursor().getAmount() != 1) return;
			if (!e.getCursor().getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) return;

			final Player target = Bukkit.getPlayerExact(ChatColor.stripColor(e.getCursor().getItemMeta().getDisplayName()));
			e.setCursor(null);
			if (target == null) {
				return;
			}
			for (final TeamBuilder builder : gameBuilder.getTeamBuilders()) {
				builder.remove(target);
			}
			teamBuilder.add(target);
			open();

		});

	}

	@Override
	public void onClick(InventoryClickEvent e) {
		//bugfix: renamed skulls can be inserted on gui
		if (e.getSlot() != e.getRawSlot()) e.setCancelled(true);
	}
}