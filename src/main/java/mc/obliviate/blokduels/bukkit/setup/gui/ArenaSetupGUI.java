package mc.obliviate.blokduels.bukkit.setup.gui;

import mc.obliviate.blokduels.bukkit.arena.elements.ArenaCuboid;
import mc.obliviate.blokduels.bukkit.setup.ArenaSetup;
import mc.obliviate.blokduels.bukkit.setup.PositionSelection;
import mc.obliviate.blokduels.bukkit.setup.chatentry.ChatEntry;
import mc.obliviate.inventory.Icon;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import mc.obliviate.inventory.GUI;

public class ArenaSetupGUI extends GUI {

	private final ArenaSetup arenaSetup;

	public ArenaSetupGUI(final Player player, final ArenaSetup arenaSetup) {
		super(player, "arena-setup-gui", "Arena Setup: " + arenaSetup.getArenaName(), 5);

		this.arenaSetup = arenaSetup;
	}

	private static void update(final ArenaSetupGUI gui) {
		new ArenaSetupGUI(gui.player, gui.arenaSetup).open();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		arenaNameHytem();
		mapNameHytem();
		cuboidHytem();
		teamSizeHytem();
		teamAmountHytem();
		setSpawnPositionHytem();
		compileHytem();
		destroyHytem();
	}

	public void arenaNameHytem() {
		final String name = arenaSetup.getArenaName();

		addItem(4, new Icon(Material.NAME_TAG)
				.setName(ChatColor.GOLD + "Arena Name")
				.setLore(ChatColor.GRAY + "Currently: " + ChatColor.RED + name, "", ChatColor.YELLOW + "Click to rename arena!")

				.onClick(e -> {
					player.closeInventory();
					player.sendMessage("§cWrite any text to set §lARENA NAME §cto chat.");
					new ChatEntry(player.getUniqueId()).onResponse(chatEvent -> {
						if (ArenaSetup.isNameUnique(chatEvent.getMessage())) {
							arenaSetup.setArenaName(chatEvent.getMessage());
						} else {
							player.sendMessage("§cAn arena named " + chatEvent.getMessage() + " is already exist! Please use another name.");
						}
						update(this);
					});
				})
		);
	}

	public void mapNameHytem() {
		final String name = arenaSetup.getMapName();

		addItem(10, new Icon(Material.EMPTY_MAP)
				.setName(ChatColor.GOLD + "Map Name")
				.setLore(ChatColor.GRAY + "Currently: " + ChatColor.RED + name, "", ChatColor.YELLOW + "Click to rename arena map!")

				.onClick(e -> {
					player.closeInventory();
					player.sendMessage("§cWrite any text to set §lMAP NAME §cto chat.");
					new ChatEntry(player.getUniqueId()).onResponse(chatEvent -> {
						arenaSetup.setMapName(chatEvent.getMessage());
						update(this);
					});
				})
		);
	}

	public void cuboidHytem() {

		String pos1State = "No Location";
		String pos2State = "No Location";
		if (arenaSetup.getArenaCuboid() != null) {
			pos1State = PositionSelection.formatLocation(arenaSetup.getArenaCuboid().getPoint1());
			pos2State = PositionSelection.formatLocation(arenaSetup.getArenaCuboid().getPoint2());
		}
		addItem(16, new Icon(Material.GLASS)
				.setName(ChatColor.GOLD + "Arena Cuboid")
				.setLore(ChatColor.GRAY + "Select corners of arena limits",
						ChatColor.GRAY + "to create the arena's cuboid.",
						"",
						ChatColor.YELLOW + "Positions of Current Cuboid:",
						ChatColor.GRAY + " 1st Position: " + ChatColor.RED + pos1State,
						ChatColor.GRAY + " 2nd Position: " + ChatColor.RED + pos2State,
						"",
						ChatColor.YELLOW + "Click to create Arena Cuboid!"
				)


				.onClick(e -> {
					if (arenaSetup.getPosSelection().getPos1() == null || arenaSetup.getPosSelection().getPos2() == null) {
						player.sendMessage(ChatColor.RED + "Please select 2 different positions to create arena cuboid.");
						return;
					}

					final ArenaCuboid cuboid = new ArenaCuboid(arenaSetup.getPosSelection().getPos1(), arenaSetup.getPosSelection().getPos2());
					arenaSetup.setArenaCuboid(cuboid);

					update(this);
				})
		);
	}

