package net.routemc.sg.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.game.GameTask;
import net.routemc.sg.utility.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterPlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            final GameTask game = RouteSG.getGameTask();
            if (!game.getGameConfig().isCustomSG()) {
                ChatUtility.sendMessage(player, Style.RED + "This server isn't custom sg.");
                return true;
            }
            if (game.getGameConfig().isCustomSG()) {
                if (!player.getUniqueId().equals(game.getHost())) {
                    player.sendMessage(Style.RED + "You are not host.");
                    return true;
                }
            }

            for (int i = 0; i < args.length; i++) {
                String name = args[i];
                if (RouteSG.getInstance().getWhitelistedPlayers().contains(name.toLowerCase())) {
                    ChatUtility.sendMessage(player, Style.RED + "Error: Failed to register " + name);
                }
               else if (!RouteSG.getInstance().getWhitelistedPlayers().contains(name.toLowerCase())) {
                    RouteSG.getInstance().getWhitelistedPlayers().add(name.toLowerCase());
                    ChatUtility.sendMessage(player, Style.YELLOW + name + Style.AQUA + " has been added to roster.");
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("ClickableMessageToPlayer");
                    out.writeUTF(name);
                    out.writeUTF("§e§lYou have been invited from");
                    out.writeUTF("§b§l§n" + RouteSG.getSettings().getServerName());
                    out.writeUTF("/play " + RouteSG.getSettings().getServerName());
                    Bukkit.getServer().sendPluginMessage(RouteSG.getInstance(), "BungeeCord", out.toByteArray());
                }
            }
        }
        return true;
    }
}
