package net.routemc.sg.listener.listeners;

import net.routemc.sg.RouteSG;
import net.routemc.sg.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.ItemStack;

public class BoatListener implements Listener {

    @EventHandler
    public void onDamage(VehicleDamageEvent event) {
        if (event.getVehicle() instanceof Boat && event.getAttacker() instanceof Player) {
            Player player = (Player) event.getAttacker();
            GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
            if (gamePlayer == null) return;
            if (gamePlayer.isWatching()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event){
        if(event.getRightClicked() instanceof Boat){
            Boat boat = (Boat) event.getRightClicked();
            Location location = boat.getLocation();
            if (location == null) return;
            if (!location.getBlock().getLocation().subtract(0D, 1D, 0D).getBlock().isLiquid()) {
                boat.remove();
                Player player = event.getPlayer();
                GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return;
                if (gamePlayer.isWatching()) {
                    event.setCancelled(true);
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.BOAT));
                }
            }
        }
    }

    /* 廃止
    @EventHandler
    public void onEnter(VehicleEnterEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (vehicle instanceof Boat) {
            Location location = vehicle.getLocation();
            if (location == null) return;
            if (!location.getBlock().getLocation().subtract(0D, 1D, 0D).getBlock().isLiquid()) {
                if (!vehicle.isDead()) {
                    vehicle.remove();
                    location.getWorld().dropItemNaturally(location, new ItemStack(Material.BOAT));
                }
            }
        }
    }*/
}