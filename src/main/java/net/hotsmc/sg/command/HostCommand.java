package net.hotsmc.sg.command;

import net.hotsmc.core.HotsCore;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.player.PlayerRank;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HostCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            final GameTask game = HSG.getGameTask();
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
