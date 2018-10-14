package net.hotsmc.sg.task;

import lombok.AllArgsConstructor;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class StrikeLightningTask extends BukkitRunnable {

    private Player player;

    @Override
    public void run() {
        if (player == null || !player.isOnline() || HSG.getGameTask().getState() != GameState.DeathMatch) {
            this.cancel();
            return;
        }
        Location location = player.getLocation();
        if (location == null) return;
        if (location.distance(HSG.getGameTask().getCurrentMap().getCenterLocation()) > HSG.getGameTask().getCircleSize()) {
            player.getLocation().getWorld().strikeLightning(player.getLocation());
        }
    }
}
