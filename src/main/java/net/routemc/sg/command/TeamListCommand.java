package net.routemc.sg.command;

import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.game.GameTask;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamListCommand  implements CommandExecutor {

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
            if (gp == null) return true;
            if(args.length == 0) {
                if (gp.isInTeam()) {
                    final GameTeam team = gp.getInTeam();
                    player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                    player.sendMessage(Style.YELLOW + Style.BOLD + " Your Team " + Style.GRAY + "(#" + team.getTeamID() + ")");
                    for (String a : team.getPlayersAsStr()) {
                        player.sendMessage(Style.GRAY + "- " + Style.WHITE + a);
                    }
                    player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                } else {
                    player.sendMessage(Style.RED + "You aren't in team");
                }
            }
            else if(args.length == 1){
                String targetName = args[0];
                Player target = Bukkit.getPlayer(targetName);
                if(target == null || !target.isOnline()){
                    player.sendMessage(Style.RED + targetName + " is offline.");
                    return true;
                }
                GamePlayer targetGP = RouteSG.getGameTask().getGamePlayer(target);
                if(targetGP == null){
                    player.sendMessage(Style.RED + targetName + " is offline.");
                    return true;
                }
                if(targetGP.isInTeam()){
                    final GameTeam team = targetGP.getInTeam();
                    player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                    player.sendMessage(Style.YELLOW + Style.BOLD + targetName + " of Team " + Style.GRAY + "(#" + team.getTeamID() + ")");
                    for (String a : team.getPlayersAsStr()) {
                        player.sendMessage(Style.GRAY + "- " + Style.WHITE + a);
                    }
                    player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                }else{
                    player.sendMessage(Style.RED + targetName + " isn't in team.");
                }
            }
        }
        return true;
    }
}
