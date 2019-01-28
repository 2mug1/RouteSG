package net.hotsmc.sg.preset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.utility.TimeUtility;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameTask;
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
        final GameTask game = HSG.getGameTask();
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
    }
}
