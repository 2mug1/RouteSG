package net.routemc.sg.command;

import net.routemc.sg.RouteSG;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.utility.ChatUtility;
import net.routemc.sg.utility.NumberUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (RouteSG.getGameTask().getGameConfig().isCustomSG()) {
                ChatUtility.sendMessage(player, ChatColor.RED + "Custom SG.");
                return true;
            }
            if (!RouteSG.getGameTask().getVoteManager().isVoting()) {
                ChatUtility.sendMessage(player, ChatColor.RED + "Not voting.");
                return true;
            }
            if (args.length == 0) {
                RouteSG.getGameTask().getVoteManager().send(player);
                return true;
            }
            if (args.length == 1) {
                if (!RouteSG.getGameTask().getVoteManager().isVoting()) {
                    ChatUtility.sendMessage(player, ChatColor.RED + "Not voting.");
                    return true;
                }
                if (!NumberUtility.isNumber(args[0])) {
                    ChatUtility.sendMessage(player, ChatColor.RED + "Please enter with a integer.");
                } else {
                    GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                    int id = Integer.parseInt(args[0]);
                    if (id < 1) {
                        ChatUtility.sendMessage(player, ChatColor.RED + "Please enter with between 1 and 5");
                    } else {
                        RouteSG.getGameTask().getVoteManager().addVote(gamePlayer, id);
                    }
                }
            } else {
                ChatUtility.sendMessage(player, ChatColor.RED + "/vote # | /v #");
            }
        }
        return true;
    }
}
