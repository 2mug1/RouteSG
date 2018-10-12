package net.hotsmc.sg.command;

import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SidebarCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(args.length == 1){
                if(args[0].equalsIgnoreCase("minimize")){
                    GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                    if(gamePlayer == null)return true;
                    gamePlayer.toggleSidebarMinimize();
                }
            }else{
                ChatUtility.sendMessage(player, ChatColor.RED + "/sidebar minimize");
            }
        }
        return true;
    }
}
