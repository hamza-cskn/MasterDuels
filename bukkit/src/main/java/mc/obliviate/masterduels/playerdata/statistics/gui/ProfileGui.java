package mc.obliviate.masterduels.playerdata.statistics.gui;

import com.google.common.base.Preconditions;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.inventory.configurable.util.ItemStackSerializer;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class ProfileGui extends ConfigurableGui {

    private final DuelStatistic duelStatistic;

    public ProfileGui(Player player, DuelStatistic duelStatistic) {
        super(player, "profile-gui");
        Preconditions.checkNotNull(duelStatistic, "statistics cannot be null");
        this.duelStatistic = duelStatistic;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        PlaceholderUtil placeholderUtil = new PlaceholderUtil()
                .add("{wins}", this.duelStatistic.getWins() + "")
                .add("{losses}", this.duelStatistic.getLosses() + "")
                .add("{fish-hook-accuracy}", MessageUtils.getPercentage(this.duelStatistic.getPlayerData().getFishHook().getThrew(), this.duelStatistic.getPlayerData().getFishHook().getHit()) + "")
                .add("{melee-accuracy}", MessageUtils.getPercentage(this.duelStatistic.getPlayerData().getClick(), this.duelStatistic.getPlayerData().getHitClick()) + "")
                .add("{bow-accuracy}", MessageUtils.getPercentage(this.duelStatistic.getPlayerData().getArrow().getThrew(), this.duelStatistic.getPlayerData().getArrow().getHit()) + "")
                .add("{regenerated-health}", this.duelStatistic.getPlayerData().getRegeneratedHealth() + "")
                .add("{hit-click}", this.duelStatistic.getPlayerData().getHitClick() + "")
                .add("{click}", this.duelStatistic.getPlayerData().getClick() + "")
                .add("{fall}", this.duelStatistic.getPlayerData().getFall() / 100d + "")
                .add("{sprint}", this.duelStatistic.getPlayerData().getSprint() / 100d + "")
                .add("{jump}", this.duelStatistic.getPlayerData().getJump() + "")
                .add("{regenerated-health}", this.duelStatistic.getPlayerData().getRegeneratedHealth() + "")
                .add("{damage-taken}", this.duelStatistic.getPlayerData().getDamageTaken() / 5d + "")
                .add("{damage-dealt}", this.duelStatistic.getPlayerData().getDamageDealt() / 5d + "")
                .add("{broken-blocks}", this.duelStatistic.getPlayerData().getBrokenBlocks() + "")
                .add("{placed-blocks}", this.duelStatistic.getPlayerData().getPlacedBlocks() + "");
        putDysfunctionalIcons(placeholderUtil, Arrays.asList("toggle-invites-icon-disabled", "toggle-invites-icon-enabled"));

        IUser user = UserHandler.getUser(duelStatistic.getPlayerUniqueId());
        Preconditions.checkNotNull(user, "user cannot be null");

        ItemStack item = getConfigItem("stat-icon");
        ItemStackSerializer.applyPlaceholdersToItemStack(item, placeholderUtil);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof SkullMeta) {
            ((SkullMeta) meta).setOwner(player.getName());
            item.setItemMeta(meta);
        }
        addItem(getConfigSlot("stat-icon"), new Icon(item));

        String node = user.inviteReceiving() ? "toggle-invites-icon-enabled" : "toggle-invites-icon-disabled";
        putIcon(node, e -> {
            user.setInviteReceiving(!user.inviteReceiving());
            open();
        });

        node = user.showBossBar() ? "toggle-boss-bar-icon-enabled" : "toggle-boss-bar-icon-disabled";
        putIcon(node, e -> {
            user.setShowBossBar(!user.showBossBar());
            open();
        });

        node = user.showScoreboard() ? "toggle-scoreboard-icon-enabled" : "toggle-scoreboard-icon-disabled";
        putIcon(node, e -> {
            user.setShowScoreboard(!user.showScoreboard());
            open();
        });
    }
}
