package net.hotsmc.sg.command;

import net.hotsmc.core.other.Style;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.reflection.BukkitReflection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ObserverCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            final GameTask game = HSG.getGameTask();
            if (!game.getGameConfig().isCustomSG()) {
                player.sendMessage(Style.RED + "This server isn't custom sg.");
                return true;
            }
            if (game.getHost() != player.getUniqueId()) {
                player.sendMessage(Style.RED + "You are not hosting the game.");
                return true;
            }
            for (int i = 0; i < args.length; i++) {
                String name = args[i];
                Player target = Bukkit.getPlayer(name);
                if(name.equalsIgnoreCase(player.getName())){
                    player.sendMessage(Style.RED + "Can't yourself.");
                }
                else if (target == null) {
                    player.sendMessage(Style.RED + "Error: " + name + " is offline.");
                }
                else if(HSG.getGameTask().getGamePlayer(target).getInTeam() != null){
                    player.sendMessage(Style.RED + "Error: " + name + " is in team.");
                }
                else if(HSG.getInstance().getObserverPlayers().contains(name.toLowerCase())){
                    player.sendMessage(Style.RED + "Error: " + name + " has already been observer.");
                }else{
                    BukkitReflection.setMaxPlayers(HSG.getInstance().getServer(), Bukkit.getServer().getMaxPlayers() + 1);
                    HSG.getInstance().getObserverPlayers().add(name.toLowerCase());
                    GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(target);
                    gamePlayer.enableWatching();
                    target.sendMessage(Style.YELLOW + "You have been updated to observer.");
                    player.sendMessage(Style.YELLOW +  name + " has been added to observers.");
                }
            }
        }
        return true;
    }
}
