package net.routemc.sg.command;

import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.utility.ChatUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class ListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            StringBuilder ingame = new StringBuilder();
            StringBuilder watching = new StringBuilder();
            Iterator<GamePlayer> it = RouteSG.getGameTask().getGamePlayers().iterator();
            while (it.hasNext()){
                GamePlayer gamePlayer = it.next();
                if(!gamePlayer.isWatching()) {
                    ingame.append(gamePlayer.getSGName());
                    if (it.hasNext()) {
                        ingame.append(Style.GRAY).append(" ");
                    }
                }else{
                    watching.append(gamePlayer.getSGName());
                    if (it.hasNext()) {
                        watching.append(Style.GRAY).append(" ");
                    }
                }
            }
            ChatUtility.sendMessage(player, ChatColor.WHITE + "There are " + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + RouteSG.getGameTask().getGamePlayers().size() + ChatColor.DARK_GRAY + "/" + ChatColor.GOLD + "24" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "players online.");
            ChatUtility.sendMessage(player, ChatColor.WHITE + "Ingame" + ChatColor.DARK_GRAY + ": " + ingame.toString());
            ChatUtility.sendMessage(player, ChatColor.WHITE + "Watching" + ChatColor.DARK_GRAY + ": " + watching.toString());
        }
        return true;
    }
}

