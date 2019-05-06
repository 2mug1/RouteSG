package net.routemc.sg.menu;

import me.trollcoding.requires.gui.Button;
import me.trollcoding.requires.gui.Menu;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.core.RouteAPI;
import net.routemc.sg.RouteSG;
import net.routemc.sg.database.PlayerData;
import net.routemc.sg.utility.DateUtility;
import net.routemc.sg.utility.ItemUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsMenu extends Menu {

    public StatisticsMenu() {
        super(false);
    }

    @Override
    public String getTitle(Player player) {
        return player.getName() + " - Stats";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for(int i = 0; i < 45; i++){
            buttons.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createGlassPane(" ", 1, 15);
                }
            });
        }
        buttons.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                PlayerData playerData = RouteSG.getGameTask().getGamePlayer(player).getPlayerData();
                long total = RouteSG.getMongoConnection().getPlayers().countDocuments();
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.WHITE + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getPoint());
                lore.add(ChatColor.WHITE + "Games Won Ranking" + ChatColor.DARK_GRAY + ": #" + ChatColor.YELLOW + playerData.getWonRank() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + total);
                lore.add(ChatColor.WHITE + "Kill Ranking " + ChatColor.DARK_GRAY + ": #" + ChatColor.YELLOW + playerData.getKillRank() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + total);
                lore.add(ChatColor.WHITE + "Point Ranking" + ChatColor.DARK_GRAY + ": #" + ChatColor.YELLOW + playerData.getPointRank() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + total);
                lore.add(ChatColor.WHITE + "Games (Won/Total)" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getWin() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + playerData.getPlayed());
                lore.add(ChatColor.WHITE + "Top3 (Placed/Total)" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getTop3() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + playerData.getPlayed());
                lore.add(ChatColor.WHITE + "Kills" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getKill());
                lore.add(ChatColor.WHITE + "Chests" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getChests());
                lore.add(ChatColor.WHITE + "First Played" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + DateUtility.getDateFormatByTimestamp(playerData.getFirstPlayed()));
                lore.add(0, Style.SCOREBAORD_SEPARATOR);
                lore.add(Style.SCOREBAORD_SEPARATOR);
                return ItemUtility.createPlayerSkull(player.getName(), RouteAPI.getColoredName(player), lore);
            }
        });

        buttons.put(29, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.YELLOW + "Kill Top10", Material.DIAMOND_SWORD, false, RouteSG.getMongoConnection().getTop10("KILL"));
            }
        });
        buttons.put(31, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.YELLOW + "Point Top10", Material.IRON_INGOT, false, RouteSG.getMongoConnection().getTop10("POINT"));
            }
        });
        buttons.put(33, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.YELLOW + "Win Top10", Material.GOLDEN_APPLE, false, RouteSG.getMongoConnection().getTop10("WIN"));
            }
        });
        return buttons;
    }
}
