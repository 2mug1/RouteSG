package net.routemc.sg.command;

import net.routemc.core.RouteAPI;
import net.routemc.sg.RouteSG;
import net.routemc.sg.chest.TierType;
import net.routemc.sg.map.MapManager;
import net.routemc.sg.utility.ChatUtility;
import net.routemc.sg.utility.NumberUtility;
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
            //権限レベル確認
            if(RouteAPI.getRankOfPlayer(player).getWeight() < 5){
                ChatUtility.sendMessage(player, ChatColor.RED + "You don't have permission.");
                return true;
            }
            if (args.length == 0) {
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "RouteSG Setting commands");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/rsg setlobby");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/rsg create <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/rsg setdefaultspawn <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/rsg setcenter <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/rsg setmin <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/rsg setmax <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/rsg setspawn <number> <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/rsg setdmspawn <number> <map>");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/rsg addtier1");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/rsg addtier2");
                ChatUtility.sendMessage(player, ChatColor.YELLOW + "/rsg scanchest <map>");
                return true;
            }
            MapManager mapManager = RouteSG.getMapManager();
            if (args.length == 1) {
                if (args[0].equals("setlobby")) {
                    mapManager.updateLobbyLocation(player);
                    return true;
                }
                if (args[0].equals("addtier1")) {
                    RouteSG.getGameTask().getChestManager().addTierItem(TierType.TIER1, player);
                    return true;
                }
                if (args[0].equals("addtier2")) {
                    RouteSG.getGameTask().getChestManager().addTierItem(TierType.TIER2, player);
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
            if(args[0].equals("scanchest")){
                String mapName = args[1];
                mapManager.scanAddAllChest(mapName, player);
                return true;
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