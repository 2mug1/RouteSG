package net.routemc.sg.map;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.routemc.core.RouteAPI;
import net.routemc.sg.RouteSG;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.utility.ChatUtility;
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
     * マップデータの中からVoteするマップを５つランダムに取り出してVoteMapに追加する
     */
    public void selectRandomVoteMaps() {
        voteMaps.clear();
        Collections.shuffle(RouteSG.getMapManager().getLoadedMapData());
        for (int i = 1; i <= 5; i++) {
            voteMaps.add(new VoteMap(RouteSG.getMapManager().getLoadedMapData().get(i).getName(), i));
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
            ChatUtility.sendMessage(player,"" + ChatColor.GREEN + voteMap.getVoteID() + ChatColor.DARK_GRAY + " > | " + ChatColor.YELLOW + voteMap.getVotes() + ChatColor.GRAY +
                    " Votes " + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + voteMap.getMapName());
        }
    }


    public void broadcast(){
        ChatUtility.broadcast(ChatColor.DARK_GREEN + "Vote using " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "/vote #" + ChatColor.DARK_GRAY + "].");
        for(VoteMap voteMap : voteMaps){
            ChatUtility.broadcast("" + ChatColor.GREEN + voteMap.getVoteID() + ChatColor.DARK_GRAY + " > | " + ChatColor.YELLOW + voteMap.getVotes() + ChatColor.GRAY +
                    " Votes " + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + voteMap.getMapName());
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
        int votes = 1;
        if(RouteAPI.getRankOfPlayer(gamePlayer.getPlayer()).getWeight() == 5) {
            votes = 6;
        }
        if(RouteAPI.getRankOfPlayer(gamePlayer.getPlayer()).getWeight() == 4) {
            votes = 5;
        }
        if(RouteAPI.getRankOfPlayer(gamePlayer.getPlayer()).getWeight() == 3) {
            votes = 4;
        }
        if(RouteAPI.getRankOfPlayer(gamePlayer.getPlayer()).getWeight() == 2) {
            votes = 3;
        }
        if(RouteAPI.getRankOfPlayer(gamePlayer.getPlayer()).getWeight() == 1) {
            votes = 2;
        }
        if(RouteAPI.getRankOfPlayer(gamePlayer.getPlayer()).getWeight() == 0) {
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
        return RouteSG.getMapManager().getMapData(voteMaps.get(0).getMapName());
    }
}
