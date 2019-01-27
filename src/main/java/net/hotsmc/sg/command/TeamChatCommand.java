package net.hotsmc.sg.command;

import net.hotsmc.core.other.Style;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.player.GamePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            final GameTask game = HSG.getGameTask();
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