	public void teamSizeHytem() {
		addItem(34, new Icon(Material.RED_MUSHROOM)
				.setName(ChatColor.GOLD + "Team Size")
				.setLore(ChatColor.GRAY + "Set players amount of a team. For",
						ChatColor.GRAY + "example enter '1' for 'SOLO'.",
						"",
						ChatColor.GRAY + "Currently: " + ChatColor.RED + arenaSetup.getTeamSize() + " players",
						"",
						ChatColor.YELLOW + "[+] Left-Click to increase!",
						ChatColor.YELLOW + "[-] Right-Click to decrease!"
				)


				.onClick(e -> {
					if (e.isLeftClick()) {
						arenaSetup.setTeamSize(arenaSetup.getTeamSize() + 1);
					} else if (e.isRightClick()) {
						arenaSetup.setTeamSize(arenaSetup.getTeamSize() - 1);
						if (arenaSetup.getTeamSize() < 1) {
							arenaSetup.setTeamSize(1);
							return;
						}
					}
					update(this);
				})
		);
	}

	public void teamAmountHytem() {
		addItem(28, new Icon(Material.RED_ROSE)
				.setName(ChatColor.GOLD + "Team Amount")
				.setLore(ChatColor.GRAY + "Set amount of teams. For example",
						ChatColor.GRAY + "enter '2' to 1v1, enter '3' to 1v1v1",
						"",
						ChatColor.GRAY + "Currently: " + ChatColor.RED + arenaSetup.getTeamAmount() + " teams",
						"",
						ChatColor.YELLOW + "[+] Left-Click to increase!",
						ChatColor.YELLOW + "[-] Right-Click to decrease!"
				)
				.onClick(e -> {
					if (e.isLeftClick()) {
						arenaSetup.setTeamAmount(arenaSetup.getTeamAmount() + 1);
					} else if (e.isRightClick()) {
						arenaSetup.setTeamAmount(arenaSetup.getTeamAmount() - 1);
						if (arenaSetup.getTeamAmount() < 2) {
							arenaSetup.setTeamAmount(2);
							return;
						}
					}
					update(this);
				})
		);
	}

	public void setSpawnPositionHytem() {

		final int size = arenaSetup.getPositionsAmount();

		addItem(40, new Icon(Material.GOLD_SPADE)
				.setName(ChatColor.GOLD + "Set Spawn Positions")
				.setLore(ChatColor.GRAY + "Set spawn positions of players.",
						"",
						ChatColor.GRAY + "Currently: " + ChatColor.RED + size + "/" + (arenaSetup.getTeamSize() * arenaSetup.getTeamAmount()),
						"",
						ChatColor.YELLOW + "Click to select position."
				)
				.onClick(e -> {
					new SpawnLocationsGUI(player, arenaSetup).open();
				})
		);
	}

	public void compileHytem() {
		if (arenaSetup.canCompile()) {
			addItem(22, new Icon(Material.EMERALD_BLOCK).setName("§aInstall Arena!").onClick(e -> {
				if (arenaSetup.compile() != null) {
					player.closeInventory();
					player.sendMessage("§aArena successfully installed!");
				} else {
					player.sendMessage("§cArena install has failed!");
				}
			}));
		} else {
			addItem(22, new Icon(Material.EMERALD_ORE).setName("§cArena is not ready to install."));
		}
	}

	public void destroyHytem() {
		addItem(8, new Icon(Material.BARRIER).setName("§aCancel Arena Installing").onClick(e -> {
			arenaSetup.destroy();
			player.closeInventory();
			player.sendMessage("§cArena installing has cancelled!");
		}));

	}

}
