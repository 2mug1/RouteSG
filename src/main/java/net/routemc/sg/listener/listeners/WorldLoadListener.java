package net.routemc.sg.listener.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldLoadListener implements Listener {

    @EventHandler
    public void init(WorldInitEvent event){
        event.getWorld().setKeepSpawnInMemory(false);
    }
}
