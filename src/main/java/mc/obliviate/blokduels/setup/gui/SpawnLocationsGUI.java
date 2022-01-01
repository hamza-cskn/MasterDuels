package mc.obliviate.blokduels.setup.gui;

import mc.obliviate.blokduels.arena.elements.Positions;
import mc.obliviate.blokduels.setup.ArenaSetup;
import mc.obliviate.blokduels.setup.PositionSelection;
import mc.obliviate.blokduels.utils.Logger;
import mc.obliviate.blokduels.utils.xmaterial.XMaterial;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class SpawnLocationsGUI extends GUI {

	private final ArenaSetup arenaSetup;
	private static final List<ItemStack> icons = Arrays.asList(
			XMaterial.RED_STAINED_GLASS.parseItem(),
			XMaterial.BLUE_STAINED_GLASS.parseItem(),
			XMaterial.YELLOW_STAINED_GLASS.parseItem(),
			XMaterial.LIME_STAINED_GLASS.parseItem(),
			XMaterial.PURPLE_STAINED_GLASS.parseItem(),
			XMaterial.CYAN_STAINED_GLASS.parseItem(),
			XMaterial.ORANGE_STAINED_GLASS.parseItem(),
			XMaterial.PINK_STAINED_GLASS.parseItem(),
			XMaterial.LIGHT_BLUE_STAINED_GLASS.parseItem(),
			XMaterial.GREEN_STAINED_GLASS.parseItem());

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
					Logger.error("positions is null: " + i);
					return;
				}

				if (positions.getLocations() == null) {
					location = null;
				} else {
					location = positions.getLocations().get(pos);
				}

				final int playerNo = pos;
				addItem(slot++, new Icon(icons.get(i-1))
						.setAmount(pos)
						.setName(ChatColor.LIGHT_PURPLE + "Team " + i + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "Player " + pos)
						.setLore(
								"",
								ChatColor.GRAY + "Set " + i + ". team's " + pos + ". player's spawn",
								ChatColor.GRAY + "position.",
								"",
								ChatColor.GRAY + "Currently: " + ChatColor.GREEN + PositionSelection.formatLocation(location),
								ChatColor.YELLOW + "Click to set position!")
						.onClick(e -> {
							positions.registerLocation(playerNo, player.getLocation());
							new SpawnLocationsGUI(player, arenaSetup).open();
						})

				);
			}

			if (slot >= 44 || i > 9) {
				player.sendMessage("§cThere are toooo many spawn position!");
				break;
			}
		}

		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()).setDamage(15), 5);
		addItem(49, new Icon(XMaterial.ARROW.parseItem()).setName(ChatColor.RED + "Back").onClick(e -> {
			new ArenaSetupGUI(player, arenaSetup).open();
		}));

	}
}
