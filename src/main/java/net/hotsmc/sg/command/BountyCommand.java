package net.hotsmc.sg.command;

import net.hotsmc.core.HotsCore;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.database.PlayerData;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.game.GameState;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.NumberUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BountyCommand implements CommandExecutor {

    //bounty player points
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(args.length == 2){
                String targetName = args[0];
                if(targetName.equals(player.getName())){
                    ChatUtility.sendMessage(player, ChatColor.RED + "You can't send bounty yourself.");
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
                String points = args[1];
                if(!NumberUtility.isNumber(points)){
                    ChatUtility.sendMessage(player, ChatColor.RED + "Please enter with a integer.");
                    return true;
                }
                if(HSG.getGameTask().getState() == GameState.Lobby || HSG.getGameTask().getState() == GameState.PreDeathMatch ||
                        HSG.getGameTask().getState() == GameState.DeathMatch || HSG.getGameTask().getState() == GameState.EndGame ){
                    ChatUtility.sendMessage(player, ChatColor.RED + "You can't bounty at this time.");
                    return true;
                }
                if(!gamePlayer.isWatching()){
                    ChatUtility.sendMessage(player, ChatColor.RED + "You aren't spectator.");
                    return true;
                }
                if(HSG.getGameTask().getBountyManager().isBountyMe(gamePlayer)){
                    ChatUtility.sendMessage(player, ChatColor.RED + "You have already sent bounty of " + HotsCore.getHotsPlayer(HSG.getGameTask().getBountyManager().getBountyPlayerWasMe(gamePlayer).getTarget().getPlayer()).getColorName() + ChatColor.RED + " for "  + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + HSG.getGameTask().getBountyManager().getBountyPlayerWasMe(gamePlayer).getPoints() + ChatColor.DARK_GRAY +"]");
                    return true;
                }
                PlayerData playerData = gamePlayer.getPlayerData();
                if(playerData == null)return true;
                int pointsInt = Integer.parseInt(points);
                if(pointsInt > playerData.getPoint()){
                    ChatUtility.sendMessage(player, ChatColor.RED + "You don't have enough points.");
                    return true;
                }
                if(pointsInt <= playerData.getPoint()){
                    gamePlayer.bountyPlayer(targetGP, pointsInt);
                    return true;
                }
            }else{
                ChatUtility.sendMessage(player, ChatColor.RED + "/bounty <player> <amount>.");
            }
        }
        return true;
    }
}
