package net.routemc.sg.utility;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.routemc.sg.RouteSG;
import org.bukkit.entity.Player;

public class BungeeUtils {

    public static void connect(Player player, String serverName) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(serverName);
        player.sendPluginMessage(RouteSG.getInstance(), "BungeeCord", output.toByteArray());
    }
}
