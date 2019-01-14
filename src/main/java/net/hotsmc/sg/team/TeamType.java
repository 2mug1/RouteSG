package net.hotsmc.sg.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hotsmc.core.other.Style;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum TeamType {
    RED(
            ChatColor.WHITE + "[" + ChatColor.RED + "R" + ChatColor.WHITE + "] " + ChatColor.RED,
            "" +  ChatColor.RED + ChatColor.BOLD + "RED",
            new String[]{
                    ChatColor.GRAY + Style.BOLD + "▓▓▓▓▓▓▓▓▓▓",
                    ChatColor.GRAY + Style.BOLD + "▓▓" + ChatColor.RED + Style.BOLD + "▓▓▓▓▓" + ChatColor.GRAY + Style.BOLD +  "▓▓▓",
                    ChatColor.GRAY + Style.BOLD + "▓▓" + ChatColor.RED + Style.BOLD + "▓" + ChatColor.GRAY + Style.BOLD + "▓▓▓▓" + ChatColor.RED + ChatColor.BOLD + "▓" + Style.BOLD + ChatColor.GRAY + Style.BOLD + "▓▓",
                    ChatColor.GRAY + Style.BOLD + "▓▓" + ChatColor.RED + Style.BOLD + "▓" + ChatColor.GRAY + Style.BOLD + "▓▓▓▓" + ChatColor.RED + Style.BOLD + "▓" + ChatColor.GRAY + Style.BOLD + "▓▓",
                    ChatColor.GRAY + Style.BOLD + "▓▓" + ChatColor.RED + Style.BOLD +"▓▓▓▓▓" + ChatColor.GRAY +Style.BOLD + "▓▓▓",
                    ChatColor.GRAY +Style.BOLD + "▓▓" + ChatColor.RED + Style.BOLD +"▓" + ChatColor.GRAY + Style.BOLD +"▓" + ChatColor.RED + Style.BOLD +"▓" + ChatColor.GRAY + Style.BOLD + "▓▓▓▓▓ " + ChatColor.RED + ChatColor.BOLD + "RED " + ChatColor.WHITE + " has been wiped out!",
                    ChatColor.GRAY + Style.BOLD +"▓▓" + ChatColor.RED +Style.BOLD + "▓" + ChatColor.GRAY +Style.BOLD + "▓▓" + ChatColor.RED +Style.BOLD + "▓"  + ChatColor.GRAY + Style.BOLD + "▓▓▓▓",
                    ChatColor.GRAY + Style.BOLD +"▓▓" + ChatColor.RED +Style.BOLD + "▓" + ChatColor.GRAY +Style.BOLD + "▓▓▓" + ChatColor.RED +Style.BOLD + "▓" + ChatColor.GRAY +Style.BOLD + "▓▓▓",
                    ChatColor.GRAY + Style.BOLD +"▓▓" + ChatColor.RED +Style.BOLD + "▓" + ChatColor.GRAY + Style.BOLD +"▓▓▓▓" + ChatColor.RED +Style.BOLD + "▓" + ChatColor.GRAY +Style.BOLD + "▓▓",
                    ChatColor.GRAY +Style.BOLD + "▓▓▓▓▓▓▓▓▓▓",
            }),
    AQUA(
            ChatColor.WHITE + "[" + ChatColor.AQUA + "A" + ChatColor.WHITE + "] " + ChatColor.AQUA,
            "" + ChatColor.AQUA + ChatColor.BOLD + "AQUA",
            new String[]{
                    ChatColor.GRAY +Style.BOLD + "▓▓▓▓▓▓▓▓▓▓",
                    ChatColor.GRAY + Style.BOLD + "▓▓▓" + ChatColor.AQUA + Style.BOLD + "▓▓▓▓" + Style.GRAY + Style.BOLD + "▓▓▓",
                    ChatColor.GRAY + Style.BOLD + "▓▓" + ChatColor.AQUA + Style.BOLD + "▓" + ChatColor.GRAY + Style.BOLD + "▓▓▓▓" + ChatColor.AQUA + Style.BOLD + "▓" + ChatColor.GRAY + Style.BOLD + "▓▓",
                    ChatColor.GRAY + Style.BOLD + "▓▓" + ChatColor.AQUA + Style.BOLD + "▓" + ChatColor.GRAY + Style.BOLD + "▓▓▓▓" + ChatColor.AQUA + Style.BOLD + "▓" + ChatColor.GRAY + Style.BOLD + "▓▓",
                    ChatColor.GRAY + Style.BOLD + "▓▓" + ChatColor.AQUA + Style.BOLD + "▓▓▓▓▓▓" + ChatColor.GRAY + Style.BOLD + "▓▓",
                    ChatColor.GRAY + Style.BOLD +"▓▓" + ChatColor.AQUA + Style.BOLD + "▓" + ChatColor.GRAY + Style.BOLD + "▓▓▓▓" + ChatColor.AQUA + Style.BOLD +"▓" + ChatColor.GRAY + Style.BOLD +"▓▓ " + ChatColor.AQUA + ChatColor.BOLD + "AQUA " + ChatColor.WHITE + " has been wiped out!",
                    ChatColor.GRAY + Style.BOLD +"▓▓" + ChatColor.AQUA +Style.BOLD + "▓" + ChatColor.GRAY + Style.BOLD + "▓▓▓▓" + ChatColor.AQUA + Style.BOLD +"▓" + ChatColor.GRAY + Style.BOLD +"▓▓",
                    ChatColor.GRAY + Style.BOLD +"▓▓" + ChatColor.AQUA +Style.BOLD + "▓" + ChatColor.GRAY + Style.BOLD + "▓▓▓▓" + ChatColor.AQUA +Style.BOLD + "▓" + ChatColor.GRAY +Style.BOLD + "▓▓",
                    ChatColor.GRAY + Style.BOLD +"▓▓" + ChatColor.AQUA +Style.BOLD + "▓" + ChatColor.GRAY +Style.BOLD + "▓▓▓▓" + ChatColor.AQUA + Style.BOLD +"▓" + ChatColor.GRAY +Style.BOLD + "▓▓",
                    ChatColor.GRAY + Style.BOLD + "▓▓▓▓▓▓▓▓▓▓",
            });

    private String prefix;
    private String displayName;
    private String[] logo;
}
