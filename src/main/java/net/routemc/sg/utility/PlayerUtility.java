package net.routemc.sg.utility;

import net.routemc.sg.RouteSG;
import net.minecraft.server.v1_7_R4.EnumClientCommand;
import net.minecraft.server.v1_7_R4.PacketPlayInClientCommand;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class PlayerUtility {

    public static void respawn(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayInClientCommand packet = new PacketPlayInClientCommand();
                try {
                    Field a = PacketPlayInClientCommand.class.getDeclaredField("a");
                    a.setAccessible(true);
                    a.set(packet, EnumClientCommand.PERFORM_RESPAWN);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                ((CraftPlayer) player).getHandle().playerConnection.a(packet);
            }
            //0.25秒後に自動リスポーン
        }.runTaskLater(RouteSG.getInstance(), 1);
    }

    public static void clearEffects(Player player) {
        for(PotionEffect effect : player.getActivePotionEffects())
        {
            player.removePotionEffect(effect.getType());
        }
    }
}
