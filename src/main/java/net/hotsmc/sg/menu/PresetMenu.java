package net.hotsmc.sg.menu;

import com.sun.javafx.css.CssError;
import net.hotsmc.core.gui.menu.Button;
import net.hotsmc.core.gui.menu.Menu;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.utility.ItemUtility;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameTask;
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
        buttons.put(0, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.DARK_AQUA + "Loadout" + Style.DARK_GRAY + ": " + Style.AQUA + "HotsSG Tournament", Material.NETHER_STAR, false,
                        Style.SCOREBAORD_SEPARATOR,
                        Style.YELLOW + " Host With Play" + Style.GRAY + ": " + Style.RED + Style.BOLD + "Disable",
                        Style.YELLOW + " Sponsor" + Style.GRAY + ": " + Style.RED + Style.BOLD + "Disable",
                        Style.YELLOW + " Spectate Roster Only" + Style.GRAY + ": " + Style.RED + Style.BOLD + "Disable",
                        Style.YELLOW + " Grace Period" + Style.GRAY + ": " + Style.GREEN + "27:00",
                        Style.SCOREBAORD_SEPARATOR);
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.closeInventory();
                GameTask game = HSG.getGameTask();
                game.setHostWithPlay(false);
                game.setSponsor(false);
                game.setSpectateRosterOnly(false);
                game.setGracePeriodTimeSec(1620);
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                player.sendMessage(Style.DARK_AQUA + " Loaded Preset" + Style.DARK_GRAY + ": " + Style.AQUA + Style.BOLD + "HotsSG Tournament");
                player.sendMessage("");
                player.sendMessage(Style.YELLOW + " Host With Play" + Style.GRAY + ": " + (game.isHostWithPlay() ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
                player.sendMessage(Style.YELLOW + " Sponsor" + Style.GRAY + ": " + (game.isSponsor() ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
                player.sendMessage(Style.YELLOW + " Spectate Roster Only" + Style.GRAY + ": " + (game.isSpectateRosterOnly() ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
                player.sendMessage(Style.YELLOW + " Grace Period" + Style.GRAY + ": " + Style.GREEN + "27:00");
                player.sendMessage("");
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
            }
        });

        buttons.put(1, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemUtility.createItemStack(Style.DARK_AQUA + "Loadout" + Style.DARK_GRAY + ": " + Style.AQUA + "祭League", Material.NETHER_STAR, false,
                        Style.SCOREBAORD_SEPARATOR,
                        Style.YELLOW + " Host With Play" + Style.GRAY + ": " + Style.RED + Style.BOLD + "Disable",
                        Style.YELLOW + " Sponsor" + Style.GRAY + ": " + Style.RED + Style.BOLD + "Disable",
                        Style.YELLOW + " Spectate Roster Only" + Style.GRAY + ": " + Style.RED + Style.BOLD + "Disable",
                        Style.YELLOW + " Grace Period" + Style.GRAY + ": " + Style.GREEN + "28:00",
                        Style.SCOREBAORD_SEPARATOR);
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.closeInventory();
                GameTask game = HSG.getGameTask();
                game.setHostWithPlay(false);
                game.setSponsor(false);
                game.setSpectateRosterOnly(false);
                game.setGracePeriodTimeSec(1680);
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                player.sendMessage(Style.DARK_AQUA + " Loaded Preset" + Style.DARK_GRAY + ": " + Style.AQUA + Style.BOLD + "祭League");
                player.sendMessage("");
                player.sendMessage(Style.YELLOW + " Host With Play" + Style.GRAY + ": " + (game.isHostWithPlay() ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
                player.sendMessage(Style.YELLOW + " Sponsor" + Style.GRAY + ": " + (game.isSponsor() ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
                player.sendMessage(Style.YELLOW + " Spectate Roster Only" + Style.GRAY + ": " + (game.isSpectateRosterOnly() ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
                player.sendMessage(Style.YELLOW + " Grace Period" + Style.GRAY + ": " + Style.GREEN + "28:00");
                player.sendMessage("");
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
            }
        });
        return buttons;
    }
}
