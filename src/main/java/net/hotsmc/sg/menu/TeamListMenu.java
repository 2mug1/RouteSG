package net.hotsmc.sg.menu;

import net.hotsmc.core.HotsCore;
import net.hotsmc.core.gui.menu.Button;
import net.hotsmc.core.gui.menu.Menu;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.utility.ItemUtility;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.team.GameTeam;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

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
        for(int i = 0; i < HSG.getInstance().getTeamManager().getTeams().size(); i++){
            GameTeam team = HSG.getInstance().getTeamManager().getTeams().get(i);
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
