package net.hotsmc.sg.listener.listeners;

import net.hotsmc.core.scoreboard.BoardTimer;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BoatListener implements Listener {

    @EventHandler
    public void onDamage(VehicleDamageEvent event) {
        if (event.getVehicle() instanceof Boat && event.getAttacker() instanceof Player) {
            Player player = (Player) event.getAttacker();
            GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
            if (gamePlayer == null) return;
            if (gamePlayer.isWatching()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(VehicleCreateEvent event){
        Vehicle vehicle = event.getVehicle();
        if(vehicle instanceof Boat){
            if(!vehicle.getLocation().getBlock().getLocation().subtract(0D, 1D, 0D).getBlock().isLiquid()){
                Location location = vehicle.getLocation();
                if(location == null)return;
                vehicle.remove();
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.BOAT));
            }
        }
    }
}