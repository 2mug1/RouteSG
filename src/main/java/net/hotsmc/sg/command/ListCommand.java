package net.hotsmc.sg.command;

import net.hotsmc.core.HotsCore;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (label.equalsIgnoreCase("list") || label.equalsIgnoreCase("l")) {
                Player player = (Player) sender;
                StringBuilder ingame = new StringBuilder();
                for (GamePlayer gamePlayer : HSG.getGameTask().getGamePlayers()) {
                    if (!gamePlayer.isWatching()) {
                        ingame.append(HotsCore.getHotsPlayer(gamePlayer.getPlayer()).getColorName()).append(ChatColor.WHITE).append(", ");
                    }
                }
                StringBuilder watching = new StringBuilder();
                for (GamePlayer gamePlayer : HSG.getGameTask().getGamePlayers()) {
                    if (gamePlayer.isWatching()) {
                        ingame.append(HotsCore.getHotsPlayer(gamePlayer.getPlayer()).getColorName()).append(ChatColor.WHITE).append(", ");
                    }
                }
                ChatUtility.sendMessage(player, ChatColor.WHITE + "There are " + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + HSG.getGameTask().getGamePlayers().size() + ChatColor.DARK_GRAY + "/" + ChatColor.GOLD + "24" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "players online.");
                ChatUtility.sendMessage(player,"" +  ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + "  " + ChatColor.WHITE + "Ingame" + ChatColor.DARK_GRAY + ": " + ingame.toString());
                ChatUtility.sendMessage(player,"" +  ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH + "  " + ChatColor.WHITE + "Watching" + ChatColor.DARK_GRAY + ": " + watching.toString());
            }
        }
        return true;
    }
}

