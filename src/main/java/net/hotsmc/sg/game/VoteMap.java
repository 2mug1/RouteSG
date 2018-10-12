package net.hotsmc.sg.game;

import lombok.Getter;
import net.hotsmc.sg.HSG;

import java.math.BigDecimal;

@Getter
public class VoteMap {
    private String mapName;
    private int voteID;
    private int votes = 0;

    public VoteMap(String mapName, int voteID){
        this.mapName = mapName;
        this.voteID = voteID;
    }

    public void addVote(int amount){
        votes+=amount;
    }

    /**
     * 確率を計算して返します
     * @return
     */
    public double getCalculatedChance(){
        int totalVotes = 0;
        for(VoteMap voteMap : HSG.getGameTask().getVoteManager().getVoteMaps()){
            totalVotes = totalVotes + voteMap.getVotes();
        }
        double c = (votes / totalVotes) * 100;
        BigDecimal c2 = new BigDecimal(c).setScale(2, BigDecimal.ROUND_DOWN);
        return c2.doubleValue();
    }
}
