package net.routemc.sg.menu;

import me.trollcoding.requires.gui.Button;
import me.trollcoding.requires.gui.Menu;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.team.GameTeam;
import net.routemc.sg.utility.ItemUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamListMenu extends Menu {

    public TeamListMenu() {
        super(true);
    }

    @Override
    public String getTitle(Player player) {
        return Style.BLUE + Style.BOLD + "Teams";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        for(int i = 0; i < RouteSG.getInstance().getTeamManager().getTeams().size(); i++){
            GameTeam team = RouteSG.getInstance().getTeamManager().getTeams().get(i);
            buttons.put(i, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    List<String> lore = new ArrayList<>();
                    for(String a : team.getPlayersAsStr()){
                        lore.add(Style.GRAY + "- " + Style.WHITE + a);
                    }
                    return ItemUtility.createItemStack(Style.YELLOW + "Team #" + team.getTeamID(), Material.PAPER, false, lore);
                }
            });
        }
        return buttons;
    }
}
