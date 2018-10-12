package net.hotsmc.sg.game;

import lombok.Getter;
import lombok.Setter;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.database.PlayerData;
import net.hotsmc.sg.game.PlayerScoreboard;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.PlayerUtility;
import net.minecraft.server.v1_7_R4.EnumClientCommand;
import net.minecraft.server.v1_7_R4.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

@Getter
@Setter
public class GamePlayer {

    private final Player player;
    private PlayerData playerData;
    private boolean watching = false;
    private boolean voted = false;
    private boolean frozen = false;
    private PlayerScoreboard scoreboard;

    public GamePlayer(Player player){
        this.player = player;
    }

    public void enableWatching(){
        watching = true;
        //全員から見えないようにする
        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            players.hidePlayer(player);
        }
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void disableWatching() {
        watching = false;
        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            players.showPlayer(player);
        }
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    public void respawn() {
        PlayerUtility.respawn(player);
    }

    public void teleport(Location location){
        player.teleport(location);
    }

    public void startScoreboard(boolean minimize) {
        scoreboard = new PlayerScoreboard(player, minimize);
        scoreboard.setup();
        scoreboard.start();
    }

    public void resetPlayer(){
        player.getInventory().clear();
        EntityEquipment equipment = player.getEquipment();
        equipment.setHelmet(null);
        equipment.setChestplate(null);
        equipment.setLeggings(null);
        equipment.setBoots(null);
        player.setFoodLevel(20);
        player.setHealth(20D);
    }

    public void toggleSidebarMinimize() {
        if(playerData.isSidebarMinimize()){
            playerData.updateSidebarMinimize(false);
            scoreboard.stop();
            startScoreboard(false);
            ChatUtility.sendMessage(player, ChatColor.GRAY + "Sidebar minimize has " + ChatColor.RED + "disabled");
        }else{
            playerData.updateSidebarMinimize(true);
            scoreboard.stop();
            startScoreboard(true);
            ChatUtility.sendMessage(player, ChatColor.GRAY + "Sidebar minimize has " + ChatColor.GREEN + "enabled");
        }
    }
}
