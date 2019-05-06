package net.routemc.sg.utility;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * 座標保存クラス
 */
@Data
public class PositionInfo {

    private String worldName;
    private double x, y, z;
    private float yaw, pitch;

    public PositionInfo(String worldName, double x, double y, double z, double yaw, double pitch){
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = (float) yaw;
        this.pitch = (float) pitch;
    }

    public PositionInfo(Player player){
        this.worldName = player.getWorld().getName();
        this.x = player.getLocation().getX();
        this.y = player.getLocation().getY();
        this.z = player.getLocation().getZ();
        this.yaw = player.getLocation().getYaw();
        this.pitch = player.getLocation().getPitch();
    }

    /**
     * Locationで返します
     * @return
     */
    public Location toLocation(){
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public String locationFormat(){
        return worldName + "/" + x + "/" + y + "/" + z + "/" + yaw + "/" + pitch;
    }
}
