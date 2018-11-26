package net.hotsmc.sg;

import net.hotsmc.core.other.Style;
import net.hotsmc.core.scoreboard.Board;
import net.hotsmc.core.scoreboard.BoardAdapter;
import net.hotsmc.sg.database.PlayerData;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.game.GameState;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.utility.DateUtility;
import net.hotsmc.sg.utility.TimeUtility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class GameScoreboardAdapter implements BoardAdapter {

    private static final String SERVER_IP = Style.AQUA + Style.BOLD + "HotsMC.net";

    @Override
    public String getTitle(Player player) {
        GameTask gameTask = HSG.getGameTask();
        GameState state = gameTask.getState();
        String timeFormat = ChatColor.RED + TimeUtility.timeFormat(gameTask.getTime());
        return ChatColor.GREEN + state.name() + " " + timeFormat;
    }

    @Override
    public List<String> getScoreboard(Player player, Board board) {
        GameTask gameTask = HSG.getGameTask();
        GamePlayer gamePlayer = gameTask.getGamePlayer(player);
        if(gamePlayer == null)return null;
        PlayerData data = gamePlayer.getPlayerData();

        final List<String> toReturn = new ArrayList<>();

        Settings settings = HSG.getSettings();

        if(!data.isSidebarMinimize()){
            toReturn.add("" + Style.GRAY + "» " + Style.WHITE + "You");
            toReturn.add("" + Style.WHITE + player.getName());
            toReturn.add("");
            toReturn.add("" + Style.GRAY + "» " + ChatColor.WHITE + "Time");
            toReturn.add(Style.YELLOW + DateUtility.getDateAsJST());
            toReturn.add(Style.YELLOW + DateUtility.getTimeAsJST());
            toReturn.add("");
            toReturn.add(Style.GRAY + "» " + ChatColor.WHITE + "Server");
            toReturn.add(Style.GOLD + settings.getServerRegion() + Style.DARK_GRAY + ": " + Style.YELLOW + settings.getServerName());
            toReturn.add("");
            toReturn.add(Style.GRAY + "» " + ChatColor.WHITE + "Players");
            toReturn.add(Style.WHITE + "Playing: " + gameTask.countAlive());
            if(gameTask.getState() != GameState.Lobby){
                toReturn.add(Style.WHITE + "Watching: " + gameTask.countWatching());
            }
            toReturn.add(SERVER_IP);
        }else{
            toReturn.add(SERVER_IP);
            toReturn.add(Style.WHITE + player.getName());
            toReturn.add(Style.GRAY + DateUtility.getDateAsJST());
            toReturn.add(Style.GRAY + DateUtility.getTimeAsJST());
            toReturn.add(Style.GRAY + settings.getServerRegion() + ": " + settings.getServerName());
        }
        return toReturn;
    }

    @Override
    public long getInterval() {
        return 2L;
    }

    @Override
    public void onScoreboardCreate(Player player, Scoreboard scoreboard) {

    }

    @Override
    public void preLoop() {

    }
}
