package mc.obliviate.masterduels.setup.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.arena.elements.ArenaCuboid;
import mc.obliviate.masterduels.setup.ArenaSetup;
import mc.obliviate.masterduels.setup.PositionSelection;
import mc.obliviate.masterduels.setup.chatentry.ChatEntry;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ArenaSetupGUI extends Gui {

	private final ArenaSetup arenaSetup;

	public ArenaSetupGUI(final Player player, final ArenaSetup arenaSetup) {
		super(player, "arena-setup-gui", "Arena Setup: " + arenaSetup.getArenaName(), 6);

		this.arenaSetup = arenaSetup;
	}

	private static void update(final ArenaSetupGUI gui) {
		gui.open();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 5);
		addItem(new Icon(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()), 52, 45, 46);
		arenaNameHytem();
		mapNameHytem();
		cuboidHytem();
		teamSizeHytem();
		teamAmountHytem();
		setSpawnPositionHytem();
		compileHytem();
		destroyHytem();
		setSpectatorPositionHytem();
		setInformationHytem();

	}

	public void arenaNameHytem() {
		final String name = arenaSetup.getArenaName();

		addItem(16, new Icon(XMaterial.NAME_TAG.parseItem())
				.setName(ChatColor.GOLD + "Arena Name")
				.setLore(ChatColor.GRAY + "Currently: " + ChatColor.RED + name, "", ChatColor.YELLOW + "Click to rename arena!")

				.onClick(e -> {
					player.closeInventory();
					player.sendMessage("§cWrite any text to set §lARENA NAME §cto chat.");
					new ChatEntry(player.getUniqueId(), getPlugin()).onResponse(chatEvent -> {
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

		addItem(15, new Icon(XMaterial.MAP.parseItem())
				.setName(ChatColor.GOLD + "Map Name")
				.setLore(ChatColor.GRAY + "Currently: " + ChatColor.RED + name, "", ChatColor.YELLOW + "Click to rename arena map!")

				.onClick(e -> {
					player.closeInventory();
					player.sendMessage("§cWrite any text to set §lMAP NAME §cto chat.");
					new ChatEntry(player.getUniqueId(), getPlugin()).onResponse(chatEvent -> {
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
		addItem(12, new Icon(XMaterial.BEACON.parseItem())
				.setName(ChatColor.GOLD + "Arena Cuboid")
				.setLore(ChatColor.GRAY + "Select corners of arena limits",
						ChatColor.GRAY + "to create the arena's cuboid.",
						"",
						ChatColor.YELLOW + "Positions of Current Cuboid:",
						ChatColor.GRAY + " 1st Position: " + ChatColor.RED + pos1State,
						ChatColor.GRAY + " 2nd Position: " + ChatColor.RED + pos2State,
						"",
						ChatColor.YELLOW + "Left click to create Arena Cuboid!",
						ChatColor.YELLOW + "Right click to expand vert positions."
				)


				.onClick(e -> {
					if (e.isRightClick()) {
						arenaSetup.getPosSelection().getPos1().setY(0);
						arenaSetup.getPosSelection().getPos2().setY(255);

					}
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
		addItem(10, new Icon(XMaterial.ROSE_BUSH.parseItem())
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
		addItem(11, new Icon(XMaterial.POPPY.parseItem())
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

	public void setSpectatorPositionHytem() {

		final int size = arenaSetup.getPositionsAmount();

		addItem(13, new Icon(XMaterial.ENDER_EYE.parseItem())
				.setName(ChatColor.GOLD + "Set Spectator Spawn")
				.setLore(ChatColor.GRAY + "Set spawn positions of spectator.",
						"",
						ChatColor.GRAY + "Currently: " + PositionSelection.formatLocation(arenaSetup.getSpectatorLocation()),
						"",
						ChatColor.YELLOW + "Click to select position."
				)
				.onClick(e -> {
					arenaSetup.setSpectatorLocation(player.getLocation());
					update(this);
				})
		);
	}


	public void setSpawnPositionHytem() {

		final int size = arenaSetup.getPositionsAmount();

		addItem(14, new Icon(XMaterial.GOLDEN_SHOVEL.parseItem())
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

	public void setInformationHytem() {

		final int size = arenaSetup.getPositionsAmount();

		addItem(49, new Icon(XMaterial.BOOKSHELF.parseItem())
				.setName(ChatColor.GOLD + "State of arena setup")
				.setLore(ChatColor.GRAY + "Mode: §b" + MessageUtils.convertMode(arenaSetup.getTeamSize(), arenaSetup.getTeamAmount()),
						ChatColor.GRAY + "Positions: §b" + size + "/" + (arenaSetup.getTeamSize() * arenaSetup.getTeamAmount()),
						ChatColor.GRAY + "Map: §b" + arenaSetup.getMapName(),
						ChatColor.GRAY + "Arena Name: §b" + arenaSetup.getArenaName()
				)
				.onClick(e -> {
					new SpawnLocationsGUI(player, arenaSetup).open();
				})
		);
	}

	public void compileHytem() {
		if (arenaSetup.canCompile()) {
			addItem(31, new Icon(XMaterial.EMERALD_BLOCK.parseItem()).setName("§aInstall Arena!").setLore("", "§7§oHere we go!").onClick(e -> {
				if (arenaSetup.compile() != null) {
					player.closeInventory();
					player.sendMessage("§aArena successfully installed!");
				} else {
					player.sendMessage("§cArena install has failed!");
				}
			}));
		} else {
			addItem(31, new Icon(XMaterial.REDSTONE_BLOCK.parseItem()).setName("§cSetup is not ready").setLore("", "§7§oPlease be sure you've ", "§7§ocompleted all requirements."));
		}
	}

	public void destroyHytem() {
		addItem(53, new Icon(XMaterial.BARRIER.parseItem()).setName("§cLeave arena setup mode").onClick(e -> {
			arenaSetup.destroy();
			player.closeInventory();
			player.sendMessage("§cArena installing has cancelled!");
		}));

	}

}
