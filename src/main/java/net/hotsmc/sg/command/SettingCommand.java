package net.hotsmc.sg.command;

import net.hotsmc.core.HotsCore;
import net.hotsmc.core.player.HotsPlayer;
import net.hotsmc.core.player.PlayerRank;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.TierType;
import net.hotsmc.sg.game.MapManager;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.NumberUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            ChatUtility.sendMessage(commandSender, "You're not player");
        } else {
            Player player = (Player) commandSender;
            HotsPlayer hotsPlayer = HotsCore.getHotsPlayer(player);
            //権限レベル確認
            if (PlayerRank.Administrator.getPermissionLevel() > hotsPlayer.getPlayerRank().getPermissionLevel()) {
                ChatUtility.sendMessage(player, ChatColor.RED + "You don't have permission.");
                return true;
            }
            if (args.length == 0) {
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "HotsSG Setting commands");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/hsg setlobby");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/hsg create <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/hsg setdefaultspawn <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/hsg setcenter <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/hsg setmin <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/hsg setmax <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/hsg setspawn <number> <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/hsg setdmspawn <number> <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/hsg addtier1");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/hsg addtier2");
                return true;
            }
            MapManager mapManager = HSG.getMapManager();
            if (args.length == 1) {
                if (args[0].equals("setlobby")) {
                    mapManager.updateLobbyLocation(player);
                    return true;
                }
                if (args[0].equals("addtier1")) {
                    HSG.getGameTask().getChestManager().addTierItem(TierType.TIER1, player);
                    return true;
                }
                if (args[0].equals("addtier2")) {
                    HSG.getGameTask().getChestManager().addTierItem(TierType.TIER2, player);
                    return true;
                }
            }
            if (args.length == 2) {
                if (args[0].equals("create")) {
                    String mapName = args[1];
                    mapManager.createMapData(mapName, player);
                    return true;
                }
                if (args[0].equals("setdefaultspawn")) {
                    String mapName = args[1];
                    mapManager.updateDefaultSpawn(mapName, player);
                    return true;
                }
                if (args[0].equals("setcenter")) {
                    String mapName = args[1];
                    mapManager.updateCenterLocation(mapName, player);
                    return true;
                }
                if (args[0].equals("setmin")) {
                    String mapName = args[1];
                    mapManager.updateMinLocation(mapName, player);
                    return true;
                }
                if (args[0].equals("setmax")) {
                    String mapName = args[1];
                    mapManager.updateMaxLocation(mapName, player);
                    return true;
                }
            }
            if (args.length == 3) {
                if (args[0].equals("setspawn")) {
                    if (!NumberUtility.isNumber(args[1])) {
                        ChatUtility.sendMessage(player, ChatColor.RED + "Please enter with a integer.");
                    } else {
                        int number = Integer.parseInt(args[1]);
                        String mapName = args[2];
                        mapManager.updateSpawn(mapName, player, number);
                    }
                    return true;
                }
                if (args[0].equals("setdmspawn")) {
                    if (!NumberUtility.isNumber(args[1])) {
                        ChatUtility.sendMessage(player, ChatColor.RED + "Please enter with a integer.");
                    } else {
                        int number = Integer.parseInt(args[1]);
                        String mapName = args[2];
                        mapManager.updateDeathmatchSpawn(mapName, player, number);
                    }
                    return true;
                }
            }
        }
        return true;
    }
}