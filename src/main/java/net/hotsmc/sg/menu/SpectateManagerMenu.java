package net.hotsmc.sg.menu;

import net.hotsmc.core.gui.menu.Button;
import net.hotsmc.core.gui.menu.Menu;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.utility.DateUtility;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.player.GamePlayerData;
import net.hotsmc.sg.utility.ItemUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SpectateManagerMenu extends Menu {

    public SpectateManagerMenu() {
        super(false);
    }

    @Override
    public String getTitle(Player player) {
        return "Spectate Manager";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        buttons.put(11, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Teleport to Player", Material.NETHER_STAR, false);
            }
            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                HSG.getInstance().getSpectateMenu().openMenu(player, 27);
            }
        });
        buttons.put(15, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.LIGHT_PURPLE + Style.BOLD + "Kill Ranking", Material.DIAMOND_SWORD, false);
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.closeInventory();
                HSG.getGameTask().getGamePlayerData().sort((o1, o2) -> o1.getKills() > o2.getKills() ? -1 : 1);
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                player.sendMessage(Style.YELLOW + Style.BOLD + " Player Kills" + Style.GRAY + " (" + DateUtility.dateFormat() + " " +  DateUtility.getTimeAsJST() + ") - " + HSG.getGameTask().getState().name() + " " + HSG.getGameTask().getFormatTime());
                int killRank = 1;
                for(GamePlayerData data : HSG.getGameTask().getGamePlayerData()){
                    player.sendMessage(Style.YELLOW + "#" + killRank + Style.DARK_GRAY + ": " + (data.getTeam() == null ? Style.WHITE + data.getName() : data.getTeam().getPrefix() + data.getName()) + Style.GRAY + " - " + Style.WHITE + data.getKills() + " kills");
                    killRank++;
                }
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
            }
        });
        return buttons;
    }
}
