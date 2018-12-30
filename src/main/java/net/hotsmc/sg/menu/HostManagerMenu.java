package net.hotsmc.sg.menu;

import net.hotsmc.core.HotsCore;
import net.hotsmc.core.gui.menu.Button;
import net.hotsmc.core.gui.menu.Menu;
import net.hotsmc.core.other.Style;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.ItemUtility;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
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
        if(!HSG.getGameTask().isTimerFlag()) {
            buttons.put(1, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return net.hotsmc.core.utility.ItemUtility.createWool(Style.GREEN + Style.BOLD + "Start Countdown",1, 5, Style.YELLOW + "Click to start countdown");
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();

                    if(HSG.getGameTask().getCurrentMap() == null){
                        ChatUtility.sendMessage(player, Style.RED + "You need to select a map.");
                        return;
                    }

                    if (HSG.getGameTask().getCustomSGPlayers().size() < HSG.getGameTask().getGameConfig().getStartPlayerSize()) {
                        ChatUtility.sendMessage(player, Style.RED + "More than " + HSG.getGameTask().getGameConfig().getStartPlayerSize() + " players required to start the game.");
                        return;
                    }

                    HSG.getGameTask().setTime(HSG.getGameTask().getGameConfig().getLobbyTime());
                    HSG.getGameTask().setTimerFlag(true);
                    for(GamePlayer gamePlayer : HSG.getGameTask().getGamePlayers()) {
                        gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.CLICK, 2, 1);
                    }
                }
            });
        }else{
            buttons.put(1, new Button() {
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
                        gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.CLICK, 2, 1);
                    }
                }
            });
        }
        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.BLUE + Style.BOLD + "Send All Registered Players", Material.LEASH, false);
            }
            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.closeInventory();
                if(HSG.getInstance().getWhitelistedPlayers().size() <= 0){
                    ChatUtility.sendMessage(player, Style.RED + "Not registered player.");
                    return;
                }
                for (String name : HSG.getInstance().getWhitelistedPlayers()) {
                    HotsCore.getInstance().getBungeeChannelApi().connectOther(name, HotsCore.getInstance().getSettings().getServerName());
                }
            }
        });
        buttons.put(7, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.BLUE + Style.BOLD + "Maps", Material.EMPTY_MAP, false);
            }
            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                HSG.getInstance().getSelectMapMenu().openMenu(player, 18);
            }
        });
        return buttons;
    }
}
