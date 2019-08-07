package net.routemc.sg.hotbar;

import lombok.Getter;
import me.signatured.ezqueuespigot.EzQueueAPI;
import me.trollcoding.requires.hotbar.ClickActionItem;
import me.trollcoding.requires.hotbar.HotbarAdapter;
import me.trollcoding.requires.utils.objects.Cooldown;
import me.trollcoding.requires.utils.objects.Style;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.routemc.core.RouteCore;
import net.routemc.core.network.packet.network.PacketClickableBroadcast;
import net.routemc.sg.RouteSG;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.menu.HostManagerMenu;
import net.routemc.sg.menu.SettingsMenu;
import net.routemc.sg.menu.SpectateManagerMenu;
import net.routemc.sg.menu.StatisticsMenu;
import net.routemc.sg.utility.BungeeUtils;
import net.routemc.sg.utility.ChatUtility;
import net.routemc.sg.utility.ItemUtility;
import org.apache.logging.log4j.core.appender.routing.Route;
import org.apache.logging.log4j.core.appender.routing.Routes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PlayerHotbar {

    LOBBY(() -> {
        ClickActionItem[] items = new ClickActionItem[9];
        Arrays.fill(items, null);
        items[3] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Vote Map", Material.BOOK, false)) {
            @Override
            public void clickAction(Player player) {
                if (!RouteSG.getGameTask().getVoteManager().isVoting()) {
                    ChatUtility.sendMessage(player, Style.RED + "Not voting");
                } else {
                    RouteSG.getInstance().getVoteMenu().openMenu(player, 9);
                }
            }
        };
        items[4] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Statistics", Material.EMERALD, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new StatisticsMenu().openMenu(player, 45);
            }
        };
        items[5] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Settings", Material.WATCH, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new SettingsMenu().openMenu(player, 9);
            }
        };

        items[6] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Broadcast", Material.NAME_TAG, false, Style.GRAY + "クリックしてサーバー全体に呼びかけます", Style.GRAY + "Click to broadcast on network to call players.")) {
            @Override
            public void clickAction(Player player) {
                if(!RouteSG.getInstance().getBroadcastCooldown().hasExpired()){
                    player.sendMessage(Style.RED + "Please wait " + RouteSG.getInstance().getBroadcastCooldown().getTimeLeft() + "s to broadcast on network.");
                }else{
                    RouteSG.getInstance().setBroadcastCooldown(new Cooldown(5000));
                    player.sendMessage(Style.AQUA + "Successfully sent broadcast to network.");
                    RouteCore.get().getPidgin().sendPacket(
                            new PacketClickableBroadcast(
                                    "&b[" + RouteSG.getSettings().getServerName() + "] &aStarts in " + RouteSG.getGameTask().getTime() + "s! " + "&e[" + Bukkit.getOnlinePlayers().size() + "/24]",
                            "&7(Click to Join)", "/play " + RouteSG.getSettings().getServerName(), "Join " + RouteSG.getSettings().getServerName()));
                }
            }
        };

        items[8] = new ClickActionItem(ItemUtility.createDye(Style.AQUA + "Back to Lobby", 1, 14)) {
            @Override
            public void clickAction(Player player) {
                BungeeUtils.connect(player, "SurvivalGames");
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
                GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new StatisticsMenu().openMenu(player, 45);
            }
        };
        items[6] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Settings", Material.WATCH, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new SettingsMenu().openMenu(player, 9);
            }
        };

        items[8] = new ClickActionItem(ItemUtility.createDye(Style.AQUA + "Back to Lobby", 1, 14)) {
            @Override
            public void clickAction(Player player) {
                EzQueueAPI.addToQueue(player, "SurvivalGames");
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
                if(!player.getUniqueId().equals(RouteSG.getGameTask().getHost())){
                    ChatUtility.sendMessage(player, ChatColor.RED + "You are not host.");
                    return;
                }
                new HostManagerMenu().openMenu(player);
            }
        };
        items[5] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Statistics", Material.EMERALD, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new StatisticsMenu().openMenu(player, 45);
            }
        };
        items[6] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Settings", Material.WATCH, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new SettingsMenu().openMenu(player, 9);
            }
        };

        items[8] = new ClickActionItem(ItemUtility.createDye(Style.AQUA + "Back to Lobby", 1, 14)) {
            @Override
            public void clickAction(Player player) {
                EzQueueAPI.addToQueue(player, "SurvivalGames");
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
                GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new SettingsMenu().openMenu(player, 9);
            }
        };
        items[1] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Statistics", Material.EMERALD, false)) {
            @Override
            public void clickAction(Player player) {
                GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                new StatisticsMenu().openMenu(player, 45);
            }
        };
        items[3] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Spectate Manager", Material.COMPASS, false)) {
            @Override
            public void clickAction(Player player) {
                new SpectateManagerMenu().openMenu(player, 27);
            }
        };
        items[4] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "Random Teleport", Material.EYE_OF_ENDER, false)) {
            @Override
            public void clickAction(Player player) {
                List<GamePlayer> players = RouteSG.getGameTask().getAlivePlayers();
                if(players.size() <= 0){
                    player.sendMessage(Style.RED + "Error: Failed to teleport to player");
                    return;
                }
                Collections.shuffle(players);
                GamePlayer target = players.get(0);
                if(target != null){
                    player.teleport(target.getPlayer());
                    ChatUtility.sendMessage(player, ChatColor.GRAY + "You are spectating " + target.getSGName());
                }else{
                    player.sendMessage(Style.RED + "Error: Failed to teleport to player");
                }
            }
        };
        items[6] = new ClickActionItem(ItemUtility.createItemStack(Style.AQUA + "View Inventory", Material.STICK, false)) {
            @Override
            public void clickAction(Player player) {

            }
        };
        items[8] = new ClickActionItem(ItemUtility.createDye(Style.AQUA + "Back to Lobby", 1, 14)) {
            @Override
            public void clickAction(Player player) {
                EzQueueAPI.addToQueue(player, "SurvivalGames");
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
