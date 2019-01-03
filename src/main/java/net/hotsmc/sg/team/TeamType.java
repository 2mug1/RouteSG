package net.hotsmc.sg.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hotsmc.core.other.Style;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum TeamType {
    RED(Style.WHITE + "[" + Style.RED + "R" + Style.WHITE + "] " + Style.RED,"" +  ChatColor.RED + ChatColor.BOLD + "RED"), AQUA(Style.WHITE + "[" + Style.AQUA + "A" + Style.WHITE + "] " + Style.AQUA, "" + ChatColor.AQUA + ChatColor.BOLD + "AQUA");

    private String prefix;
    private String displayName;
}
