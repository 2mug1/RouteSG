package net.hotsmc.sg.command;

import net.hotsmc.core.other.Style;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.player.GamePlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillTotalCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if(args.length == 1){
                String name = args[0];
                GamePlayerData gamePlayerData = HSG.getGameTask().getGamePlayerData(name);
                if(gamePlayerData == null){
                    sender.sendMessage("Data can't be found.");
                    return true;
                }
                int rank = 1;
                for(GamePlayerData gamePlayerData1 : HSG.getGameTask().getGamePlayerData()){
                    if(gamePlayerData1.getName().equalsIgnoreCase(name)){
                        break;
                    }
                    rank++;
                }
                sender.sendMessage(Style.AQUA + gamePlayerData.getName() + " is currently having " + gamePlayerData.getKills() + " kills (#" + rank + ")");
            }else{
                sender.sendMessage(Style.RED + "/kt <player>");
            }
        }
        return true;
    }
}
