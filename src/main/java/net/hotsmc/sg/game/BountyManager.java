package net.hotsmc.sg.game;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class BountyManager {

    @Getter
    private List<BountyData> bounties;

    public BountyManager(){
        this.bounties = Lists.newArrayList();
    }

    public void clearBountyData(){
        this.bounties.clear();
    }

    /**
     * 自分が誰をBountyしたか
     * @param me
     * @return
     */
    public BountyData getBountyPlayerWasMe(GamePlayer me){
        for(BountyData bountyData : bounties){
            if(bountyData.getSender().equals(me)){
                return bountyData;
            }
        }
        return null;
    }

    /**
     * 自分はBountyしているのか
     * @param me
     * @return
     */
    public boolean isBountyMe(GamePlayer me){
        for(BountyData bountyData : bounties){
            if(bountyData.getSender().getPlayer().getName().equals(me.getPlayer().getName())){
                return true;
            }
        }
        return false;
    }

    /**
     * 自分がBountyされていたか
     * @param me
     * @return
     */
    public boolean wasBountiedMe(GamePlayer me){
        for(BountyData bountyData : bounties){
            if(bountyData.getTarget().getPlayer().getName().equals(me.getPlayer().getName())){
                return true;
            }
        }
        return false;
    }

    /**
     * 対象プレイヤーのBountyDataをListで返します
     * @param target
     * @return
     */
    public List<BountyData> getBounties(GamePlayer target){
        List<BountyData> bouties = Lists.newArrayList();
        for(BountyData bountyData : bounties){
            if(bountyData.getTarget().getPlayer().getName().equals(target.getPlayer().getName())){
                bouties.add(bountyData);
            }
        }
        return bouties;
    }

    public void addBounty(BountyData bountyData){
        bounties.add(bountyData);
    }
}
