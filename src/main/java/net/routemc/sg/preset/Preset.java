package net.routemc.sg.preset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.sg.RouteSG;
import net.routemc.sg.game.GameTask;
import net.routemc.sg.reflection.BukkitReflection;
import net.routemc.sg.utility.ChatUtility;
import net.routemc.sg.utility.TimeUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class Preset {

    private final String presetName;
    private final boolean hostWithPlay;
    private final boolean sponsor;
    private final boolean spectateRosterOnly;
    private final int gracePeriodSec;

    public void apply(Player player){
        final GameTask game = RouteSG.getGameTask();
        player.sendMessage(Style.HORIZONTAL_SEPARATOR);
        player.sendMessage(Style.DARK_AQUA + " Loaded Preset" + Style.DARK_GRAY + ": " + Style.AQUA + Style.BOLD + presetName);
        player.sendMessage("");
        player.sendMessage(Style.YELLOW + " Host With Play" + Style.GRAY + ": " + (hostWithPlay ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
        player.sendMessage(Style.YELLOW + " Sponsor" + Style.GRAY + ": " + (sponsor ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
        player.sendMessage(Style.YELLOW + " Spectate Roster Only" + Style.GRAY + ": " + (spectateRosterOnly ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
        player.sendMessage(Style.YELLOW + " Grace Period" + Style.GRAY + ": " + Style.GREEN + TimeUtility.timeFormat(gracePeriodSec));
        player.sendMessage("");
        player.sendMessage(Style.HORIZONTAL_SEPARATOR);
        game.setHostWithPlay(hostWithPlay);
        game.setSponsor(sponsor);
        game.setSpectateRosterOnly(spectateRosterOnly);
        game.setGracePeriodTimeSec(gracePeriodSec);

        if(hostWithPlay){
            if (Bukkit.getServer().getOnlinePlayers().size() >= 25) {
                ChatUtility.sendMessage(player, Style.RED + "Server is fully... Can't be changed " + Style.LIGHT_PURPLE + Style.BOLD + "Host with play");
                return;
            }
            RouteSG.getGameTask().getGamePlayer(player).disableWatching();
        }else{
            RouteSG.getGameTask().getGamePlayer(player).enableWatching();
            BukkitReflection.setMaxPlayers(RouteSG.getInstance().getServer(), 25);
        }
    }
}
