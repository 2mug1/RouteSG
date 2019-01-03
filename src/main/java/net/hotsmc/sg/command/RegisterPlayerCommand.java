package net.hotsmc.sg.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.hotsmc.core.HotsCore;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.player.PlayerRank;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RegisterPlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            final GameTask game = HSG.getGameTask();
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
                if (HSG.getInstance().getWhitelistedPlayers().contains(name.toLowerCase())) {
                    ChatUtility.sendMessage(player, Style.RED + "Error: Failed to register " + name);
                }
               else if (!HSG.getInstance().getWhitelistedPlayers().contains(name.toLowerCase())) {
                    HSG.getInstance().getWhitelistedPlayers().add(name.toLowerCase());
                    ChatUtility.sendMessage(player, Style.YELLOW + name + Style.AQUA + " has been added to roster.");
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("ClickableMessageToPlayer");
                    out.writeUTF(name);
                    out.writeUTF("§e§lYou have been invited from");
                    out.writeUTF("§b§l§n" + HSG.getSettings().getServerName());
                    out.writeUTF("/join " + HSG.getSettings().getServerName());
                    player.sendPluginMessage(HSG.getInstance(), "BungeeCord", out.toByteArray());
                }
            }
        }
        return true;
    }
}
