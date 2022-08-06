package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.inventory.configurable.util.ItemStackSerializer;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.game.MatchTeamManager;
import mc.obliviate.masterduels.game.Team;
import mc.obliviate.masterduels.game.creator.KitManager;
import mc.obliviate.masterduels.game.creator.MatchCreator;
import mc.obliviate.masterduels.kit.gui.KitSelectionGUI;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;

public class DuelMatchCreatorNonOwnerGUI extends ConfigurableGui {

    private final MatchCreator matchCreator;
    private static Config guiConfig;

    public DuelMatchCreatorNonOwnerGUI(Player player, MatchCreator matchCreator) {
        super(player, "duel-match-creator-non-owner-gui");
        this.matchCreator = matchCreator;
        setTitle(MessageUtils.parseColor(MessageUtils.applyPlaceholders(ConfigurationHandler.getMenus().getString(getSectionPath() + ".title"),
                new PlaceholderUtil()
                        .add("{owner}", Utils.getDisplayName(Bukkit.getPlayer(this.matchCreator.getOwnerPlayer())))
                        .add("{mode}", MessageUtils.convertMode(this.matchCreator.getBuilder().getTeamSize(), this.matchCreator.getBuilder().getTeamAmount())))));
        setSize((this.matchCreator.getBuilder().getTeamAmount() + 1) * 9);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        putDysfunctionalIcons(new PlaceholderUtil()
                        .add("{mode}", MessageUtils.convertMode(this.matchCreator.getBuilder().getTeamSize(), this.matchCreator.getBuilder().getTeamAmount()))
                        .add("{invited-players}", this.matchCreator.getInvites().size() + "")
                        .add("{total-players}", this.matchCreator.getBuilder().getPlayers().size() + "")
                        .add("{round-amount}", this.matchCreator.getBuilder().getTotalRounds() + "")
                        .add("{game-timer}", TimerUtils.formatTimeAsTimer(this.matchCreator.getBuilder().getDuration().toMinutes() * 60))
                        .add("{game-time}", TimerUtils.formatTimeAsTime(this.matchCreator.getBuilder().getDuration().toMinutes() * 60))
                        .add("{team-amount}", this.matchCreator.getBuilder().getTeamAmount() + "")
                        .add("{team-size}", this.matchCreator.getBuilder().getTeamSize() + "")
                , Collections.singletonList("kit-icon"));
        putTeamIcons();

        putIcon("leave", e -> {
            player.performCommand("duel leave");
            player.closeInventory();
        });

        if (this.matchCreator.getBuilder().getData().getKitManager().getKitMode().equals(KitManager.KitMode.VARIOUS)) {
            Member.Builder builder = this.matchCreator.getBuilder().getData().getGameTeamManager().getMemberBuilder(player.getUniqueId());
            putIcon("kit-icon", new PlaceholderUtil().add("{kit}", builder.getKit(this.matchCreator.getBuilder().getData().getKitManager().getKitMode()) == null ? MessageUtils.parseColor(MessageUtils.getMessage("kit.none-kit-name")) : builder.getKit(this.matchCreator.getBuilder().getData().getKitManager().getKitMode()).getKitName()), e -> {
                new KitSelectionGUI(player, this.matchCreator.getBuilder(), kit -> {
                    this.matchCreator.getBuilder().getData().getGameTeamManager().getMemberBuilder(player.getUniqueId()).setKit(kit);
                    open();
                }).open();
            });
        }
    }

    private void putTeamIcons() {
        final MatchBuilder matchBuilder = this.matchCreator.getBuilder();
        final MatchTeamManager matchTeamManager = matchBuilder.getData().getGameTeamManager();

        for (int teamNo = 0; teamNo < matchBuilder.getTeamAmount(); teamNo++) {

            final Icon icon = new Icon(DuelMatchCreatorNonOwnerGUI.guiConfig.teamIcons.get(Math.min(teamNo, DuelMatchCreatorNonOwnerGUI.guiConfig.teamIcons.size() - 1)).clone()).setName(DuelMatchCreatorNonOwnerGUI.guiConfig.teamIconName).setLore(DuelMatchCreatorNonOwnerGUI.guiConfig.teamIconLore);
            final ItemStack item = icon.getItem();
            ItemStackSerializer.applyPlaceholdersToItemStack(item, new PlaceholderUtil().add("{team-no}", (teamNo + 1) + "").add("{team-players-amount}", matchTeamManager.getTeamBuilders().get(teamNo).getMemberBuilders().size() + "").add("{team-size}", matchBuilder.getTeamAmount() + ""));

            addItem((teamNo + 1) * 9, item);

            for (int member = 0; member < matchBuilder.getTeamSize(); member++) {
                final int slot = ((teamNo + 1) * 9 + 1 + member);

                final Team.Builder team = matchTeamManager.getTeamBuilders().get(teamNo);
                if (team.getMemberBuilders().size() <= member) {
                    addItem(slot, getNullMemberSlotIcon());
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

                final ItemStack playerHead = getPlayerSlotIcon(builder);

                final Icon playerHeadIcon = new Icon(playerHead);
                addItem(slot, playerHeadIcon);
            }
        }
    }

    private Icon getNullMemberSlotIcon() {
        return new Icon(DuelMatchCreatorNonOwnerGUI.guiConfig.getEmptyIcon());

    }

    @Override
    public String getSectionPath() {
        return "duel-creator.non-owner-gui";
    }

    /**
     * Purpose of this class is storing slot formats
     * gui configuration.
     */
    private ItemStack getPlayerSlotIcon(Member.Builder builder) {
        final ItemStack item = new Icon(guiConfig.playerSlotIcon.clone()).setName(guiConfig.teamIconName).setLore(guiConfig.teamIconLore).getItem();
        ItemStackSerializer.applyPlaceholdersToItemStack(item, new PlaceholderUtil().add("{player}", Utils.getDisplayName(builder.getPlayer())).add("{kit}", builder.getKit(this.matchCreator.getBuilder().getData().getKitManager().getKitMode()) == null ? MessageUtils.parseColor(MessageUtils.getMessage("kit.none-kit-name")) : builder.getKit(this.matchCreator.getBuilder().getData().getKitManager().getKitMode()).getKitName()));
        if (item.getItemMeta() instanceof SkullMeta) {
            final SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            skullMeta.setOwner(builder.getPlayer().getName());
            item.setItemMeta(skullMeta);
        }
        return item;
    }

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
            DuelMatchCreatorNonOwnerGUI.guiConfig = this;
        }

        private ItemStack getEmptyIcon() {
            return emptySlotIcon.clone();
        }
    }
}
