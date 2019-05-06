package net.routemc.sg.task;

import lombok.AllArgsConstructor;
import net.routemc.sg.RouteSG;
import net.routemc.sg.game.GameState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class StrikeLightningTask extends BukkitRunnable {

    private Player player;

    @Override
    public void run() {
        if (player == null || !player.isOnline() || RouteSG.getGameTask().getState() != GameState.DeathMatch) {
            this.cancel();
            return;
        }
        Location location = player.getLocation();
        if (location == null) {
            cancel();
            return;
        }
        if (location.distance(RouteSG.getGameTask().getCurrentMap().getCenterLocation()) > RouteSG.getGameTask().getCircleSize()) {
            player.getLocation().getWorld().strikeLightning(player.getLocation());
        }
    }
}
