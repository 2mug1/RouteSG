package net.hotsmc.sg.chest;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

@Getter
@Setter
public class GameChest {

    private Inventory inventory;
    private Location location;
    private boolean tier2;
    private boolean opened = false;

    public GameChest(boolean tier2, Location location, BlockFace face) {
        this.tier2 = tier2;
        this.location = location;
        this.inventory = Bukkit.createInventory(null, 27, "Chest");
        setFacingDirection(face, location.getBlock());
    }

    private static void setFacingDirection(final BlockFace face, final Block block) {
        final BlockState state = block.getState();
        final MaterialData materialData = state.getData();
        if (materialData instanceof Directional) {
            final Directional directional = (Directional) materialData;
            directional.setFacingDirection(face);
            state.update();
        }
    }
}
