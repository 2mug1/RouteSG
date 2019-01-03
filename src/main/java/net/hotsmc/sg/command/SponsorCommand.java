package net.hotsmc.sg.command;

import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.game.GameState;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SponsorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(!HSG.getGameTask().isSponsor()){
                ChatUtility.sendMessage(player, ChatColor.RED + "Sponsor has been disabled by settings.");
                return true;
            }
            if(args.length == 1){
                if(HSG.getGameTask().getState() == GameState.Lobby || HSG.getGameTask().getState() == GameState.PreDeathmatch ||
                        HSG.getGameTask().getState() == GameState.DeathMatch || HSG.getGameTask().getState() == GameState.EndGame){
                    ChatUtility.sendMessage(player, ChatColor.RED + "You can't send sponsor items at this time.");
                    return true;
                }
                String targetName = args[0];
                if(targetName.equals(player.getName())){
                    ChatUtility.sendMessage(player, ChatColor.RED + "You can't send sponsor item yourself.");
                    return true;
                }
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
                    return true;
                }
                GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                if(gamePlayer == null)return true;
                if(!gamePlayer.isWatching()){
                    ChatUtility.sendMessage(player, ChatColor.RED + "You aren't spectator.");
                    return true;
                }
                targetGP.openSponsorMenu(player);
            }else{
                ChatUtility.sendMessage(player, ChatColor.RED + "/sponsor <player>");
            }
        }
        return true;
    }
}
