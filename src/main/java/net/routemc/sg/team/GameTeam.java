package net.routemc.sg.team;

import ca.wacos.nametagedit.NametagAPI;
import lombok.Getter;
import lombok.Setter;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.core.RouteAPI;
import net.routemc.core.profile.Profile;
import net.routemc.sg.RouteSG;
import net.routemc.sg.player.GamePlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.*;

@Getter
@Setter
public class GameTeam {

    private static final String TEAM_MESSAGE_PREFIX = Style.YELLOW + Style.BOLD + "Team" + Style.DARK_GRAY + " » ";
    private static final Hashtable<Integer, String> prefixes = new Hashtable<>();

    static {
        prefixes.put(1, Style.AQUA);
        prefixes.put(2, Style.RED);
        prefixes.put(3, Style.YELLOW);
        prefixes.put(4, Style.GREEN);
        prefixes.put(5, Style.PINK);
        prefixes.put(6, Style.GOLD);
        prefixes.put(7, Style.BLUE);
        prefixes.put(8, Style.DARK_GREEN);
        prefixes.put(9, Style.DARK_AQUA);
        prefixes.put(10, Style.DARK_PURPLE);
        prefixes.put(11, Style.DARK_RED);
        prefixes.put(12, Style.GRAY);
    }

    private final int teamID;
    private final List<GamePlayer> players;
    private final List<String> invitePlayers;
    private String leaderName;

    public GameTeam(int teamID, GamePlayer leader){
        this.teamID = teamID;
        this.players = new LinkedList<>();
        this.invitePlayers = new LinkedList<>();
        this.leaderName = leader.getName();
        NametagAPI.setPrefix(leader.getName(), getPrefix());
        players.add(leader);
        leader.sendMessage(TEAM_MESSAGE_PREFIX + Style.YELLOW + "You have created team #" + teamID);
    }

    public String getPrefix(){
        return prefixes.get(teamID) + Style.BOLD + teamID + " " + Style.RESET + prefixes.get(teamID);
    }

    public GamePlayer getTeamPlayer(GamePlayer gamePlayer){
        for(GamePlayer teamPlayer : players){
            if(teamPlayer.getUUID().equals(gamePlayer.getUUID())){
                return teamPlayer;
            }
        }
        return null;
    }

    public boolean isLeader(GamePlayer gamePlayer){
        return leaderName.equals(gamePlayer.getName());
    }

    public void addPlayer(GamePlayer teamPlayer) {
        if (teamPlayer.isInTeam()) {
            teamPlayer.sendMessage(Style.RED + "You've already been in team");
        } else if (!invitePlayers.contains(teamPlayer.getName())) {
            teamPlayer.sendMessage(ChatColor.RED + "You were not invited by " + leaderName);
        } else {
            invitePlayers.remove(teamPlayer.getPlayer().getName());
            NametagAPI.setPrefix(teamPlayer.getName(), getPrefix());
            players.add(teamPlayer);
            broadcast(RouteAPI.getColoredName(teamPlayer.getPlayer()) + Style.GRAY + " has joined to #" + teamID);
        }
    }

    public void removePlayer(GamePlayer teamPlayer) {
        Profile profile = RouteAPI.getProfileByUUID(teamPlayer.getPlayer().getUniqueId());
        NametagAPI.setPrefix(teamPlayer.getName(), (profile.isInClan() ? profile.getClan().getStyleTag() : "") + Style.RESET + profile.getActiveRank().getColor());
        broadcast(teamPlayer.getName() + Style.GRAY + " has left");
        players.remove(teamPlayer);
        if (players.size() <= 0) {
            RouteSG.getInstance().getTeamManager().removeTeam(this);
        } else if (isLeader(teamPlayer)) {
            GamePlayer newLeader = players.get(0);
            setLeaderName(leaderName);
            broadcast(ChatColor.YELLOW + "Team leader has been changed to " + newLeader.getPlayer().getName());
        }
    }

    public List<GamePlayer> getAlivePlayers(){
        List<GamePlayer> alive = new ArrayList<>();
        for(GamePlayer teamPlayer : players){
            if(teamPlayer.isAlive()){
                alive.add(teamPlayer);
            }
        }
        return alive;
    }

    public List<GamePlayer> getDeadPlayers(){
        List<GamePlayer> dead = new ArrayList<>();
        for(GamePlayer teamPlayer : players){
            if(!teamPlayer.isAlive()){
                dead.add(teamPlayer);
            }
        }
        return dead;
    }

    public List<String> getPlayersAsStr(){
        List<String> name = new ArrayList<>();
        for(GamePlayer gamePlayer : getPlayers()){
            name.add(gamePlayer.getName());
        }
        return name;
    }



    public void removeAllPlayer() {
        for(GamePlayer teamPlayer : players){
            NametagAPI.setPrefix(teamPlayer.getName(), RouteAPI.getRankOfPlayer(teamPlayer.getPlayer()).getColor());
        }
        players.clear();
    }

    public void broadcast(String message){
        for(GamePlayer teamPlayer : players){
            teamPlayer.sendMessage(TEAM_MESSAGE_PREFIX  + message);
        }
    }

    public boolean isExists(GamePlayer gamePlayer){
        return getTeamPlayer(gamePlayer) != null;
    }

    public void disband() {
        invitePlayers.clear();
        broadcast(ChatColor.RED + "Team #" + teamID + " has been removed!");
        removeAllPlayer();
        RouteSG.getInstance().getTeamManager().removeTeam(this);
    }

    public void invite(GamePlayer sender, GamePlayer target){
        if(!invitePlayers.contains(target.getName())){
            invitePlayers.add(target.getName());
        }
        sender.sendMessage(TEAM_MESSAGE_PREFIX + ChatColor.YELLOW + "You sent invitation to " + target.getName());
        target.sendMessage(Style.HORIZONTAL_SEPARATOR);
        ComponentBuilder msg = new ComponentBuilder(TEAM_MESSAGE_PREFIX);
        msg.append(ChatColor.GREEN + "Invited from " + leaderName + " ");
        msg.append("" + ChatColor.GRAY + "(Click to Join)");
        msg.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/team accept " + leaderName));
        msg.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/team accept " + leaderName).create()));
        target.getPlayer().spigot().sendMessage(msg.create());
        target.sendMessage(Style.HORIZONTAL_SEPARATOR);
        target.getPlayer().playSound(target.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5F, 1);
    }

    public void kick(GamePlayer sender, GamePlayer targetGP) {
        if(!isExists(targetGP)){
            sender.sendMessage(targetGP.getName() + " isn't in team");
        }else{
            removePlayer(targetGP);
        }
    }

    public void chat(GamePlayer sender, String msg) {
        for(GamePlayer gamePlayer : players){
            gamePlayer.sendMessage(TEAM_MESSAGE_PREFIX + sender.getSGName() + Style.D_GRAY + ": " + Style.RESET + ChatColor.translateAlternateColorCodes('&', msg));
        }
    }
}
