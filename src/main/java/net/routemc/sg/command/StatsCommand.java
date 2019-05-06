package net.routemc.sg.command;

import net.hotsmc.bot.util.MojangUtils;
import net.routemc.core.RouteAPI;
import net.routemc.sg.RouteSG;
import net.routemc.sg.database.PlayerData;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.utility.ChatUtility;
import net.routemc.sg.utility.DateUtility;
import net.routemc.sg.utility.MongoUtility;
import net.routemc.sg.utility.NameUtility;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            long total = RouteSG.getMongoConnection().getPlayers().countDocuments();
            if (args.length == 0) {
                GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return true;
                PlayerData playerData = gamePlayer.getPlayerData();
                if (playerData == null) return true;
                ChatUtility.sendMessage(player, RouteAPI.getColoredName(player) + ChatColor.WHITE + "'s Records");
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Rank" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + RouteAPI.getColoredName(player));
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getPoint());
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Games Won Ranking" + ChatColor.DARK_GRAY + ": #" + ChatColor.YELLOW + playerData.getWonRank() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + total);
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Kill Ranking " + ChatColor.DARK_GRAY + ": #" + ChatColor.YELLOW + playerData.getKillRank() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + total);
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Point Ranking" + ChatColor.DARK_GRAY + ": #" + ChatColor.YELLOW + playerData.getPointRank() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + total);
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Games (Won/Total)" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getWin() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + playerData.getPlayed());
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Top3 (Placed/Total)" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getTop3() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + playerData.getPlayed());
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Kills" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getKill());
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Chests" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getChests());
                ChatUtility.sendMessage(player, ChatColor.WHITE + "First Played" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + DateUtility.getDateFormatByTimestamp(playerData.getFirstPlayed()));
                return true;
            }
            if (args.length == 1) {
                String targetName = args[0];
                Document document = RouteSG.getMongoConnection().getPlayers().find(MongoUtility.find("NAME", targetName)).first();
                //来たことがあるか調べる
                if (document == null) {
                    ChatUtility.sendMessage(commandSender, ChatColor.RED + "That player has never joined the game");
                    return true;
                }
                PlayerData playerData = new PlayerData(NameUtility.getUUIDByName(targetName));
                playerData.setName(targetName);
                playerData.loadData();
                ChatUtility.sendMessage(player, "" + RouteAPI.getRankOfPlayerByUUID(UUID.fromString(MojangUtils.INSTANCE.getUuid(targetName))).getColor() + targetName + ChatColor.WHITE + "'s Records");
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getPoint());
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Games Won Ranking" + ChatColor.DARK_GRAY + ": #" + ChatColor.YELLOW + playerData.getWonRank() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + total);
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Kill Ranking" + ChatColor.DARK_GRAY + ": #" + ChatColor.YELLOW + playerData.getKillRank() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + total);
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Point Ranking" + ChatColor.DARK_GRAY + ": #" + ChatColor.YELLOW + playerData.getPointRank() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + total);
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Games (Won/Total)" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getWin() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + playerData.getPlayed());
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Top3 (Placed/Total)" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getTop3() + ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + playerData.getPlayed());
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Kills" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getKill());
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Chests" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + playerData.getChests());
                ChatUtility.sendMessage(player, ChatColor.WHITE + "First Played" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + DateUtility.getDateFormatByTimestamp(playerData.getFirstPlayed()));
            } else {
                ChatUtility.sendMessage(player, ChatColor.RED + "/stats <player>");
            }
        }
        return true;
    }
}
