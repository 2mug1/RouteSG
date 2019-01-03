package net.hotsmc.sg.menu;

import ca.wacos.nametagedit.NametagAPI;
import net.hotsmc.core.HotsCore;
import net.hotsmc.core.gui.menu.Button;
import net.hotsmc.core.gui.menu.Menu;
import net.hotsmc.core.other.Style;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.reflection.BukkitReflection;
import net.hotsmc.sg.team.GameTeam;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.ItemUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        return "Custom SG";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        if (!HSG.getGameTask().isTimerFlag()) {
            buttons.put(0, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return net.hotsmc.core.utility.ItemUtility.createWool(Style.GREEN + Style.BOLD + "Start Countdown", 1, 5, Style.YELLOW + "Click to start countdown");
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();

                    if (HSG.getGameTask().getCurrentMap() == null) {
                        ChatUtility.sendMessage(player, Style.RED + "You need to select a map.");
                        return;
                    }

                    if(HSG.getGameTask().isTeamFight()){
                        if(HSG.getGameTask().isHostWithPlay()) {
                            for (GamePlayer gamePlayer : HSG.getGameTask().getGamePlayers()) {
                                if (gamePlayer.getInTeam() == null && !HSG.getInstance().getObserverPlayers().contains(gamePlayer.getName().toLowerCase())) {
                                    player.sendMessage(Style.RED + "Require to join all players are belonging to any team.");
                                    return;
                                }
                            }
                        }else{
                            for (GamePlayer gamePlayer : HSG.getGameTask().getCustomSGPlayers()) {
                                if (gamePlayer.getInTeam() == null && !HSG.getInstance().getObserverPlayers().contains(gamePlayer.getName().toLowerCase())) {
                                    player.sendMessage(Style.RED + "Require to join all players are belonging to any team.");
                                    return;
                                }
                            }
                        }
                    }

                    if (!HSG.getGameTask().isHostWithPlay()) {
                        if (HSG.getGameTask().getCustomSGPlayers().size() < HSG.getGameTask().getGameConfig().getStartPlayerSize()) {
                            ChatUtility.sendMessage(player, Style.RED + "More than " + HSG.getGameTask().getGameConfig().getStartPlayerSize() + " players required to start the countdown.");
                            return;
                        }
                    } else if (HSG.getGameTask().getGamePlayers().size() < HSG.getGameTask().getGameConfig().getStartPlayerSize()) {
                        ChatUtility.sendMessage(player, Style.RED + "More than " + HSG.getGameTask().getGameConfig().getStartPlayerSize() + " players required to start the countdown.");
                        return;
                    }

                    HSG.getGameTask().setTime(HSG.getGameTask().getGameConfig().getLobbyTime());
                    HSG.getGameTask().setTimerFlag(true);
                    for (GamePlayer gamePlayer : HSG.getGameTask().getGamePlayers()) {
                        gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.CLICK, 1, 2);
                    }
                    ChatUtility.broadcast(Style.GREEN + Style.BOLD + "Countdown has been started.");
                }
            });
        } else {
            buttons.put(0, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return net.hotsmc.core.utility.ItemUtility.createWool(Style.RED + Style.BOLD + "Stop Countdown", 1, 14, Style.YELLOW + "Click to stop countdown");
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    HSG.getGameTask().setTimerFlag(false);
                    HSG.getGameTask().setTime(HSG.getGameTask().getGameConfig().getLobbyTime());
                    for (GamePlayer gamePlayer : HSG.getGameTask().getGamePlayers()) {
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
                HSG.getInstance().getSelectMapMenu().openMenu(player, 18);
            }
        });
        if (!HSG.getGameTask().isHostWithPlay()) {
            buttons.put(2, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Host with Play: " + Style.RED + Style.BOLD + "Disabled", Material.GOLD_SWORD, false);
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    if (Bukkit.getServer().getOnlinePlayers().length >= 25) {
                        ChatUtility.sendMessage(player, Style.RED + "Server is fully... Can't be changed " + Style.LIGHT_PURPLE + Style.BOLD + "Host with play");
                        return;
                    }
                    HSG.getGameTask().setHostWithPlay(true);
                    HSG.getGameTask().getGamePlayer(player).disableWatching();
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Host with Play " + Style.GRAY + "has been " + Style.GREEN + Style.BOLD + "Enabled");
                    BukkitReflection.setMaxPlayers(HSG.getInstance().getServer(), 24 + HSG.getInstance().getObserverPlayers().size());
                    if(HSG.getGameTask().getGamePlayer(player).getInTeam() != null){
                        HSG.getGameTask().getGamePlayer(player).getInTeam().getPlayers().remove(HSG.getGameTask().getGamePlayer(player));
                        HotsCore.getHotsPlayer(player).updateNameTag();
                    }
                }
            });
        } else {
            buttons.put(2, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Host with Play: " + Style.GREEN + Style.BOLD + "Enabled", Material.GOLD_SWORD, false);
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    HSG.getGameTask().setHostWithPlay(false);
                    HSG.getGameTask().getGamePlayer(player).enableWatching();
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Host with Play " + Style.GRAY + "has been " + Style.RED + Style.BOLD + "Disabled");
                    BukkitReflection.setMaxPlayers(HSG.getInstance().getServer(), 25);
                }
            });
        }

        if(HSG.getGameTask().isSponsor()){
            buttons.put(3, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Sponsor: " + Style.GREEN + Style.BOLD + "Enabled", Material.GOLD_AXE, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    HSG.getGameTask().setSponsor(false);
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Sponsor " + Style.GRAY + "has been " + Style.RED + Style.BOLD + "Disabled");
                }
            });
        }else{
            buttons.put(3, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Sponsor: " + Style.RED + Style.BOLD + "Disabled", Material.GOLD_AXE, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    HSG.getGameTask().setSponsor(true);
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Sponsor " + Style.GRAY + "has been " + Style.GREEN + Style.BOLD + "Enabled");
                }
            });
        }

        if(HSG.getGameTask().isGracePeriod3Minutes()){
            buttons.put(4, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Grace Period 3 minutes: " + Style.GREEN + Style.BOLD + "Enabled", Material.WATCH, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    HSG.getGameTask().setGracePeriod3Minutes(false);
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Grace Period 3 minutes " + Style.GRAY + "has been " + Style.RED + Style.BOLD + "Disabled");
                }
            });
        }else{
            buttons.put(4, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Grace Period 3 minutes: " + Style.RED + Style.BOLD + "Disabled", Material.WATCH, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    HSG.getGameTask().setGracePeriod3Minutes(true);
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Grace Period 3 minutes " + Style.GRAY + "has been " + Style.GREEN + Style.BOLD + "Enabled");
                }
            });
        }

        if(HSG.getGameTask().isTeamFight()){
            buttons.put(5, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Team Fight: " + Style.GREEN + Style.BOLD + "Enabled", Material.COAL, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    HSG.getGameTask().setTeamFight(false);
                    HSG.getGameTask().resetTeams();
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Team Fight " + Style.GRAY + "has been " + Style.RED + Style.BOLD + "Disabled");
                }
            });
        }else{
            buttons.put(5, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Team Fight: " + Style.RED + Style.BOLD + "Disabled", Material.COAL, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    HSG.getGameTask().setTeamFight(true);
                    ChatUtility.sendMessage(player, Style.LIGHT_PURPLE + Style.BOLD + "Team Fight " + Style.GRAY + "has been " + Style.GREEN + Style.BOLD + "Enabled");
                }
            });
        }

        buttons.put(6, new Button() {
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
                for(String p : HSG.getInstance().getWhitelistedPlayers()){
                    roster.add(Style.GRAY + "- " +  Style.AQUA + p);
                }
                return ItemUtility.createItemStack(Style.YELLOW + Style.BOLD + Style.UNDER_LINE + "Roster", Material.PAPER, false, roster);
            }
        });
        return buttons;
    }
}
