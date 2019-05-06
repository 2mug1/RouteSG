package net.routemc.sg.command;

import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.player.GamePlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCountCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if(args.length == 0){
                final String name = ((Player)sender).getName();
                GamePlayerData gamePlayerData = RouteSG.getGameTask().getGamePlayerData(name);
                if (gamePlayerData == null) {
                    sender.sendMessage("Data can't be found.");
                    return true;
                }
                int rank = 1;
                for (GamePlayerData gamePlayerData1 : RouteSG.getGameTask().getGamePlayerData()) {
                    if (gamePlayerData1.getName().equalsIgnoreCase(name)) {
                        break;
                    }
                    rank++;
                }
                sender.sendMessage(Style.AQUA + gamePlayerData.getName() + " is currently having " + gamePlayerData.getKills() + " kills (#" + rank + ")");
            }
            else if(args.length == 1) {
               final String name = args[0];
                GamePlayerData gamePlayerData = RouteSG.getGameTask().getGamePlayerData(name);
                if (gamePlayerData == null) {
                    sender.sendMessage("Data can't be found.");
                    return true;
                }
                int rank = 1;
                for (GamePlayerData gamePlayerData1 : RouteSG.getGameTask().getGamePlayerData()) {
                    if (gamePlayerData1.getName().equalsIgnoreCase(name)) {
                        break;
                    }
                    rank++;
                }
                sender.sendMessage(Style.AQUA + gamePlayerData.getName() + " is currently having " + gamePlayerData.getKills() + " kills (#" + rank + ")");
            }
        }
        return true;
    }
}
