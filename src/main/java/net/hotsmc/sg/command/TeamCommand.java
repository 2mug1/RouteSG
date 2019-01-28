package net.hotsmc.sg.command;

import net.hotsmc.core.other.Style;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.GameState;
import net.hotsmc.sg.game.GameTask;
import net.hotsmc.sg.player.GamePlayer;
import net.hotsmc.sg.team.GameTeam;
import net.hotsmc.sg.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            if (args.length == 0) {
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                player.sendMessage(Style.YELLOW + Style.BOLD + " Team Help");
                player.sendMessage(Style.GRAY + " (Leader)");
                player.sendMessage(Style.AQUA + " 作成: /team create");
                player.sendMessage(Style.AQUA + " 離脱: /team leave");
                player.sendMessage(Style.AQUA + " 解散: /team disband");
                player.sendMessage(Style.AQUA + " キック: /team kick <player>");
                player.sendMessage(Style.GRAY + " (General)");
                player.sendMessage(Style.AQUA + " 招待: /team invite <player1> <player2>...");
                player.sendMessage(Style.AQUA + " 招待承認: /team accept <player>");
                player.sendMessage(Style.AQUA + " チャット: /tc <message>");
                player.sendMessage(Style.AQUA + " メンバー表示: /tl");
                player.sendMessage(Style.AQUA + " 他チームのメンバー表示: /tl <player>");
                player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                return true;
            }

            if (HSG.getGameTask().getState() == GameState.Lobby) {

                GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
                if (gamePlayer == null) return true;
                TeamManager m = HSG.getInstance().getTeamManager();

                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("create")) {
                        if (!gamePlayer.isInTeam()) {
                            m.addTeam(m.createTeam(gamePlayer));
                        } else {
                            gamePlayer.sendMessage(Style.RED + "You have already been in team.");
                        }
                    } else if (args[0].equalsIgnoreCase("leave")) {
                        if (gamePlayer.isInTeam()) {
                            gamePlayer.getInTeam().removePlayer(gamePlayer);
                        } else {
                            gamePlayer.sendMessage(Style.RED + "You aren't in team.");
                        }
                    } else if (args[0].equalsIgnoreCase("disband")) {
                        if (!gamePlayer.isInTeam()) {
                            gamePlayer.sendMessage(Style.RED + "You aren't in team.");
                        } else if (gamePlayer.getInTeam().isLeader(gamePlayer)) {
                            gamePlayer.getInTeam().disband();
                        } else {
                            gamePlayer.sendMessage(Style.RED + "You aren't leader");
                        }
                    }
                }
                if (args.length >= 2) {
                    if (args[0].equalsIgnoreCase("invite")) {
                        if (gamePlayer.isInTeam()) {
                            for (int i = 1; i < args.length; i++) {
                                String targetName = args[i];
                                if (targetName.equalsIgnoreCase(player.getName())) {
                                    player.sendMessage(Style.RED + "Can't yourself");
                                } else if (Bukkit.getPlayer(targetName) == null || !Bukkit.getPlayer(targetName).isOnline()) {
                                    player.sendMessage(ChatColor.RED + targetName + " is offline.");
                                } else if (HSG.getGameTask().getGamePlayer(Bukkit.getPlayer(targetName)) == null) {
                                    player.sendMessage(ChatColor.RED + targetName + " is offline.");
                                } else {
                                    gamePlayer.getInTeam().invite(gamePlayer, HSG.getGameTask().getGamePlayer(Bukkit.getPlayer(targetName)));
                                }
                            }
                        } else {
                            player.sendMessage(Style.RED + "You aren't in team");
                        }
                    }
                }
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("accept")) {
                        if (gamePlayer.isInTeam()) {
                            gamePlayer.sendMessage(Style.RED + "You have already been in team.");
                            return true;
                        }
                        String teamLeader = args[1];
                        GameTeam team = m.getTeamByLeader(teamLeader);
                        if (team != null) {
                            team.addPlayer(gamePlayer);
                        } else {
                            gamePlayer.sendMessage(Style.RED + "That team can't find.");
                        }
                    } else if (args[0].equalsIgnoreCase("kick")) {
                        if (!gamePlayer.isInTeam()) {
                            gamePlayer.sendMessage(Style.RED + "You are not in team");
                            return true;
                        }
                        String targetName = args[1];
                        if (targetName.equalsIgnoreCase(player.getName())) {
                            player.sendMessage(Style.RED + "Can't yourself");
                            return true;
                        }
                        Player target = Bukkit.getPlayer(targetName);
                        if (target == null || !target.isOnline()) {
                            player.sendMessage(ChatColor.RED + targetName + " is offline.");
                            return true;
                        }
                        GamePlayer targetGP = HSG.getGameTask().getGamePlayer(target);
                        if (targetGP == null) {
                            player.sendMessage(ChatColor.RED + targetName + " is offline.");
                            return true;
                        }
                        if (gamePlayer.getInTeam().isLeader(gamePlayer)) {
                            if (gamePlayer.getInTeam().isExists(targetGP)) {
                                gamePlayer.getInTeam().kick(gamePlayer, targetGP);
                            }
                        } else {
                            gamePlayer.sendMessage(Style.RED + "You are not leader.");
                        }
                    }
                }
            }else{
                player.sendMessage(ChatColor.RED + "Game has already been running.");
            }
        }
        return true;
    }
}
