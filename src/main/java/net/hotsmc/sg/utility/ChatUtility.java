package net.hotsmc.sg.utility;

import net.hotsmc.sg.game.GamePlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtility {

    public static String PLUGIN_MESSAGE_PREFIX = "" + ChatColor.AQUA + ChatColor.BOLD + "Hots" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "SG" + ChatColor.DARK_GRAY + " » " + ChatColor.RESET;

    private static String format(String message) {
        return PLUGIN_MESSAGE_PREFIX + message;
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(format(message));
    }

    public static void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(format((message)));
    }

    public static void sendMessage(GamePlayer player, String message) {player.getPlayer().sendMessage(format(message));
    }

    public static void broadcast(String message){
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            sendMessage(player, message);
        }
    }
}
