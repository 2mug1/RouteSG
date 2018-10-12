package net.hotsmc.sg.utility;

import net.minecraft.server.v1_7_R4.MinecraftServer;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ServerUtility {

    public static void setMotd(String motd) {
        String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        Object minecraftserver = null;
        try {
            minecraftserver = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer").getDeclaredMethod("getServer", null).invoke(Bukkit.getServer(), null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Field f = null;
        try {
            f = minecraftserver.getClass().getSuperclass().getDeclaredField("motd");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            f.set(minecraftserver, motd);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}