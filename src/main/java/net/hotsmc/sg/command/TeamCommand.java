package net.hotsmc.sg.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sun.tracing.dtrace.StabilityLevel;
import net.hotsmc.core.HotsCore;
import net.hotsmc.core.other.Style;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GamePlayer;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.team.GameTeam;
import net.hotsmc.sg.team.TeamType;
import net.hotsmc.sg.utility.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class TeamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            final GameTask game = HSG.getGameTask();
            if (!game.getGameConfig().isCustomSG()) {
                player.sendMessage(Style.RED + "This server isn't custom sg.");
                return true;
            }
            if (game.getHost() != player.getUniqueId()) {
                player.sendMessage(Style.RED + "You are not hosting the game.");
                return true;
            }
            if (!game.isTeamFight()) {
                player.sendMessage(Style.RED + "Please enable team fight.");
                return true;
            }
            if(args[0] == null){
                player.sendMessage(Style.RED + "/team <team> <player1> <player2> <player3>...");
                player.sendMessage(Style.YELLOW + "Teams: " + Style.AQUA + "AQUA" + Style.GRAY + ", " + Style.RED + "RED");
                return true;
            }
            TeamType teamType = getTeamType(args[0]);
            if (teamType == null) {
                player.sendMessage(Style.RED + "/team <team> <player1> <player2> <player3>...");
                player.sendMessage(Style.YELLOW + "Teams: " + Style.AQUA + "AQUA" + Style.GRAY + ", " + Style.RED + "RED");
                return true;
            }
            GameTeam gameTeam = HSG.getGameTask().getGameTeam(teamType);
            if (gameTeam == null) return true;
            for (int i = 1; i < args.length; i++) {
                String name = args[i];
                Player target = Bukkit.getPlayer(name);
                if (target == null) {
                    player.sendMessage(Style.RED + "Error: " + name + " is offline.");
                }
                else if(HSG.getInstance().getObserverPlayers().contains(name.toLowerCase())){
                    player.sendMessage(Style.RED + "Error: " + name + " is observer.");
                }
                //現在参加しているチームがあったら
                else if (HSG.getGameTask().getGamePlayer(target).getInTeam() != null) {
                    GameTeam team = HSG.getGameTask().getGamePlayer(target).getInTeam();
                    String teamName = team.getTeamType().getDisplayName();
                    team.getPlayers().remove(HSG.getGameTask().getGamePlayer(target));
                    player.sendMessage(Style.YELLOW + name + Style.WHITE + " has been removed from " + teamName);
                    gameTeam.addPlayer(HSG.getGameTask().getGamePlayer(target));
                    player.sendMessage(Style.YELLOW + name + Style.WHITE + " has been added to " + teamType.getDisplayName());
                } else {
                    gameTeam.addPlayer(HSG.getGameTask().getGamePlayer(target));
                    player.sendMessage(Style.YELLOW + name + Style.WHITE + " has been added to " + teamType.getDisplayName());
                }
            }
        }
        return true;
    }

    private TeamType getTeamType(String team){
        for(TeamType type : TeamType.values()){
            if(type.name().equals(team)){
                return type;
            }
        }
        return null;
    }
}
