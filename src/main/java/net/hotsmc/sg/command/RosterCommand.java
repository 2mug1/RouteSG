package net.hotsmc.sg.command;

import net.hotsmc.core.HotsCore;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.player.PlayerRank;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RosterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            final GameTask game = HSG.getGameTask();
            if(!game.getGameConfig().isCustomSG()){
                player.sendMessage(Style.RED + "This server isn't custom sg.");
                return true;
            }
            if(game.getGameConfig().isCustomSG()) {
                if (HSG.getInstance().getWhitelistedPlayers().size() >= 1) {
                    List<String> roster = new ArrayList<>();
                    for (String p : HSG.getInstance().getWhitelistedPlayers()) {
                        roster.add(Style.AQUA + p);
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    Iterator it = roster.iterator();
                    while (it.hasNext()) {
                        stringBuilder.append(it.next());
                        if (it.hasNext()) {
                            stringBuilder.append(ChatColor.GRAY + ", ");
                        }
                    }
                    player.sendMessage(Style.YELLOW + Style.BOLD + "Roster");
                    player.sendMessage(stringBuilder.toString());
                }else{
                    player.sendMessage(ChatColor.RED + "Not registered player.");
                }
            }
        }
        return true;
    }
}
