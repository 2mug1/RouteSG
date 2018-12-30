package net.hotsmc.sg.command;

import net.hotsmc.core.HotsCore;
import net.hotsmc.core.player.HotsPlayer;
import net.hotsmc.core.utility.PlayerDataUtility;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.database.PlayerData;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.utility.*;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            long total = HSG.getMongoConnection().getPlayers().countDocuments();
            if (args.length == 0) {
                GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return true;
                PlayerData playerData = gamePlayer.getPlayerData();
                if (playerData == null) return true;
                HotsPlayer hotsPlayer = HotsCore.getHotsPlayer(player);
                if (hotsPlayer == null) return true;
                ChatUtility.sendMessage(player, hotsPlayer.getColorName() + ChatColor.WHITE + "'s Records");
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Rank" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + hotsPlayer.getPlayerRank().name());
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
                Document document = HSG.getMongoConnection().getPlayers().find(MongoUtility.find("NAME", targetName)).first();
                //来たことがあるか調べる
                if (document == null) {
                    ChatUtility.sendMessage(commandSender, ChatColor.RED + "That player has never joined the game");
                    return true;
                }
                PlayerData playerData = new PlayerData(NameUtility.getUUIDByName(targetName));
                playerData.setName(targetName);
                playerData.loadData();
                ChatUtility.sendMessage(player, PlayerDataUtility.getColorName(targetName) + ChatColor.WHITE + "'s Records");
                ChatUtility.sendMessage(player, ChatColor.WHITE + "Rank" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + PlayerDataUtility.getRank(targetName));
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
