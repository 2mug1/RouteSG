package net.hotsmc.sg.command;

import net.hotsmc.core.HotsCore;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.player.PlayerRank;
import net.hotsmc.sg.HSG;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FRKBReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (HotsCore.getHotsPlayer(player).getPlayerRank().getPermissionLevel() < PlayerRank.Administrator.getPermissionLevel()) {
                player.sendMessage(Style.RED + "You don't have permission.");
                return true;
            }
            HSG.getSettings().reloadFRKB();
            player.sendMessage("Reloaded fishing rod kb.");
        }
        return true;
    }
}
