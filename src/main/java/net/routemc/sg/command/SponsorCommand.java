package net.routemc.sg.command;

import net.routemc.sg.RouteSG;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.game.GameState;
import net.routemc.sg.utility.ChatUtility;
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
            if(!RouteSG.getGameTask().isSponsor()){
                ChatUtility.sendMessage(player, ChatColor.RED + "Sponsor has been disabled by settings.");
                return true;
            }
            if(args.length == 1){
                if(RouteSG.getGameTask().getState() == GameState.Lobby || RouteSG.getGameTask().getState() == GameState.PreDeathmatch ||
                        RouteSG.getGameTask().getState() == GameState.DeathMatch || RouteSG.getGameTask().getState() == GameState.EndGame){
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
                GamePlayer targetGP = RouteSG.getGameTask().getGamePlayer(target);
                if(targetGP == null)return true;
                if(targetGP.isWatching()){
                    ChatUtility.sendMessage(player, ChatColor.RED + targetName + " isn't playing the game.");
                    return true;
                }
                GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
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
