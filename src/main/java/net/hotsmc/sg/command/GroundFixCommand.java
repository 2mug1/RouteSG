package net.hotsmc.sg.command;

import net.hotsmc.core.HotsCore;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class GroundFixCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            CraftPlayer cplayer = (CraftPlayer) player;
            //1.8は利用できない
            if (cplayer.getHandle().playerConnection.networkManager.getVersion() == 47) {
                ChatUtility.sendMessage(player, ChatColor.RED + "Ground Fix Command is unavailable 1.8");
                return true;
            }

            GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
            if (gamePlayer == null) return true;
            for (GamePlayer gp : HSG.getGameTask().getGamePlayers()) {
                if (gp != gamePlayer) {
                    if (!gp.isWatching() && !HotsCore.getHotsPlayer(gamePlayer.getPlayer()).isVanish()) {
                        gamePlayer.hidePlayer(gp);
                        gamePlayer.showPlayer(gp);
                    }
                }
            }
            ChatUtility.sendMessage(gamePlayer,ChatColor.GREEN + "Successfully fixed player sync.");
        }
        return true;
    }
}
