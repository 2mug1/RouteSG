package net.hotsmc.sg.command;

import net.hotsmc.core.HotsCore;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.player.PlayerRank;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.hotbar.PlayerHotbar;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.ItemUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveHostCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            final GameTask game = HSG.getGameTask();
            if (!game.getGameConfig().isCustomSG()) {
                player.sendMessage(Style.RED + "This server isn't custom sg.");
                return true;
            }

            if(args.length == 1) {
                if (game.getGameConfig().isCustomSG()) {
                    if (game.getHost() != player.getUniqueId()) {
                        player.sendMessage(Style.RED + "You are not hosting the game.");
                        return true;
                    }
                    String name = args[0];
                    if(name.equalsIgnoreCase(player.getName())){
                        player.sendMessage(Style.RED + "Can't yourself.");
                        return true;
                    }
                    Player target = Bukkit.getPlayer(name);
                    if(target == null || !target.isOnline()){
                        ChatUtility.sendMessage(player, Style.RED + name + " is offline.");
                        return true;
                    }
                    GamePlayer old = HSG.getGameTask().getGamePlayer(player);
                    old.disableWatching();
                    old.setHotbar(PlayerHotbar.LOBBY);
                    player.getInventory().setItem(0, ItemUtility.createItemStack(ChatColor.AQUA + "Lobby Sword", Material.STONE_SWORD, true));
                    player.getInventory().setItem(1, ItemUtility.createItemStack(ChatColor.AQUA + "Lobby Rod", Material.FISHING_ROD, true));
                    game.updateHost(target);
                    ChatUtility.sendMessage(player, Style.YELLOW + "You've given host to " + Style.AQUA + name + "!");
                    target.sendMessage(Style.YELLOW + "Add to roster: /register <player1> <player2> <player3>...");
                    target.sendMessage(Style.YELLOW + "Remove from roster: /unregister <player>");
                    target.sendMessage(Style.YELLOW + "Change Host: /givehost <player>");
                    target.sendMessage(Style.YELLOW + "Show Roster: /roster");
                    target.sendMessage(Style.YELLOW + "Add Observer: /observer <player1> <player2> <player3>...");
                    player.sendMessage(Style.YELLOW + "Add to team: /team <team> <player1> <player2> <player3>...");
                    player.sendMessage(Style.YELLOW + "Teams: " + Style.AQUA + "AQUA" + Style.GRAY + ", " + Style.RED + "RED");
                }
            }
        }
        return true;
    }
}
