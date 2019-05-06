package net.routemc.sg.player;

import lombok.Data;
import net.routemc.sg.RouteSG;
import net.routemc.sg.team.GameTeam;

@Data
public class GamePlayerData {

    private String name;
    private GameTeam team;
    private int kills;
    private boolean isAlive = true;
    private int placeRank = 0;

    public GamePlayerData(String name, GameTeam team){
        this.name = name;
        this.team = team;
    }

    public void addKill(){
        kills += 1;
    }

    public void applyPlaceRank(){
        placeRank = RouteSG.getGameTask().getAliveSizeByGamePlayerData();
    }
}
