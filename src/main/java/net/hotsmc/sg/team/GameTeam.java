package net.hotsmc.sg.team;

import ca.wacos.nametagedit.NametagAPI;
import lombok.Getter;
import net.hotsmc.core.HotsCore;
import net.hotsmc.core.player.HotsPlayer;
import net.hotsmc.sg.game.GamePlayer;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameTeam {

    private TeamType teamType;
    private String prefix;
    private List<GamePlayer> players;

    public GameTeam(TeamType teamType){
        this.teamType = teamType;
        this.prefix = teamType.getPrefix();
        this.players = new ArrayList<>();
    }

    public GamePlayer getTeamPlayer(GamePlayer gamePlayer){
        for(GamePlayer teamPlayer : players){
            if(teamPlayer.getUUID().equals(gamePlayer.getUUID())){
                return teamPlayer;
            }
        }
        return null;
    }

    public void addPlayer(GamePlayer teamPlayer){
        NametagAPI.setPrefix(teamPlayer.getName(), prefix);
        players.add(teamPlayer);
        teamPlayer.sendMessage(ChatColor.GRAY + "You have joined " + teamType.getDisplayName());
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


    public void removeAllPlayer() {
        for(GamePlayer teamPlayer : players){
            HotsPlayer hotsPlayer = HotsCore.getHotsPlayer(teamPlayer.getPlayer());
            if(hotsPlayer != null) {
                hotsPlayer.updateNameTag();
            }
        }
        players.clear();
    }
}
