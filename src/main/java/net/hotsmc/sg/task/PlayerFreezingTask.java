package net.hotsmc.sg.task;

import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerFreezingTask extends BukkitRunnable {

    private Player player;

    private Location location;

    public PlayerFreezingTask(Player player, Location location){
        this.player = player;
        this.location = location;
    }

    @Override
    public void run() {
        if(player == null || HSG.getGameTask().getGamePlayer(player).isWatching() || !player.isOnline() || HSG.getGameTask().getState() != GameState.PreGame && HSG.getGameTask().getState() != GameState.PreDeathmatch ){
            this.cancel();
            return;
        }
        if(player.getLocation().getBlockX() != location.getBlockX() || player.getLocation().getBlockZ() != location.getBlockZ()) {
            player.teleport(location);
        }
    }
}
