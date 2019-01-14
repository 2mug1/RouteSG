package net.hotsmc.sg.command;

import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
            if(gamePlayer == null)return true;
            if(!gamePlayer.isWatching()){
                ChatUtility.sendMessage(player,  ChatColor.RED + "You are not spectating.");
                return true;
            }
            if(args.length == 1) {
                String targetName = args[0];
                Player target = Bukkit.getPlayer(targetName);
                if(target == null)return true;
                if(!target.isOnline()){
                    ChatUtility.sendMessage(player, ChatColor.RED + "That player is offline.");
                    return true;
                }
                GamePlayer targetGP = HSG.getGameTask().getGamePlayer(target);
                if(targetGP == null)return true;
                if(targetGP.isWatching()){
                    ChatUtility.sendMessage(player, ChatColor.RED + targetName + " isn't playing the game.");
                }else{
                    player.teleport(target);
                    ChatUtility.sendMessage(player, ChatColor.GRAY + "You are spectating " + targetGP.getSGName());
                }
                return true;
            }else{
                ChatUtility.sendMessage(player, ChatColor.RED + "/spec <player>");
            }
        }
        return true;
    }
}
