package net.hotsmc.sg.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

@Getter
@Setter
public class GameChest {

    private Inventory inventory;
    private Location location;
    private boolean tier2;
    private boolean opened = false;

    public GameChest(boolean tier2, Location location) {
        this.tier2 = tier2;
        this.location = location;
        this.inventory = Bukkit.createInventory(null, 27, "Chest");
    }
}
