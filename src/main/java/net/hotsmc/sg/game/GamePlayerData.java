package net.hotsmc.sg.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.team.TeamType;

@Data
public class GamePlayerData {

    private String name;
    private TeamType teamType;
    private int kills;
    private boolean isAlive = true;
    private int placeRank = 0;

    public GamePlayerData(String name, TeamType teamType){
        this.name = name;
        this.teamType = teamType;
    }

    public void addKill(){
        kills += 1;
    }

    public void applyPlaceRank(){
        placeRank = HSG.getGameTask().getAliveSizeByGamePlayerData();
    }
}
