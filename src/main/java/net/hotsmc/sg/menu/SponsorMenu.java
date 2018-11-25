package net.hotsmc.sg.menu;

import lombok.AllArgsConstructor;
import net.hotsmc.core.HotsCore;
import net.hotsmc.core.gui.menu.Button;
import net.hotsmc.core.gui.menu.Menu;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.ItemUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SponsorMenu extends Menu {

    private GamePlayer gamePlayer;

    public SponsorMenu(GamePlayer gamePlayer) {
        super(true);
        this.gamePlayer = gamePlayer;
    }

    @Override
    public String getTitle(Player player){
        return ChatColor.DARK_GRAY + "[ " + ChatColor.GOLD + "Sponsor" + ChatColor.DARK_GRAY + " ]";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        for(int i = 0; i < 8; i++) {
            if (gamePlayer.getSponsorItems().get(i).getType() != Material.AIR) {
                ItemStack itemStack = gamePlayer.getSponsorItems().get(i);
                Material type = itemStack.getType();
                buttons.put(i, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return ItemUtility.addLore(itemStack, ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Cost" + ChatColor.DARK_GRAY + "] " + ChatColor.YELLOW + getCost(type) + ChatColor.GREEN + " points");
                    }
                    @Override
                    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                        gamePlayer.sendSponsorItem(type, HSG.getGameTask().getGamePlayer(player));
                        player.closeInventory();
                    }
                });
            }
        }
        return buttons;
    }

    @Override
    public void onOpen(Player player) {
        ChatUtility.sendMessage(player, ChatColor.WHITE + "You have opened the sponsor menu for " + HotsCore.getHotsPlayer(gamePlayer.getPlayer()).getColorName() + ChatColor.WHITE + "!");
    }

    private int getCost(Material type){
        if(type == Material.ENDER_PEARL){
            return 150;
        }
        if(type == Material.IRON_INGOT){
            return 60;
        }
        if(type == Material.ARROW){
            return 50;
        }
        if(type == Material.EXP_BOTTLE){
            return 70;
        }
        if(type == Material.PORK){
            return 30;
        }
        if(type == Material.BOW){
            return 75;
        }
        if(type == Material.FLINT_AND_STEEL){
            return 75;
        }
        if(type == Material.MUSHROOM_SOUP){
            return 65;
        }
        return 0;
    }
}
