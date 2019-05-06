package net.routemc.sg.menu;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.trollcoding.requires.gui.Button;
import me.trollcoding.requires.gui.Menu;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.utility.ItemUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InviteMenu extends Menu {

    public InviteMenu() {
        super(false);
    }

    @Override
    public String getTitle(Player player) {
        return Style.BLUE + Style.BOLD + "Select a player to invite";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        int slot = 0;
        for(String name : RouteSG.getInstance().getWhitelistedPlayers()){
                buttons.put(slot, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return ItemUtility.createItemStack(Style.WHITE + "Invite " + Style.YELLOW + Style.BOLD + name, Material.PAPER, false, Style.GRAY + "Click to invite player");
                    }
                    @Override
                    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                        if(Bukkit.getPlayer(name) != null){
                            player.sendMessage(Style.RED + name + " has been in this server.");
                            return;
                        }
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("ClickableMessageToPlayer");
                        out.writeUTF(name);
                        out.writeUTF("§e§lYou have been invited from");
                        out.writeUTF("§b§l§n" + RouteSG.getSettings().getServerName());
                        out.writeUTF("/play " + RouteSG.getSettings().getServerName());
                        player.sendPluginMessage(RouteSG.getInstance(), "BungeeCord", out.toByteArray());
                        player.sendMessage(Style.YELLOW + "You have been invited " + Style.AQUA + name);
                    }
                });
            slot++;
        }
        return buttons;
    }
}
