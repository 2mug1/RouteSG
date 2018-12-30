package net.hotsmc.sg.command;

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

public class UnregisterPlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (HotsCore.getHotsPlayer(player).getPlayerRank().getPermissionLevel() < PlayerRank.Emerald.getPermissionLevel()) {
                player.sendMessage(Style.RED + "You don't have permission.");
                return true;
            }
            final GameTask game = HSG.getGameTask();
            if (game.getGameConfig().isCustomSG()) {
                if (!player.getUniqueId().equals(game.getHost())) {
                    player.sendMessage(Style.RED + "You are not host.");
                    return true;
                }
            }

            if(args.length == 1) {
                if (!game.getGameConfig().isCustomSG()) {
                    ChatUtility.sendMessage(player, Style.RED + "This server isn't custom sg.");
                    return true;
                }
                final String name = args[0];
                if (!HSG.getInstance().getWhitelistedPlayers().contains(name.toLowerCase())) {
                    ChatUtility.sendMessage(player, Style.RED + name + " isn't registered.");
                    return true;
                }
                HSG.getInstance().getWhitelistedPlayers().remove(name.toLowerCase());
                ChatUtility.sendMessage(player, Style.YELLOW + name + Style.AQUA + " has been removed from whitelist.");
            }else{
                ChatUtility.sendMessage(player,Style.RED + "/unregister <player>");
            }
        }
        return true;
    }
}
