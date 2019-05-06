package net.routemc.sg.menu;

import me.trollcoding.requires.gui.Button;
import me.trollcoding.requires.gui.Menu;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.preset.Preset;
import net.routemc.sg.utility.ItemUtility;
import net.routemc.sg.utility.TimeUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PresetMenu extends Menu {

    public PresetMenu() {
        super(false);
    }

    @Override
    public String getTitle(Player player) {
        return Style.BLUE + Style.BOLD + "Preset";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        for(int i = 0; i < RouteSG.getInstance().getPresetManager().getPresets().size(); i++){
            final Preset preset = RouteSG.getInstance().getPresetManager().getPresets().get(i);
            buttons.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemUtility.createItemStack(Style.AQUA + "Loadout" + Style.DARK_GRAY + ": " + Style.YELLOW + Style.BOLD + preset.getPresetName(), Material.NETHER_STAR, false,
                            Style.SCOREBAORD_SEPARATOR,
                            Style.YELLOW + " Host With Play" + Style.GRAY + ": " + (preset.isHostWithPlay() ? Style.GREEN + Style.BOLD + "Enable" : Style.RED + Style.BOLD + "Disable"),
                            Style.YELLOW + " Sponsor" + Style.GRAY + ": " + (preset.isSponsor() ? Style.GREEN + Style.BOLD + "Enable" : Style.RED + Style.BOLD + "Disable"),
                            Style.YELLOW + " Spectate Roster Only" + Style.GRAY + ": " + (preset.isSpectateRosterOnly() ? Style.GREEN + Style.BOLD + "Enable" : Style.RED + Style.BOLD + "Disable"),
                            Style.YELLOW + " Grace Period" + Style.GRAY + ": " + Style.GREEN + TimeUtility.timeFormat(preset.getGracePeriodSec()),
                            Style.SCOREBAORD_SEPARATOR);
                }
                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    new HostManagerMenu().openMenu(player);
                    preset.apply(player);
                }
            });
        }
        return buttons;
    }
}
