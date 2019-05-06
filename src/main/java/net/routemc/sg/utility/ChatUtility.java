package net.routemc.sg.utility;

import net.routemc.sg.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtility {

    public static String PLUGIN_MESSAGE_PREFIX = ChatColor.RESET + "";

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

    public static void normalBroadcast(String message){
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            player.sendMessage(message);
        }
    }
}
