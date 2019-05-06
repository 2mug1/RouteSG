package net.routemc.sg.command;

import me.trollcoding.requires.utils.objects.Style;
import net.routemc.core.RouteAPI;
import net.routemc.sg.RouteSG;
import org.apache.logging.log4j.core.appender.routing.Route;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FRKBReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(RouteAPI.getRankOfPlayer(player).getWeight() < 5){
                player.sendMessage(Style.RED + "You don't have permission.");
                return true;
            }
            RouteSG.getSettings().reloadFRKB();
            player.sendMessage("Reloaded fishing rod kb.");
        }
        return true;
    }
}
