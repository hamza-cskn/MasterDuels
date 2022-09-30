package mc.obliviate.masterduels.gui.spectator;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.inventory.configurable.util.ItemStackSerializer;
import mc.obliviate.inventory.pagination.PaginationManager;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class SpectatorTeleportationGUI extends ConfigurableGui {

    private final Match match;
    private static Config guiConfig;
    private final PaginationManager paginationManager = new PaginationManager(this);


    public SpectatorTeleportationGUI(Player player, Match match) {
        super(player, "spectator-teleportation-gui");
        this.match = match;
        this.paginationManager.getSlots().addAll(guiConfig.pageSlots);

        ItemStack sampleItem = ItemStackSerializer.deserializeItemStack(getIconsSection("player-icon"));
        for (Member member : match.getAllMembers()) {
            ItemStack item = sampleItem.clone();
            ItemStackSerializer.applyPlaceholdersToItemStack(item, new PlaceholderUtil()
                    .add("{player}", member.getPlayer().getName())
                    .add("{team-no}", this.match.getGameDataStorage().getGameTeamManager().getTeam(member.getPlayer()).getTeamId() + ""));
            this.paginationManager.addItem(new Icon(item).onClick(e -> {
                this.player.closeInventory();
                this.player.teleport(member.getPlayer());
            }));
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        putDysfunctionalIcons(Arrays.asList("next", "previous", "player-icon"));

        if (this.paginationManager.getCurrentPage() != this.paginationManager.getLastPage()) {
            putIcon("previous", e -> this.paginationManager.goPreviousPage().update());
        }
        if (this.paginationManager.getCurrentPage() != 0) {
            putIcon("next", e -> this.paginationManager.goNextPage().update());
        }
        this.paginationManager.update();
    }

    public static class Config {

        private final List<Integer> pageSlots;

        public Config(List<Integer> pageSlots) {
            this.pageSlots = pageSlots;
            SpectatorTeleportationGUI.guiConfig = this;
        }

    }
}
