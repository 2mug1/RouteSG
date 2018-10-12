package net.hotsmc.sg.utility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class GroundUtility {

    public static void doGroundFixCommand(Player me) {
        CraftPlayer cplayer = (CraftPlayer) me;
        //1.8は利用できない
        if (cplayer.getHandle().playerConnection.networkManager.getVersion() == 47) {
            ChatUtility.sendMessage(me, ChatColor.RED + "Ground Fix Command is unavailable 1.8");
        } else {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                me.hidePlayer(player);
                me.showPlayer(player);
            }
            ChatUtility.sendMessage(me, ChatColor.GREEN + "Successfully you have done ground fix.");
        }
    }

    public static void doGroundFix(Player me){
        CraftPlayer cplayer = (CraftPlayer) me;
        //1.7なら
        if (cplayer.getHandle().playerConnection.networkManager.getVersion() != 47) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                me.hidePlayer(player);
                me.showPlayer(player);
            }
        }
    }
}

