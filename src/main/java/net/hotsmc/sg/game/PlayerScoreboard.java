package net.hotsmc.sg.game;

import lombok.Setter;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.Settings;
import net.hotsmc.sg.utility.DateUtility;
import net.hotsmc.sg.utility.TimeUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerScoreboard extends BukkitRunnable {

    private static final String WEB_URL = "" + ChatColor.AQUA + ChatColor.BOLD + "HotsMC.net";

    private Player player;
    private Scoreboard scoreboard;
    private Objective obj;
    @Setter
    private boolean minimize;

    public PlayerScoreboard(Player player, boolean minimize) {
        this.player = player;
        this.minimize = minimize;
    }

    private String checkNameLength(String name) {
        if (name.length() > 12) {
            return name.substring(0, 12);
        }
        return name;
    }

    private String getDisplayName(){
        GameTask gameTask = HSG.getGameTask();
        GameState state = gameTask.getState();
        String timeFormat = ChatColor.RED + TimeUtility.timeFormat(gameTask.getTime());
        return ChatColor.GREEN + state.name() + " " + timeFormat;
    }


    public void setup() {
        Settings settings = HSG.getSettings();
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        obj = scoreboard.registerNewObjective("hotssg", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(getDisplayName());

        if(!minimize) {
            Team rank = scoreboard.registerNewTeam("You");
            rank.addEntry(ChatColor.AQUA.toString());
            rank.setPrefix("" + ChatColor.GRAY + "» " + ChatColor.WHITE + "You");
            obj.getScore(ChatColor.AQUA.toString()).setScore(14);
            Team youName = scoreboard.registerNewTeam("YouName");
            youName.addEntry(ChatColor.BLACK.toString());
            youName.setPrefix(ChatColor.WHITE + checkNameLength(player.getName()));
            obj.getScore(ChatColor.BLACK.toString()).setScore(13);

            Team blank1 = scoreboard.registerNewTeam("Blank1");
            blank1.addEntry(ChatColor.BLUE.toString());
            blank1.setPrefix(" ");
            obj.getScore(ChatColor.BLUE.toString()).setScore(12);

            Team timeName = scoreboard.registerNewTeam("TimeName");
            timeName.addEntry(ChatColor.BOLD.toString());
            timeName.setPrefix("" + ChatColor.GRAY + "» " + ChatColor.WHITE + "Time");
            obj.getScore(ChatColor.BOLD.toString()).setScore(11);
            Team date = scoreboard.registerNewTeam("Date");
            date.addEntry(ChatColor.DARK_AQUA.toString());
            date.setPrefix(ChatColor.YELLOW + DateUtility.getDateAsJST());
            obj.getScore(ChatColor.DARK_AQUA.toString()).setScore(10);
            Team time = scoreboard.registerNewTeam("Time");
            time.addEntry(ChatColor.DARK_BLUE.toString());
            time.setPrefix(ChatColor.YELLOW + DateUtility.getTimeAsJST());
            obj.getScore(ChatColor.DARK_BLUE.toString()).setScore(9);

            Team blank4 = scoreboard.registerNewTeam("Blank4");
            blank4.addEntry(ChatColor.DARK_GRAY.toString());
            blank4.setPrefix(" ");
            obj.getScore(ChatColor.DARK_GRAY.toString()).setScore(8);

            Team server = scoreboard.registerNewTeam("Server");
            server.addEntry(ChatColor.DARK_GREEN.toString());
            server.setPrefix("" + ChatColor.GRAY + "» " + ChatColor.WHITE + "Server");
            obj.getScore(ChatColor.DARK_GREEN.toString()).setScore(7);
            Team serverName = scoreboard.registerNewTeam("ServerName");
            serverName.addEntry(ChatColor.DARK_RED.toString());
            serverName.setPrefix(ChatColor.GOLD + settings.getServerRegion() + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + settings.getServerName());
            obj.getScore(ChatColor.DARK_RED.toString()).setScore(6);

            Team blank2 = scoreboard.registerNewTeam("Blank2");
            blank2.addEntry(ChatColor.GOLD.toString());
            blank2.setPrefix(" ");
            obj.getScore(ChatColor.GOLD.toString()).setScore(5);

            Team players = scoreboard.registerNewTeam("Players");
            players.addEntry(ChatColor.GRAY.toString());
            players.setPrefix("" + ChatColor.GRAY + "» " + ChatColor.WHITE + "Players");
            obj.getScore(ChatColor.GRAY.toString()).setScore(4);

            Team playing = scoreboard.registerNewTeam("Playing");
            playing.addEntry(ChatColor.GREEN.toString());
            playing.setPrefix(ChatColor.WHITE + "Playing: ");
            playing.setSuffix("" + ChatColor.WHITE + HSG.getGameTask().countAlive());
            obj.getScore(ChatColor.GREEN.toString()).setScore(3);

            Team watching = scoreboard.registerNewTeam("Watching");
            watching.addEntry(ChatColor.ITALIC.toString());
            watching.setPrefix(ChatColor.WHITE + "Watching: ");
            watching.setSuffix("" + ChatColor.WHITE + HSG.getGameTask().countWatching());
            obj.getScore(ChatColor.ITALIC.toString()).setScore(2);

            Team ip = scoreboard.registerNewTeam("IP");
            ip.addEntry(ChatColor.LIGHT_PURPLE.toString());
            ip.setPrefix(WEB_URL);
            obj.getScore(ChatColor.LIGHT_PURPLE.toString()).setScore(1);
        }else{
            Team ip = scoreboard.registerNewTeam("IP");
            ip.addEntry(ChatColor.LIGHT_PURPLE.toString());
            ip.setPrefix(WEB_URL);
            obj.getScore(ChatColor.LIGHT_PURPLE.toString()).setScore(5);

            Team youName = scoreboard.registerNewTeam("YouName");
            youName.addEntry(ChatColor.BLACK.toString());
            youName.setPrefix(ChatColor.WHITE + player.getName());
            obj.getScore(ChatColor.BLACK.toString()).setScore(4);

            Team date = scoreboard.registerNewTeam("Date");
            date.addEntry(ChatColor.DARK_AQUA.toString());
            date.setPrefix(ChatColor.GRAY + DateUtility.getDateAsJST());
            obj.getScore(ChatColor.DARK_AQUA.toString()).setScore(3);

            Team time = scoreboard.registerNewTeam("Time");
            time.addEntry(ChatColor.DARK_BLUE.toString());
            time.setPrefix(ChatColor.GRAY + DateUtility.getTimeAsJST());
            obj.getScore(ChatColor.DARK_BLUE.toString()).setScore(2);

            Team serverName = scoreboard.registerNewTeam("ServerName");
            serverName.addEntry(ChatColor.DARK_RED.toString());
            serverName.setPrefix(ChatColor.GRAY + settings.getServerRegion() + ": " + settings.getServerName());
            obj.getScore(ChatColor.DARK_RED.toString()).setScore(1);
        }
    }

    private void update() {
        obj.setDisplayName(getDisplayName());
        if(!minimize){
            scoreboard.getTeam("Date").setPrefix(ChatColor.YELLOW + DateUtility.getDateAsJST());
            scoreboard.getTeam("Time").setPrefix(ChatColor.YELLOW + DateUtility.getTimeAsJST());
            scoreboard.getTeam("Playing").setSuffix("" + ChatColor.WHITE + HSG.getGameTask().countAlive());
            scoreboard.getTeam("Watching").setSuffix("" + ChatColor.WHITE + HSG.getGameTask().countWatching());
        }else{
            scoreboard.getTeam("Date").setPrefix(ChatColor.YELLOW + DateUtility.getDateAsJST());
            scoreboard.getTeam("Time").setPrefix(ChatColor.YELLOW + DateUtility.getTimeAsJST());
        }
        player.setScoreboard(scoreboard);
    }

    public void start(){
        this.runTaskTimer(HSG.getInstance(), 0, 5);
    }

    public void stop(){
        this.cancel();
    }

    @Override
    public void run() {
        if (player == null || !player.isOnline()) {
            this.cancel();
            return;
        }
        update();
    }
}