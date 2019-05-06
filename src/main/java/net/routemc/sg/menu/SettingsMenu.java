package net.routemc.sg.menu;

import me.trollcoding.requires.gui.Button;
import me.trollcoding.requires.gui.Menu;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.database.PlayerData;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.utility.ItemUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SettingsMenu extends Menu {


    public SettingsMenu() {
        super(false);
    }

    @Override
    public String getTitle(Player player) {
        return "Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
        if (gamePlayer != null) {
            PlayerData playerData = gamePlayer.getPlayerData();
            if (playerData.isSidebarMCSG()) {
                buttons.put(0, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return ItemUtility.createItemStack(Style.YELLOW + "Sidebar MCSG: " + Style.GREEN + "Enabled", Material.EMPTY_MAP, false);
                    }

                    @Override
                    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                        player.closeInventory();
                        GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                        if (gamePlayer != null) {
                            gamePlayer.getPlayerData().updateSidebarMCSG(false);
                            gamePlayer.sendMessage(Style.YELLOW + "Sidebar MCSG" + Style.GRAY + " has been " + Style.RED + "Disabled");
                        }
                    }
                });
            } else {
                buttons.put(0, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return ItemUtility.createItemStack(Style.YELLOW + "Sidebar MCSG: " + Style.RED + "Disabled", Material.EMPTY_MAP, false);
                    }

                    @Override
                    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                        player.closeInventory();
                        GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                        if (gamePlayer != null) {
                            gamePlayer.getPlayerData().updateSidebarMCSG(true);
                            gamePlayer.sendMessage(Style.YELLOW + "Sidebar MCSG" + Style.GRAY + " has been " + Style.GREEN + "Enabled");
                        }
                    }
                });
            }
            if (playerData.isLobbyPvP()) {
                buttons.put(1, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return ItemUtility.createItemStack(Style.YELLOW + "Lobby PvP: " + Style.GREEN + "Enabled", Material.FISHING_ROD, false);
                    }

                    @Override
                    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                        player.closeInventory();
                        GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                        if (gamePlayer != null) {
                            gamePlayer.getPlayerData().updateLobbyPvP(false);
                            gamePlayer.sendMessage(Style.YELLOW + "Lobby PvP" + Style.GRAY + " has been " + Style.RED + "Disabled");
                        }
                    }
                });
            } else {
                buttons.put(1, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return ItemUtility.createItemStack(Style.YELLOW + "Lobby PvP: " + Style.RED + "Disabled", Material.FISHING_ROD, false);
                    }

                    @Override
                    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                        player.closeInventory();
                        GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                        if (gamePlayer != null) {
                            gamePlayer.getPlayerData().updateLobbyPvP(true);
                            gamePlayer.sendMessage(Style.YELLOW + "Lobby PvP" + Style.GRAY + " has been " + Style.GREEN + "Enabled");
                        }
                    }
                });
            }
        }
        return buttons;
    }
}
