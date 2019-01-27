package net.hotsmc.sg.menu;

import net.hotsmc.core.gui.menu.Button;
import net.hotsmc.core.gui.menu.Menu;
import net.hotsmc.core.other.Style;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.player.GamePlayer;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.ItemUtility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectateMenu extends Menu {

    public SpectateMenu() {
        super(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Spectate players";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        List<GamePlayer> playing = new ArrayList<>();
        for (GamePlayer gamePlayer : HSG.getGameTask().getGamePlayers()) {
            if (!gamePlayer.isWatching()) {
                playing.add(gamePlayer);
            }
        }
        for (int i = 0; i < playing.size(); i++) {
            GamePlayer gamePlayer = playing.get(i);
            if (gamePlayer.getPlayer().isOnline() && gamePlayer.getPlayer() != null && !gamePlayer.isWatching()) {
                buttons.put(i, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return ItemUtility.createPlayerSkull(gamePlayer.getPlayer().getName(), gamePlayer.getSGName(), Style.WHITE + HSG.getGameTask().getGamePlayerData(gamePlayer.getName()).getKills() + Style.GRAY + " kills");
                    }
                    @Override
                    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                        if (!gamePlayer.getPlayer().isOnline()) {
                            ChatUtility.sendMessage(player, ChatColor.RED + "That player is offline.");
                            return;
                        }
                        if (gamePlayer.isWatching()) {
                            ChatUtility.sendMessage(player, ChatColor.RED + "That player is spectator.");
                        } else {
                            player.teleport(gamePlayer.getPlayer());
                            ChatUtility.sendMessage(player, ChatColor.GRAY + "You are spectating " + gamePlayer.getSGName());
                        }
                    }
                });
            }
        }
        return buttons;
    }
}
