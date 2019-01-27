package net.hotsmc.sg.command;

import net.hotsmc.core.other.Style;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.player.GamePlayer;
import net.hotsmc.sg.team.GameTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamListCommand  implements CommandExecutor {

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
            if (gp == null) return true;
            if (gp.isInTeam()) {
                final GameTeam team = gp.getInTeam();
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                player.sendMessage(Style.YELLOW + Style.BOLD + " Your Team "+ Style.GRAY + "(#" + team + ")");
                for(String a : team.getPlayersAsStr()){
                    player.sendMessage(Style.GRAY + "- " + Style.WHITE + a);
                }
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
            }else{
                player.sendMessage(Style.RED + "You aren't in team");
            }
        }
        return true;
    }
}
