package net.routemc.sg.menu;

import me.trollcoding.requires.gui.Button;
import me.trollcoding.requires.gui.Menu;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.core.RouteAPI;
import net.routemc.core.RouteCore;
import net.routemc.core.profile.Profile;
import net.routemc.sg.RouteSG;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.utility.ChatUtility;
import net.routemc.sg.utility.ItemUtility;
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
        for (GamePlayer gamePlayer : RouteSG.getGameTask().getGamePlayers()) {
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
                        Profile profile = RouteAPI.getProfileByUUID(player.getUniqueId());
                        return ItemUtility.createPlayerSkull(gamePlayer.getPlayer().getName(), (profile.isInClan() ? profile.getClan().getStyleTag() + " " : "") + gamePlayer.getSGName(), Style.WHITE + RouteSG.getGameTask().getGamePlayerData(gamePlayer.getName()).getKills() + Style.GRAY + " kills");
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
