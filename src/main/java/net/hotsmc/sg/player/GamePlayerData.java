package net.hotsmc.sg.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.team.GameTeam;
import net.hotsmc.sg.team.TeamType;

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
        placeRank = HSG.getGameTask().getAliveSizeByGamePlayerData();
    }
}
