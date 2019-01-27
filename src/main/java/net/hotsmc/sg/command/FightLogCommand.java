package net.hotsmc.sg.command;

import net.hotsmc.core.other.Style;
import net.hotsmc.core.utility.DateUtility;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameState;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.player.GamePlayer;
import net.hotsmc.sg.player.GamePlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FightLogCommand  implements CommandExecutor {

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
            if(gp.getUUID() == game.getHost()){
                if(game.getState() == GameState.Lobby){
                    player.sendMessage(Style.RED + "Game hasn't been started yet.");
                    return true;
                }
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                player.sendMessage(Style.YELLOW + Style.BOLD + " Fight Log" + Style.GRAY + " (" + DateUtility.dateFormat() + " " +  DateUtility.getTimeAsJST() + ") - " + DateUtility.getTimeAsJST() + ") - " + HSG.getGameTask().getState().name() + " " + HSG.getGameTask().getFormatTime());
                for(String log : game.getFightLog()){
                    player.sendMessage(log);
                }
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
            }else{
                player.sendMessage(Style.RED + "You aren't host.");
            }
        }
        return true;
    }
}
