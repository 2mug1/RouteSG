package net.hotsmc.sg.menu;

import net.hotsmc.core.gui.menu.Button;
import net.hotsmc.core.gui.menu.Menu;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.utility.ItemUtility;
import net.hotsmc.core.utility.TimeUtility;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameTask;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GracePeriodMenu extends Menu {

    public GracePeriodMenu() {
        super(true);
    }

    @Override
    public String getTitle(Player player) {
        return Style.BLUE + Style.BOLD + "Grace Period";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        buttons.put(3, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createDye(Style.RED + Style.BOLD + "-30", 1, 8);
            }
            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                GameTask game = HSG.getGameTask();
                if(game.getGracePeriodTimeSec() - 30 < 30){
                    player.sendMessage(Style.RED + "Can't set under 0:30");
                    player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1, 1);
                }else{
                    game.setGracePeriodTimeSec(game.getGracePeriodTimeSec() - 30);
                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                    player.sendMessage(Style.RED + "[-30] " + Style.YELLOW + Style.BOLD + "Grace Period " + Style.GRAY + "has been updated to " + Style.WHITE + TimeUtility.timeFormat(game.getGracePeriodTimeSec()));
                }
            }
        });
        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.YELLOW + Style.BOLD + "Grace Period: " + Style.WHITE + TimeUtility.timeFormat(HSG.getGameTask().getGracePeriodTimeSec()), Material.WATCH, false);
            }
        });
        buttons.put(5, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createDye(Style.GREEN + Style.BOLD + "+30", 1, 10);
            }
            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                GameTask game = HSG.getGameTask();
                if(game.getGracePeriodTimeSec() + 30 > 1800){
                    player.sendMessage(Style.RED + "Can't set over 30:00");
                    player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1, 1);
                }else{
                    game.setGracePeriodTimeSec(game.getGracePeriodTimeSec() + 30);
                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                    player.sendMessage(Style.GREEN + "[+30] " + Style.YELLOW + Style.BOLD + "Grace Period " + Style.GRAY + "has been updated to " + Style.WHITE + TimeUtility.timeFormat(game.getGracePeriodTimeSec()));
                }
            }
        });
        return buttons;
    }
}
