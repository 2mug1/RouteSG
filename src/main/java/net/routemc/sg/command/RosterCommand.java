package net.routemc.sg.command;

import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.game.GameTask;
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
            final GameTask game = RouteSG.getGameTask();
            if(!game.getGameConfig().isCustomSG()){
                player.sendMessage(Style.RED + "This server isn't custom sg.");
                return true;
            }
            if(game.getGameConfig().isCustomSG()) {
                if (RouteSG.getInstance().getWhitelistedPlayers().size() >= 1) {
                    List<String> roster = new ArrayList<>();
                    for (String p : RouteSG.getInstance().getWhitelistedPlayers()) {
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
