package net.routemc.sg.command;

import me.trollcoding.requires.utils.objects.Style;
import net.routemc.core.RouteAPI;
import net.routemc.sg.RouteSG;
import net.routemc.sg.game.GameTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HostCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            final GameTask game = RouteSG.getGameTask();
            if(!game.getGameConfig().isCustomSG()){
                player.sendMessage(Style.RED + "This server isn't custom sg.");
                return true;
            }
            if(game.getGameConfig().isCustomSG()){
                if(game.getHost() == player.getUniqueId()){
                    player.sendMessage(Style.RED + "You have already been hosting.");
                    return true;
                }
                if(game.getHost() != null){
                    player.sendMessage(Style.RED + "You are not hosting the game.");
                    return true;
                }
                game.updateHost(player);
            }
        }
        return true;
    }
}
