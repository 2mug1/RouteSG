package net.hotsmc.sg.map;

import lombok.Getter;
import net.hotsmc.sg.HSG;

import java.math.BigDecimal;

@Getter
public class VoteMap {
    private String mapName;
    private int voteID;
    private int votes = 0;

    public VoteMap(String mapName, int voteID) {
        this.mapName = mapName;
        this.voteID = voteID;
    }

    public void addVote(int amount) {
        votes += amount;
    }
}