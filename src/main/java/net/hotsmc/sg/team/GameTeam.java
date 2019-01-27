package net.hotsmc.sg.team;

import ca.wacos.nametagedit.NametagAPI;
import lombok.Getter;
import lombok.Setter;
import net.hotsmc.core.HotsCore;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.player.HotsPlayer;
import net.hotsmc.core.utility.PlayerDataUtility;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.player.GamePlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class GameTeam {

    private static final String TEAM_MESSAGE_PREFIX = Style.YELLOW + Style.BOLD + "Team" + Style.DARK_GRAY + " » ";

    private int teamID;
    private List<GamePlayer> players;
    private String leaderName;
    private List<String> invitePlayers;

    public GameTeam(int teamID, GamePlayer leader){
        this.teamID = teamID;
        this.players = new LinkedList<>();
        this.invitePlayers = new LinkedList<>();
        this.leaderName = leader.getName();
        NametagAPI.setPrefix(leader.getName(), getPrefix() + leader.getHotsPlayer().getPlayerRank().getPrefix());
        players.add(leader);
        leader.sendMessage(TEAM_MESSAGE_PREFIX + Style.YELLOW + "You have created team #" + teamID);
    }

    public String getPrefix(){
        return Style.WHITE + "[" + teamID + "] " + Style.GRAY;
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
            return;
        }
        if (!invitePlayers.contains(teamPlayer.getName())) {
            teamPlayer.sendMessage(ChatColor.RED + "You were not invited by " + leaderName);
        } else {
            invitePlayers.remove(teamPlayer.getPlayer().getName());
            NametagAPI.setPrefix(teamPlayer.getName(), getPrefix() + teamPlayer.getHotsPlayer().getPlayerRank().getPrefix());
            players.add(teamPlayer);
            broadcast(teamPlayer.getHotsPlayer().getColorName() + Style.GRAY + " has joined to #" + teamID);
        }
    }

    public void removePlayer(GamePlayer teamPlayer) {
        HotsPlayer hotsPlayer = HotsCore.getHotsPlayer(teamPlayer.getPlayer());
        if (hotsPlayer != null) {
            hotsPlayer.updateNameTag();
        }
        broadcast(PlayerDataUtility.getColorName(teamPlayer.getName()) + Style.GRAY + " has left");
        players.remove(teamPlayer);
        if (players.size() <= 0) {
            HSG.getInstance().getTeamManager().removeTeam(this);
            return;
        }
        if (isLeader(teamPlayer)) {
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
            HotsPlayer hotsPlayer = HotsCore.getHotsPlayer(teamPlayer.getPlayer());
            if(hotsPlayer != null) {
                hotsPlayer.updateNameTag();
            }
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
    }

    public void invite(GamePlayer sender, GamePlayer target){
        if(!invitePlayers.contains(target.getName())){
            invitePlayers.add(target.getName());
        }
        sender.sendMessage(TEAM_MESSAGE_PREFIX + ChatColor.YELLOW + "You sent invitation to " + target.getName());
        target.sendMessage(Style.HORIZONTAL_SEPARATOR);
        ComponentBuilder msg = new ComponentBuilder(TEAM_MESSAGE_PREFIX);
        msg.append(ChatColor.GREEN + "Invited from " + leaderName + " ");
        msg.append(""+ ChatColor.GRAY + "(Click to Join)");
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
