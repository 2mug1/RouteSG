package net.routemc.sg.menu;

import me.trollcoding.requires.gui.Button;
import me.trollcoding.requires.gui.Menu;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.reflection.BukkitReflection;
import net.routemc.sg.utility.ChatUtility;
import net.routemc.sg.utility.ItemUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostManagerMenu extends Menu {

    public HostManagerMenu() {
        super(false);
    }

    @Override
    public String getTitle(Player player) {
        return Style.BLUE + Style.BOLD + "Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        if (!RouteSG.getGameTask().isTimerFlag()) {
            buttons.put(0, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createWool(Style.GREEN + Style.BOLD + "Start Countdown", 1, 5, Style.YELLOW + "Click to start countdown");
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();

                    if (RouteSG.getGameTask().getCurrentMap() == null) {
                        ChatUtility.sendMessage(player, Style.RED + "You need to select a map.");
                        return;
                    }

                    if (!RouteSG.getGameTask().isHostWithPlay()) {
                        if (RouteSG.getGameTask().getCustomSGPlayers().size() < RouteSG.getGameTask().getGameConfig().getStartPlayerSize()) {
                            ChatUtility.sendMessage(player, Style.RED + "More than " + RouteSG.getGameTask().getGameConfig().getStartPlayerSize() + " players required to start the countdown.");
                            return;
                        }
                    } else if (RouteSG.getGameTask().getGamePlayers().size() < RouteSG.getGameTask().getGameConfig().getStartPlayerSize()) {
                        ChatUtility.sendMessage(player, Style.RED + "More than " + RouteSG.getGameTask().getGameConfig().getStartPlayerSize() + " players required to start the countdown.");
                        return;
                    }

                    RouteSG.getGameTask().setTime(30);
                    RouteSG.getGameTask().setTimerFlag(true);
                    for (GamePlayer gamePlayer : RouteSG.getGameTask().getGamePlayers()) {
                        gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.CLICK, 1, 2);
                    }
                    ChatUtility.broadcast(Style.GREEN + Style.BOLD + "Countdown has been started.");
                }
            });
        } else {
            buttons.put(0, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createWool(Style.RED + Style.BOLD + "Stop Countdown", 1, 14, Style.YELLOW + "Click to stop countdown");
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    RouteSG.getGameTask().setTimerFlag(false);
                    RouteSG.getGameTask().setTime(30);
                    for (GamePlayer gamePlayer : RouteSG.getGameTask().getGamePlayers()) {
                        gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.CLICK, 1, 2);
                    }
                    ChatUtility.broadcast(Style.RED + Style.BOLD + "Countdown has been stopped.");
                }
            });
        }

        buttons.put(1, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.BLUE + Style.BOLD + "Maps", Material.EMPTY_MAP, false);
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                RouteSG.getInstance().getSelectMapMenu().openMenu(player);
            }
        });

        buttons.put(2, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Grace Period", Material.WATCH, false);
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new GracePeriodMenu().openMenu(player);
            }
        });

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack("" + Style.D_AQUA + Style.BOLD + "Preset", Material.REDSTONE_COMPARATOR, false, Style.GRAY + "Load Preset");
            }
            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.closeInventory();
                RouteSG.getInstance().getPresetMenu().openMenu(player);
            }
        });

        buttons.put(6, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.YELLOW + Style.BOLD + "Teams", Material.SKULL_ITEM, false);
            }
            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                RouteSG.getInstance().getTeamListMenu().openMenu(player, 54);
            }
        });

        buttons.put(7, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.GREEN + Style.BOLD + "Invitation", Material.FEATHER, false);
            }
            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                new InviteMenu().openMenu(player, 54);
            }
        });

        buttons.put(8, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> roster = new ArrayList<>();
                for(String p : RouteSG.getInstance().getWhitelistedPlayers()){
                    roster.add(Style.GRAY + "- " +  Style.AQUA + p);
                }
                return ItemUtility.createItemStack(Style.YELLOW + Style.BOLD + Style.UNDER_LINE + "Roster", Material.BOOK, false, roster);
            }
        });

        for(int i = 9; i < 18; i++){
            buttons.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createGlassPane(" ", 1, 6);
                }
            });
        }

        if (!RouteSG.getGameTask().isHostWithPlay()) {
            buttons.put(21, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Host with Play: " + Style.RED + Style.BOLD + "Disabled", Material.GOLD_SWORD, false);
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    if (Bukkit.getServer().getOnlinePlayers().size() >= 25) {
                        ChatUtility.sendMessage(player, Style.RED + "Server is fully... Can't be changed " + Style.LIGHT_PURPLE + Style.BOLD + "Host with play");
                        return;
                    }
                    RouteSG.getGameTask().setHostWithPlay(true);
                    RouteSG.getGameTask().getGamePlayer(player).disableWatching();
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Host with Play " + Style.GRAY + "has been " + Style.GREEN + Style.BOLD + "Enabled");
                    BukkitReflection.setMaxPlayers(RouteSG.getInstance().getServer(), 24 + RouteSG.getInstance().getObserverPlayers().size());
                }
            });
        } else {
            buttons.put(21, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Host with Play: " + Style.GREEN + Style.BOLD + "Enabled", Material.GOLD_SWORD, false);
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    RouteSG.getGameTask().setHostWithPlay(false);
                    RouteSG.getGameTask().getGamePlayer(player).enableWatching();
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Host with Play " + Style.GRAY + "has been " + Style.RED + Style.BOLD + "Disabled");
                    BukkitReflection.setMaxPlayers(RouteSG.getInstance().getServer(), 25);
                }
            });
        }

        if(RouteSG.getGameTask().isSponsor()){
            buttons.put(22, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Sponsor: " + Style.GREEN + Style.BOLD + "Enabled", Material.GOLD_AXE, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    RouteSG.getGameTask().setSponsor(false);
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Sponsor " + Style.GRAY + "has been " + Style.RED + Style.BOLD + "Disabled");
                }
            });
        }else{
            buttons.put(22, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Sponsor: " + Style.RED + Style.BOLD + "Disabled", Material.GOLD_AXE, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    RouteSG.getGameTask().setSponsor(true);
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Sponsor " + Style.GRAY + "has been " + Style.GREEN + Style.BOLD + "Enabled");
                }
            });
        }

        if(RouteSG.getGameTask().isSpectateRosterOnly()){
            buttons.put(23, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Spectate Roster Only: " + Style.GREEN + Style.BOLD + "Enabled", Material.COMPASS, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    RouteSG.getGameTask().setSpectateRosterOnly(false);
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Spectate Roster Only " + Style.GRAY + "has been " + Style.RED + Style.BOLD + "Disabled");
                }
            });
        }else{
            buttons.put(23, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Spectate Roster Only: " + Style.RED + Style.BOLD + "Disabled", Material.COMPASS, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    RouteSG.getGameTask().setSpectateRosterOnly(true);
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Spectate Roster Only " + Style.GRAY + "has been " + Style.GREEN + Style.BOLD + "Enabled");
                }
            });
        }

        return buttons;
    }
}
