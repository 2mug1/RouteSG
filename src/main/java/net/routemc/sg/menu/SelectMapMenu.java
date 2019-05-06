package net.routemc.sg.menu;

import me.trollcoding.requires.gui.Button;
import me.trollcoding.requires.gui.Menu;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.map.MapData;
import net.routemc.sg.utility.ChatUtility;
import net.routemc.sg.utility.ItemUtility;
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
                Collections.shuffle(RouteSG.getMapManager().getLoadedMapData());
                MapData mapData = RouteSG.getMapManager().getLoadedMapData().get(0);
                RouteSG.getGameTask().setCurrentMap(mapData);
                ChatUtility.normalBroadcast(Style.RED + Style.BOLD + "Map" + Style.DARK_GRAY + " » " + Style.AQUA + Style.BOLD + mapData.getName() + Style.YELLOW + " has been selected by Random");
            }
        });
        int slot = 9;
        for(MapData mapData : RouteSG.getMapManager().getLoadedMapData()){
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.AQUA + Style.BOLD + mapData.getName(), Material.EMPTY_MAP, false);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    RouteSG.getGameTask().setCurrentMap(mapData);
                    ChatUtility.normalBroadcast(Style.RED + Style.BOLD + "Map" + Style.DARK_GRAY + " » " + Style.AQUA + Style.BOLD + mapData.getName() + Style.YELLOW + " has been selected by Host");
                }
            });
            slot++;
        }
        return buttons;
    }
}
