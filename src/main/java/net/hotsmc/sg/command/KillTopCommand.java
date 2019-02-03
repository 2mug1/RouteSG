package net.hotsmc.sg.command;

import net.hotsmc.core.other.Style;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameState;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.player.GamePlayerData;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillTopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
           final GameTask game = HSG.getGameTask();

           if(game.getState() == GameState.Lobby){
               sender.sendMessage(Style.RED + "Game hasn't been started yet.");
               return true;
           }

           if(game.getGamePlayerData().size() <= 0){
               sender.sendMessage(Style.RED + "Data size is 0");
               return true;
           }

            game.getGamePlayerData().sort((o1, o2) -> o1.getKills() > o2.getKills() ? -1 : 1);

            sender.sendMessage(Style.HORIZONTAL_SEPARATOR);
            sender.sendMessage(Style.YELLOW + Style.BOLD + " Kill Top");
            int killRank = 1;
            for(GamePlayerData data : game.getGamePlayerData()){
                if(killRank >= 10) {
                    break;
                }
                sender.sendMessage(Style.YELLOW + "#" + killRank + Style.DARK_GRAY + ": " + (data.getTeam() == null ? Style.WHITE + data.getName() : data.getTeam().getPrefix() + data.getName()) + Style.GRAY + " - " + Style.WHITE + data.getKills() + " kills");
                killRank++;
            }
            sender.sendMessage(Style.HORIZONTAL_SEPARATOR);
        }
        return true;
    }
}
