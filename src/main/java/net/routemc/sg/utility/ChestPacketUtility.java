package net.routemc.sg.utility;

import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockAction;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ChestPacketUtility {

    public static void openChestPacket(Location chestLocation) {
        for (Player player : chestLocation.getWorld().getPlayers()) {
            PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(chestLocation.getBlockX(), chestLocation.getBlockY(), chestLocation.getBlockZ(), Blocks.CHEST, 1, 1);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static void closeChestPacket(Location chestLocation) {
        for (Player player : chestLocation.getWorld().getPlayers()) {
            PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(chestLocation.getBlockX(), chestLocation.getBlockY(), chestLocation.getBlockZ(), Blocks.CHEST, 1, 0);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }
}
