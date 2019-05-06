package net.routemc.sg.team;

import lombok.Getter;
import net.routemc.sg.player.GamePlayer;

import java.util.LinkedList;
import java.util.List;

@Getter
public class TeamManager {

    private List<GameTeam> teams = new LinkedList<>();

    public boolean canCreate(){
        return teams.size() < 12;
    }

    public GameTeam createTeam(GamePlayer leader) {
        return new GameTeam(teams.size() + 1, leader);
    }

    public void addTeam(GameTeam team) {
        teams.add(team);
    }

    public void removeTeam(GameTeam team) {
        teams.remove(team);
    }

    public GameTeam getTeamById(int teamID) {
        for (GameTeam team : teams) {
            if (team.getTeamID() == teamID) {
                return team;
            }
        }
        return null;
    }

    public GameTeam getTeamByPlayer(GamePlayer gamePlayer) {
        for (GameTeam team : teams) {
            for (GamePlayer player : team.getPlayers()) {
                if (player.getUUID() == player.getUUID()) {
                    return team;
                }
            }
        }
        return null;
    }

    public GameTeam getTeamByLeader(String leader) {
        for (GameTeam team : teams) {
            if (leader.equalsIgnoreCase(team.getLeaderName())) {
                return team;
            }
        }
        return null;
    }
}
