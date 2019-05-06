package net.routemc.sg.command;

import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.game.GameTask;
import net.routemc.sg.player.GamePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            final GameTask game = RouteSG.getGameTask();
            if (!game.getGameConfig().isCustomSG()) {
                player.sendMessage(Style.RED + "This server isn't custom sg.");
                return true;
            }
            GamePlayer gp = game.getGamePlayer(player);
            if(gp == null)return true;
            if(!gp.isInTeam()){
                player.sendMessage(Style.RED + "You aren't in team");
                return true;
            }
            if(args.length >= 1){
                StringBuilder msg = new StringBuilder();
                for(int i = 0; i < args.length; i++){
                    msg.append(args[i]);
                }
                if(gp.isInTeam()){
                    gp.getInTeam().chat(gp, msg.toString());
                }
            }else{
                player.sendMessage(Style.RED + "Team Chat: /tc <message>");
            }
        }
        return true;
    }
}
