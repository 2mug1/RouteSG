package net.hotsmc.sg;

import net.hotsmc.core.HotsCore;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.scoreboard.Board;
import net.hotsmc.core.scoreboard.BoardAdapter;
import net.hotsmc.sg.database.PlayerData;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.game.GameState;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.game.VoteMap;
import net.hotsmc.sg.utility.DateUtility;
import net.hotsmc.sg.utility.TimeUtility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.List;

public class GameScoreboardAdapter implements BoardAdapter {

    private static final String SERVER_IP = Style.GRAY + "HotsMC.net";
    private static final String SERVER_IP_MCSG = Style.AQUA + Style.BOLD + "HotsMC.net";
    private static final String MAIN = Style.YELLOW;
    private static final String SUB = Style.WHITE;
    private static final String DISPLAY_NAME = Style.YELLOW + Style.BOLD + "Hots SG";

    @Override
    public String getTitle(Player player) {
        GameTask game = HSG.getGameTask();
        GamePlayer gamePlayer = game.getGamePlayer(player);
        if(gamePlayer == null)return "";
        if(!gamePlayer.getPlayerData().isSidebarMCSG()) {
            return DISPLAY_NAME;
        }
        GameState state = game.getState();
        String timeFormat = ChatColor.RED + TimeUtility.timeFormat(game.getTime());
        return ChatColor.GREEN + state.name() + " " + timeFormat;
    }

    @Override
    public List<String> getScoreboard(Player player, Board board) {
        GameTask game = HSG.getGameTask();
        GamePlayer gamePlayer = game.getGamePlayer(player);
        if(gamePlayer == null)return null;

        final List<String> toReturn = new ArrayList<>();

        Settings settings = HSG.getSettings();

        GameState state = game.getState();

        if(!gamePlayer.getPlayerData().isSidebarMCSG()) {
            toReturn.add(0, Style.SCOREBAORD_SEPARATOR);
            toReturn.add(1,MAIN + "State: " + SUB + state.name());
            toReturn.add(2,MAIN + "Time: " + SUB + TimeUtility.timeFormat(game.getTime()));
            toReturn.add(3,MAIN + "Server: " + SUB + settings.getServerName() + Style.GRAY + " (" + settings.getServerRegion() + ")");
            toReturn.add(4,MAIN + "Players: " + SUB + game.countAlive() + Style.GRAY + "/" + SUB + "24");
            if (state == GameState.Lobby) {
                if (game.isTimerFlag()) {
                    toReturn.add(Style.SCOREBAORD_SEPARATOR);
                    toReturn.add(MAIN + "Map Votes:");
                    for (VoteMap voteMap : game.getVoteManager().getVoteMaps()) {
                        toReturn.add(SUB + "- " + voteMap.getMapName() + ": " + voteMap.getVotes());
                    }
                }
            }
            if (state != GameState.Lobby) {
                toReturn.add(MAIN + "Watching: " + SUB + game.countWatching());
            }
            toReturn.add("");
            toReturn.add(SERVER_IP);
            toReturn.add(Style.SCOREBAORD_SEPARATOR);
        }else{
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
            toReturn.add(Style.WHITE + "Playing: " + game.countAlive());
            if(game.getState() != GameState.Lobby){
                toReturn.add(Style.WHITE + "Watching: " + game.countWatching());
            }
            toReturn.add(SERVER_IP_MCSG);
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
