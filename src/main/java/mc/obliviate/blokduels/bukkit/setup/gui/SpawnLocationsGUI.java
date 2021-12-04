package mc.obliviate.blokduels.bukkit.setup.gui;

import mc.obliviate.blokduels.bukkit.arena.elements.Positions;
import mc.obliviate.blokduels.bukkit.setup.ArenaSetup;
import mc.obliviate.blokduels.bukkit.setup.PositionSelection;
import mc.obliviate.inventory.Icon;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import mc.obliviate.inventory.GUI;

public class SpawnLocationsGUI extends GUI {

	private final ArenaSetup arenaSetup;

	public SpawnLocationsGUI(Player player, ArenaSetup arenaSetup) {
		super(player, "spawn-locations-gui", "Select Spawn Location", 6);
		this.arenaSetup = arenaSetup;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		int slot = 0;
		for (int i = 1; arenaSetup.getTeamAmount() >= i; i++) {
			for (int pos = 1; arenaSetup.getTeamSize() >= pos; pos++) {
				Positions positions = arenaSetup.getPositions().get("spawn-team-" + i);
				final Location location;

				if (positions == null) {
					Bukkit.broadcastMessage("positions is null: " + i);
					return;
				}

				if (positions.getLocations() == null) {
					location = null;
				} else {
					location = positions.getLocations().get(pos);
				}

				final int playerNo = pos;
				addItem(slot++, new Icon(Material.STAINED_GLASS_PANE)
						.setDamage(i)
						.setAmount(pos)
						.setName(ChatColor.LIGHT_PURPLE + "Team " + i + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Player " + pos)
						.setLore(
								"",
								ChatColor.GRAY + "Set " + i + ". team's " + pos + ". player's spawn",
								ChatColor.GRAY + "position.",
								"",
								ChatColor.GRAY + "Currently: " + ChatColor.RED + PositionSelection.formatLocation(location),
								ChatColor.YELLOW + "Click to set position!")
						.onClick(e -> {
							positions.registerLocation(playerNo, player.getLocation());
							new SpawnLocationsGUI(player, arenaSetup).open();
						})

				);
			}

			if (slot >= 44) {
				player.sendMessage("There are tooooo many spawn position!");
				break;
			}
		}

		fillRow(new Icon(Material.STAINED_GLASS_PANE).setDamage(15), 5);
		addItem(49, new Icon(Material.ARROW).setName(ChatColor.RED + "Back").onClick(e -> {
			new ArenaSetupGUI(player, arenaSetup).open();
		}));

	}
}