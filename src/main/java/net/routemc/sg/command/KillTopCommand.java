package net.routemc.sg.command;

import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.game.GameState;
import net.routemc.sg.game.GameTask;
import net.routemc.sg.player.GamePlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillTopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
           final GameTask game = RouteSG.getGameTask();

           if(game.getState() == GameState.Lobby){
               sender.sendMessage(Style.RED + "Game hasn't been started yet.");
               return true;
           }

           if(game.getGamePlayerData().size() <= 0){
               sender.sendMessage(Style.RED + "Data size is 0");
               return true;
           }

            game.getGamePlayerData().sort((o1, o2) -> o1.getKills() > o2.getKills() ? -1 : 1);

            sender.sendMessage(Style.HORIZONTAL_SEPARATOR);
            sender.sendMessage(Style.YELLOW + Style.BOLD + " Kill Top");
            int killRank = 1;
            for(GamePlayerData data : game.getGamePlayerData()){
                if(killRank > 10) {
                    break;
                }
                sender.sendMessage(Style.YELLOW + "#" + killRank + Style.DARK_GRAY + ": " + (data.getTeam() == null ? Style.WHITE + data.getName() : data.getTeam().getPrefix() + data.getName()) + Style.GRAY + " - " + Style.WHITE + data.getKills() + " kills");
                killRank++;
            }
            sender.sendMessage(Style.HORIZONTAL_SEPARATOR);
        }
        return true;
    }
}
