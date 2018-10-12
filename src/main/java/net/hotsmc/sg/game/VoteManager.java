package net.hotsmc.sg.game;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.hotsmc.core.HotsCore;
import net.hotsmc.core.player.HotsPlayer;
import net.hotsmc.core.player.PlayerRank;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
@Getter
public class VoteManager {

    private List<VoteMap> voteMaps;

    @Setter
    private boolean voting;

    public VoteManager(){
        voteMaps = Lists.newArrayList();
    }

    /**
     * マップデータの中からVoteするマップを５つ取り出してVoteMapに追加する
     */
    public void selectRandomVoteMaps() {
        voteMaps.clear();
        Collections.shuffle(HSG.getMapManager().getLoadedMapData());
        for (int i = 1; i <= 5; i++) {
            voteMaps.add(new VoteMap(HSG.getMapManager().getLoadedMapData().get(i).getName(), i));
        }
    }

    public VoteMap getVoteMap(int id){
        for(VoteMap voteMap : voteMaps){
            if(voteMap.getVoteID() == id){
                return voteMap;
            }
        }
        return null;
    }

    public void send(Player player){
        ChatUtility.sendMessage(player,ChatColor.DARK_GREEN + "Vote using " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "/vote #" + ChatColor.DARK_GRAY + "].");
        for(VoteMap voteMap : voteMaps){
            ChatUtility.sendMessage(player,"" + ChatColor.GREEN + voteMap.getVoteID() + ChatColor.DARK_GRAY + " > ¦ " + ChatColor.YELLOW + voteMap.getVotes() + ChatColor.GRAY +
                    " Votes " + ChatColor.DARK_GRAY + " ¦ " + ChatColor.DARK_GREEN + voteMap.getMapName());
        }
    }


    public void broadcast(){
        ChatUtility.broadcast(ChatColor.DARK_GREEN + "Vote using " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "/vote #" + ChatColor.DARK_GRAY + "].");
        for(VoteMap voteMap : voteMaps){
            ChatUtility.broadcast("" + ChatColor.GREEN + voteMap.getVoteID() + ChatColor.DARK_GRAY + " > ¦ " + ChatColor.YELLOW + voteMap.getVotes() + ChatColor.GRAY +
                    " Votes " + ChatColor.DARK_GRAY + " ¦ " + ChatColor.DARK_GREEN + voteMap.getMapName());
        }
    }

    /**
     * 一票入れる
     */
    public void addVote(GamePlayer gamePlayer, int voteID){
        if(gamePlayer.isVoted()){
            ChatUtility.sendMessage(gamePlayer, ChatColor.RED + "You have already voted.");
            return;
        }
        if(voteID > 5) {
            ChatUtility.sendMessage(gamePlayer, ChatColor.RED + "Vote ID of maximum is 5.");
            return;
        }
        VoteMap voteMap = getVoteMap(voteID);
        HotsPlayer hotsPlayer = HotsCore.getHotsPlayer(gamePlayer.getPlayer());
        if(hotsPlayer == null)return;
        int votes = 1;
        if(hotsPlayer.getPlayerRank().getPermissionLevel() == PlayerRank.Owner.getPermissionLevel()) {
            votes = 6;
        }
        if(hotsPlayer.getPlayerRank().getPermissionLevel() == PlayerRank.Administrator.getPermissionLevel()) {
            votes = 6;
        }
        if(hotsPlayer.getPlayerRank().getPermissionLevel() == PlayerRank.Moderator.getPermissionLevel()) {
            votes = 5;
        }
        if(hotsPlayer.getPlayerRank().getPermissionLevel() == PlayerRank.VIP.getPermissionLevel()) {
            votes = 5;
        }
        if(hotsPlayer.getPlayerRank().getPermissionLevel() == PlayerRank.Emerald.getPermissionLevel()) {
            votes = 4;
        }
        if(hotsPlayer.getPlayerRank().getPermissionLevel() == PlayerRank.Diamond.getPermissionLevel()) {
            votes = 3;
        }
        if(hotsPlayer.getPlayerRank().getPermissionLevel() == PlayerRank.Gold.getPermissionLevel()) {
            votes = 2;
        }
        if(hotsPlayer.getPlayerRank().getPermissionLevel() == PlayerRank.Regular.getPermissionLevel()) {
            votes = 1;
        }
        voteMap.addVote(votes);
        gamePlayer.setVoted(true);
        ChatUtility.sendMessage(gamePlayer, ChatColor.GREEN + "You have voted " +  ChatColor.YELLOW + votes + ChatColor.GREEN +  " for " + ChatColor.YELLOW + getVoteMap(voteID).getMapName());
    }

    /**
     * 一番投票数の多い投票データからマップデータを返します
     * @return
     */
    public MapData getDecidedMapData(){
        voteMaps.sort(new VoteMapComparator());
        return HSG.getMapManager().getMapData(voteMaps.get(0).getMapName());
    }
}
