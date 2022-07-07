package mc.obliviate.masterduels.gui.creator;

import com.google.common.base.Preconditions;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.game.MatchTeamManager;
import mc.obliviate.masterduels.game.Team;
import mc.obliviate.masterduels.game.creator.MatchCreator;
import mc.obliviate.masterduels.gui.ConfigurableGui;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
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

	private static Config guiConfig;
	private final MatchCreator matchCreator;
	private final boolean isOwner;

	public DuelTeamManagerGUI(Player player, MatchCreator matchCreator) {
		super(player, "duel-team-manage-gui");
		this.matchCreator = matchCreator;
		this.isOwner = matchCreator.getOwnerPlayer().equals(player.getUniqueId());
		setSize((matchCreator.getBuilder().getTeamAmount() + 1) * 9);
	}

	@Override
	public void onOpen(final InventoryOpenEvent event) {
		putDysfunctionalIcons();

		putIcon("back", e -> {
			new DuelMatchCreatorGUI(player, matchCreator).open();
		});

		final MatchBuilder matchBuilder = matchCreator.getBuilder();
		final MatchTeamManager matchTeamManager = matchBuilder.getData().getGameTeamManager();

		for (int teamNo = 0; teamNo < matchBuilder.getTeamAmount(); teamNo++) {

			final Icon icon = new Icon(guiConfig.teamIcons.get(Math.min(teamNo, guiConfig.teamIcons.size() - 1)).clone()).setName(guiConfig.teamIconName).setLore(guiConfig.teamIconLore);
			final ItemStack item = SerializerUtils.applyPlaceholdersOnItemStack(icon.getItem(), new PlaceholderUtil().add("{team-no}", (teamNo + 1) + "").add("{team-players-amount}", matchTeamManager.getTeamBuilders().get(teamNo).getMemberBuilders().size() + "").add("{team-size}", matchBuilder.getTeamAmount() + ""));

			addItem((teamNo + 1) * 9, item);

			for (int member = 0; member < matchBuilder.getTeamSize(); member++) {
				final int slot = ((teamNo + 1) * 9 + 1 + member);

				final Team.Builder team = matchTeamManager.getTeamBuilders().get(teamNo);
				if (team.getMemberBuilders().size() <= member) {
					addItem(slot, getNullMemberSlotIcon(team));
					continue;
				}

				final Member.Builder builder = team.getMemberBuilders().get(member);
				if (builder == null) {
					break;
				}

				if (slot >= getSize()) {
					builder.getPlayer().sendMessage("§cThere are toooo many member!");
					return;
				}

				final ItemStack playerHead = guiConfig.getPlayerSlotIcon(builder);

				final Icon playerHeadIcon = new Icon(playerHead);
				if (isOwner) {
					playerHeadIcon.onClick(e -> {
						switch (e.getAction()) {
							case DROP_ALL_SLOT:
							case DROP_ONE_SLOT:
								final Player player1 = getOwner(e.getCurrentItem());
								if (matchCreator.getOwnerPlayer().equals(player1.getUniqueId()))
									return; //owner cannot leave
								matchBuilder.removePlayer(player1);
								open();
								break;
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

								//and their users
								final IUser requesterMember = UserHandler.getUser(requester.getUniqueId());
								final IUser targetMember = UserHandler.getUser(target.getUniqueId());
								Preconditions.checkNotNull(requesterMember);
								Preconditions.checkNotNull(targetMember);

								//and their teams
								final int targetTeam = matchCreator.getBuilder().getData().getGameTeamManager().getTeamBuilder(target).getTeamId();
								final int requesterTeam = matchCreator.getBuilder().getData().getGameTeamManager().getTeamBuilder(requester).getTeamId();

								//swap!
								MatchTeamManager teamManager = matchCreator.getBuilder().getData().getGameTeamManager();

								teamManager.unregisterPlayer(requesterMember);
								teamManager.unregisterPlayer(targetMember);

								teamManager.registerPlayer(requesterMember.getPlayer(), null, targetTeam);
								teamManager.registerPlayer(targetMember.getPlayer(), null, requesterTeam);


								e.setCursor(null);
								open();
								break;
						}
					});
				}
				addItem(slot, playerHeadIcon);
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

	private Icon getNullMemberSlotIcon(Team.Builder team) {
		return new Icon(guiConfig.getEmptyIcon()).onClick(e -> {
			if (!isValidPlayerHead(e.getCursor())) {
				return;
			}

			final Player target = getOwner(e.getCursor());
			e.setCursor(null);
			if (target == null) {
				return;
			}

			matchCreator.getBuilder().removePlayer(target);
			matchCreator.getBuilder().addPlayer(target, null, team.getTeamId());
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
	public static class Config {

		private final ItemStack emptySlotIcon;
		private final ItemStack playerSlotIcon;

		private final String teamIconName;
		private final List<String> teamIconLore;

		private final List<ItemStack> teamIcons;

		public Config(ItemStack emptySlotIcon, ItemStack playerSlotIcon, String teamIconName, List<String> teamIconLore, List<ItemStack> teamIcons) {
			this.emptySlotIcon = emptySlotIcon;
			this.playerSlotIcon = playerSlotIcon;
			this.teamIconName = teamIconName;
			this.teamIconLore = teamIconLore;
			this.teamIcons = teamIcons;
			DuelTeamManagerGUI.guiConfig = this;
		}

		private ItemStack getEmptyIcon() {
			return emptySlotIcon.clone();
		}

		private ItemStack getPlayerSlotIcon(Member.Builder builder) {
			final ItemStack item = SerializerUtils.applyPlaceholdersOnItemStack(playerSlotIcon.clone(), new PlaceholderUtil().add("{player}", Utils.getDisplayName(builder.getPlayer())).add("{kit}", builder.getKit() == null ? "" : builder.getKit().getKitName()));
			if (item.getItemMeta() instanceof SkullMeta) {
				final SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
				skullMeta.setOwner(builder.getPlayer().getName());
				item.setItemMeta(skullMeta);
			}
			return item;
		}
	}

}
