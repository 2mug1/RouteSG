package net.routemc.sg.menu;

import me.trollcoding.requires.gui.Button;
import me.trollcoding.requires.gui.Menu;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.map.VoteMap;
import net.routemc.sg.utility.ItemUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class VoteMenu extends Menu {

    public VoteMenu() {
        super(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Vote a map";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        if(RouteSG.getGameTask().getVoteManager().isVoting()){
            for(int i = 0; i < RouteSG.getGameTask().getVoteManager().getVoteMaps().size(); i++){
                VoteMap voteMap = RouteSG.getGameTask().getVoteManager().getVoteMaps().get(i);
                buttons.put(i, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return ItemUtility.createItemStack(Style.AQUA + voteMap.getVoteID() + ". " + voteMap.getMapName() + ": " + Style.WHITE + voteMap.getVotes(), Material.EMPTY_MAP, false);
                    }
                    @Override
                    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                        player.closeInventory();
                        GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
                        if(gamePlayer == null)return;
                        RouteSG.getGameTask().getVoteManager().addVote(gamePlayer, voteMap.getVoteID());
                    }
                });
            }
        }
        return buttons;
    }
}
