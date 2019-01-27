package net.hotsmc.sg.hotbar;

import lombok.Getter;
import net.hotsmc.core.HotsCore;
import net.hotsmc.core.gui.ClickActionItem;
import net.hotsmc.core.hotbar.HotbarAdapter;
import net.hotsmc.core.other.Style;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.player.GamePlayer;
import net.hotsmc.sg.menu.HostManagerMenu;
import net.hotsmc.sg.menu.SettingsMenu;
import net.hotsmc.sg.menu.SpectateManagerMenu;
import net.hotsmc.sg.menu.StatisticsMenu;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.ItemUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public enum PlayerHotbar {

    LOBBY(() -> {
        ClickActionItem[] items = new ClickActionItem[9];
        Arrays.fill(items, null);
        items[3] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Vote Map", Material.BOOK, false)) {
            @Override
            public void clickAction(Player player) {
                if (!HSG.getGameTask().getVoteManager().isVoting()) {
                    ChatUtility.sendMessage(player, Style.RED + "Not voting");
                } else {
                    HSG.getInstance().getVoteMenu().openMenu(player, 9);
                }
            }
        };
        items[5] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Statistics", Material.EMERALD, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new StatisticsMenu().openMenu(player, 45);
            }
        };
        items[6] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Settings", Material.WATCH, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new SettingsMenu().openMenu(player, 9);
            }
        };

        items[8] = new ClickActionItem(net.hotsmc.core.utility.ItemUtility.createDye(Style.AQUA + "Back to Hub", 1, 14)) {
            @Override
            public void clickAction(Player player) {
                HotsCore.getInstance().getBungeeChannelApi().connect(player, "Hub");
            }
        };
        return items;
    }),

    CUSTOMSG_LOBBY(() -> {
        ClickActionItem[] items = new ClickActionItem[9];
        Arrays.fill(items, null);
        items[5] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Statistics", Material.EMERALD, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new StatisticsMenu().openMenu(player, 45);
            }
        };
        items[6] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Settings", Material.WATCH, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new SettingsMenu().openMenu(player, 9);
            }
        };

        items[8] = new ClickActionItem(net.hotsmc.core.utility.ItemUtility.createDye(Style.AQUA + "Back to Hub", 1, 14)) {
            @Override
            public void clickAction(Player player) {
                HotsCore.getInstance().getBungeeChannelApi().connect(player, "Hub");
            }
        };
        return items;
    }),

    CUSTOMSG_HOST_LOBBY(() -> {
        ClickActionItem[] items = new ClickActionItem[9];
        Arrays.fill(items, null);
        items[3] = new ClickActionItem(ItemUtility.createItemStack(Style.BLUE + Style.BOLD + "Host Manager", Material.NAME_TAG, false)) {
            @Override
            public void clickAction(Player player) {
                if(!player.getUniqueId().equals(HSG.getGameTask().getHost())){
                    ChatUtility.sendMessage(player, ChatColor.RED + "You are not host.");
                    return;
                }
                new HostManagerMenu().openMenu(player);
            }
        };
        items[5] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Statistics", Material.EMERALD, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new StatisticsMenu().openMenu(player, 45);
            }
        };
        items[6] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Settings", Material.WATCH, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new SettingsMenu().openMenu(player, 9);
            }
        };

        items[8] = new ClickActionItem(net.hotsmc.core.utility.ItemUtility.createDye(Style.AQUA + "Back to Hub", 1, 14)) {
            @Override
            public void clickAction(Player player) {
                HotsCore.getInstance().getBungeeChannelApi().connect(player, "Hub");
            }
        };
        return items;
    }),

    SPECTATE(() -> {
        ClickActionItem[] items = new ClickActionItem[9];
        Arrays.fill(items, null);
        items[0] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Settings", Material.WATCH, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new SettingsMenu().openMenu(player, 9);
            }
        };
        items[1] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Statistics", Material.EMERALD, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new StatisticsMenu().openMenu(player, 45);
            }
        };
        items[4] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Spectate Manager", Material.NAME_TAG, false)) {
            @Override
            public void clickAction(Player player) {
                new SpectateManagerMenu().openMenu(player, 27);
            }
        };
        items[8] = new ClickActionItem(net.hotsmc.core.utility.ItemUtility.createDye(Style.AQUA + "Back to Hub", 1, 14)) {
            @Override
            public void clickAction(Player player) {
                HotsCore.getInstance().getBungeeChannelApi().connect(player, "Hub");
            }
        };
        return items;
    });

    @Getter
    private HotbarAdapter adapter;

    PlayerHotbar(HotbarAdapter adapter) {
        this.adapter = adapter;
    }

}
