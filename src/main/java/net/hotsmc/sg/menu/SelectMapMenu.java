package net.hotsmc.sg.menu;

import net.hotsmc.core.gui.menu.Button;
import net.hotsmc.core.gui.menu.Menu;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.utility.ItemUtility;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.map.MapData;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SelectMapMenu extends Menu {

    public SelectMapMenu() {
        super(false);
    }

    @Override
    public String getTitle(Player player) {
        return "Select a Map";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.YELLOW + Style.BOLD + "Random Select", Material.NETHER_STAR, false);
            }
            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                Collections.shuffle(HSG.getMapManager().getLoadedMapData());
                MapData mapData = HSG.getMapManager().getLoadedMapData().get(0);
                HSG.getGameTask().setCurrentMap(mapData);
                ChatUtility.normalBroadcast(Style.RED + Style.BOLD + "Map" + Style.DARK_GRAY + " » " + Style.AQUA + Style.BOLD + mapData.getName() + Style.YELLOW + " has been selected by Random");
            }
        });
        int slot = 9;
        for(MapData mapData : HSG.getMapManager().getLoadedMapData()){
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.AQUA + Style.BOLD + mapData.getName(), Material.EMPTY_MAP, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    HSG.getGameTask().setCurrentMap(mapData);
                    ChatUtility.normalBroadcast(Style.RED + Style.BOLD + "Map" + Style.DARK_GRAY + " » " + Style.AQUA + Style.BOLD + mapData.getName() + Style.YELLOW + " has been selected by Host");
                }
            });
            slot++;
        }
        return buttons;
    }
}
