package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.api.arena.ITeamBuilder;
import mc.obliviate.masterduels.game.MatchCreator;
import mc.obliviate.masterduels.gui.ConfigurableGui;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class DuelTeamManagerGUI extends ConfigurableGui {

	private static DuelTeamManagerGUIConfig guiConfig;
	private final MatchCreator matchCreator;

	public DuelTeamManagerGUI(Player player, MatchCreator matchCreator) {
		super(player, "duel-team-manage-gui");
		this.matchCreator = matchCreator;
		setSize((matchCreator.getBuilder().getTeamAmount() + 1) * 9);
	}

	public static void setGuiConfig(DuelTeamManagerGUIConfig guiConfig) {
		DuelTeamManagerGUI.guiConfig = guiConfig;
	}

	@Override
	public void onOpen(final InventoryOpenEvent event) {

		putDysfunctionalIcons();
		putIcon("back", e -> {
			new DuelMatchCreatorGUI(player, matchCreator).open();
		});

		for (int team = 0; team < matchCreator.getBuilder().getTeamAmount(); team++) {
			final Icon icon = new Icon(Utils.teamIcons.get(team).clone()).setName(guiConfig.teamIconName).setLore(guiConfig.teamIconLore);
			final ItemStack item = SerializerUtils.applyPlaceholdersOnItemStack(icon.getItem(),
					new PlaceholderUtil()
							.add("{team-no}", (team + 1) + "")
							.add("{team-players-amount}", matchCreator.getBuilder().getTeamBuilders().get(team + 1).getMembers().size() + "")
							.add("{team-size}", matchCreator.getBuilder().getTeamAmount() + ""));

			addItem((team + 1) * 9, item);

			for (int member = 0; member < matchCreator.getBuilder().getTeamSize(); member++) {
				final int slot = ((team + 1) * 9 + 1 + member);

				final ITeamBuilder teamBuilder = matchCreator.getBuilder().getTeamBuilders().get(team + 1);
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

				final ItemStack playerHead = guiConfig.getPlayerSlotIcon(player);

				addItem(slot, new Icon(playerHead).onClick(e -> {
					switch (e.getAction()) {
						case PICKUP_HALF:
						case PICKUP_ONE:
						case PICKUP_ALL:
						case PICKUP_SOME:
							Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
								addItem(slot, new Icon(guiConfig.getEmptyIcon()).onClick(ev -> {
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
							final Player requester = getOwner(cursor);
							final Player target = getOwner(e.getCurrentItem());

							//and their teams
							final ITeamBuilder requesterTeamBuilder = matchCreator.getBuilder().getTeamBuilder(requester);
							final ITeamBuilder targetTeamBuilder = matchCreator.getBuilder().getTeamBuilder(target);

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

	private Player getOwner(ItemStack skullItemStack) {
		final SkullMeta skullMeta = (SkullMeta) skullItemStack.getItemMeta();
		return Bukkit.getPlayerExact(skullMeta.getOwner());
	}

	private boolean isValidPlayerHead(ItemStack item) {
		if (item == null) return false;
		if (item.getAmount() != 1) return false;
		if (!item.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) return false;
		return true;
	}

	private Icon getNullMemberSlotIcon(ITeamBuilder teamBuilder) {
		return new Icon(guiConfig.getEmptyIcon()).onClick(e -> {
			if (!isValidPlayerHead(e.getCursor())) {
				return;
			}

			final Player target = getOwner(e.getCursor());
			e.setCursor(null);
			if (target == null) {
				return;
			}
			for (final ITeamBuilder builder : matchCreator.getBuilder().getTeamBuilders().values()) {
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
		if (e.getSlot() != e.getRawSlot()) {
			e.setCancelled(true);
			return false;
		}
		return false;
	}

	@Override
	public String getSectionPath() {
		return "duel-creator.manage-teams-gui";
	}

	/**
	 * Purpose of this class is storing slot formats
	 * gui configuration.
	 */
	public static class DuelTeamManagerGUIConfig {

		private final ItemStack emptySlotIcon;
		private final ItemStack playerSlotIcon;

		private final String teamIconName;
		private final List<String> teamIconLore;

		public DuelTeamManagerGUIConfig(ItemStack emptySlotIcon, ItemStack playerSlotIcon, String teamIconName, List<String> teamIconLore) {
			this.emptySlotIcon = emptySlotIcon;
			this.playerSlotIcon = playerSlotIcon;
			this.teamIconName = teamIconName;
			this.teamIconLore = teamIconLore;
		}

		protected ItemStack getEmptyIcon() {
			return emptySlotIcon.clone();
		}

		protected ItemStack getPlayerSlotIcon(Player player) {
			final ItemStack item = SerializerUtils.applyPlaceholdersOnItemStack(playerSlotIcon.clone(), new PlaceholderUtil().add("{player}", Utils.getDisplayName(player)));
			if (item.getItemMeta() instanceof SkullMeta) {
				final SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
				skullMeta.setOwner(player.getName());
				item.setItemMeta(skullMeta);
			}
			return item;
		}
	}

}
