package net.routemc.sg.listener.listeners;

import ca.wacos.nametagedit.NametagAPI;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.trollcoding.requires.hotbar.ClickActionItem;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.core.RouteAPI;
import net.routemc.core.profile.Profile;
import net.routemc.core.rank.Rank;
import net.routemc.core.util.menu.ViewPlayerMenu;
import net.routemc.sg.database.PlayerData;
import net.routemc.sg.RouteSG;
import net.routemc.sg.game.GameTask;
import net.routemc.sg.hotbar.PlayerHotbar;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.player.GamePlayerData;
import net.routemc.sg.team.GameTeam;
import net.routemc.sg.game.GameConfig;
import net.routemc.sg.game.GameState;
import net.routemc.sg.utility.*;
import org.apache.logging.log4j.core.appender.routing.Route;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class PlayerListener implements Listener {

    private Field fieldPlayerConnection;
    private Method sendPacket;
    private Constructor<?> packetVelocity;

    public PlayerListener() {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
            Class<?> packetVelocityClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutEntityVelocity");
            Class<?> playerConnectionClass = Class.forName("net.minecraft.server." + version + ".PlayerConnection");

            this.fieldPlayerConnection = entityPlayerClass.getField("playerConnection");
            this.sendPacket = playerConnectionClass.getMethod("sendPacket", new Class[]{packetVelocityClass.getSuperclass()});
            this.packetVelocity = packetVelocityClass.getConstructor(new Class[]{Integer.TYPE, Double.TYPE, Double.TYPE, Double.TYPE});
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        final GameTask game = RouteSG.getGameTask();
        final GameState state = game.getState();
        if (state == GameState.PreGame) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + RouteSG.getSettings().getServerName() + " is preparing the game");
        } else {
            if (state == GameState.EndGame) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + RouteSG.getSettings().getServerName() + " is ending the game");
            }

            if (game.getGameConfig().isCustomSG()) {
                if (game.getHost() != null && game.getHost() == event.getUniqueId()) {
                    return;
                }

                Profile profile = RouteAPI.getProfileByUUID(event.getUniqueId());
                Rank playerRank = profile.getActiveRank();

                if (state == GameState.Lobby && Bukkit.getServer().getOnlinePlayers().size() < 1 && game.getHost() == null) {
                    if (playerRank.getWeight() < 1 && profile.getEconomy().getCoins() < 10) {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Style.AQUA + "Not enough coins to make Custom SG.");
                    }
                    return;
                }

                if (state == GameState.Lobby) {
                    if (!RouteSG.getInstance().getWhitelistedPlayers().contains(event.getName().toLowerCase())) {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Style.AQUA + "You are not registered to roster.");
                    }
                } else {
                    if (playerRank.getWeight() >= 1) {
                        return;
                    }

                    if (game.isSpectateRosterOnly() && !RouteSG.getInstance().getWhitelistedPlayers().contains(event.getName().toLowerCase())) {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Style.AQUA + "Roster only to spectate.");
                    }
                }
            }

        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(RouteAPI.getRankOfPlayerByUUID(player.getUniqueId()).getColor() + player.getName() + Style.YELLOW + " has joined.");

        player.setDisplayName(RouteAPI.getColoredName(player));

        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.setExp(0F);
        player.setLevel(0);
        PlayerUtility.clearEffects(player);
        EntityEquipment equipment = player.getEquipment();
        equipment.setHelmet(null);
        equipment.setChestplate(null);
        equipment.setLeggings(null);
        equipment.setBoots(null);
        GamePlayer gamePlayer = new GamePlayer(player);
        PlayerData playerData = new PlayerData(player.getUniqueId().toString());
        playerData.setName(player.getName());
        playerData.loadData();
        gamePlayer.setPlayerData(playerData);
        gamePlayer.resetSponsorItems();
        GameTask gameTask = RouteSG.getGameTask();
        GameConfig gameConfig = gameTask.getGameConfig();
        GameState state = gameTask.getState();

        Profile profile = RouteAPI.getProfileByUUID(player.getUniqueId());
        NametagAPI.setPrefix(player.getName(), (profile.isInClan() ? profile.getClan().getStyleTag() : "") + Style.RESET + profile.getActiveRank().getColor());

        if (state == GameState.Lobby) {
            if (gameConfig.isCustomSG()) {
                if (Bukkit.getServer().getOnlinePlayers().size() <= 1) {
                    if (RouteAPI.getRankOfPlayer(player).getWeight() < 1) {
                        RouteAPI.getEconomyOfPlayer(player).removeCoin(10);
                    }
                    player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                    player.sendMessage(Style.YELLOW + Style.BOLD + " Host Help");
                    player.sendMessage(Style.AQUA + " ホストする: /host");
                    player.sendMessage(Style.AQUA + " プレイヤー登録: /register <player1> <player2> <player3>...");
                    player.sendMessage(Style.AQUA + " プレイヤー削除: /unregister <player>");
                    player.sendMessage(Style.AQUA + " ホスト変更: /givehost <player>");
                    player.sendMessage(Style.AQUA + " 登録プレイヤー表示: /roster");
                    player.sendMessage(Style.AQUA + " オブザーバー追加: /observer <player1> <player2> <player3>...");
                    player.sendMessage(Style.AQUA + " 戦闘ログ確認: /fl");
                    player.sendMessage(Style.HORIZONTAL_SEPARATOR);
                }
                if (RouteSG.getInstance().getObserverPlayers().contains(player.getName().toLowerCase())) {
                    gamePlayer.enableWatching();
                }
            }
            player.teleport(gameConfig.getLobbyLocation());
            if (!gameConfig.isCustomSG()) {
                if (gameTask.isTimerFlag()) {
                    gameTask.getVoteManager().send(player);
                }
            }
            if (!gameConfig.isCustomSG()) {
                gamePlayer.setHotbar(PlayerHotbar.LOBBY);
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("CustomSG-Update-Host");
                out.writeUTF(RouteSG.getSettings().getServerName());
                out.writeUTF("Not Hosting");
                player.sendPluginMessage(RouteSG.getInstance(), "BungeeCord", out.toByteArray());
            } else {
                gamePlayer.setHotbar(PlayerHotbar.CUSTOMSG_LOBBY);
            }
            player.getInventory().setItem(0, ItemUtility.createItemStack(ChatColor.AQUA + "Lobby Sword", Material.STONE_SWORD, true));
            player.getInventory().setItem(1, ItemUtility.createItemStack(ChatColor.AQUA + "Lobby Rod", Material.FISHING_ROD, true));
        }
        gameTask.addGamePlayer(gamePlayer);
        if (state == GameState.LiveGame || state == GameState.PreDeathmatch || state == GameState.DeathMatch) {
            player.teleport(gameTask.getCurrentMap().getDefaultSpawn());
            //観戦者同士互いに見えないようにする
            for (GamePlayer gp : gameTask.getGamePlayers()) {
                if (gp.isWatching()) {
                    player.hidePlayer(gp.getPlayer());
                    gp.getPlayer().hidePlayer(player);
                }
            }
            gamePlayer.enableWatching();
            gamePlayer.setHotbar(PlayerHotbar.SPECTATE);
            ChatUtility.sendMessage(gamePlayer, ChatColor.WHITE + "If you want to spectate player: " + ChatColor.YELLOW + "/spec <player>");
            ChatUtility.sendMessage(gamePlayer, ChatColor.WHITE + "If you want to send sponsor to player: " + ChatColor.YELLOW + "/sponsor <player>");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(RouteAPI.getRankOfPlayerByUUID(player.getUniqueId()).getColor() + player.getName() + Style.YELLOW + " has left.");

        GameTask gameTask = RouteSG.getGameTask();
        GameState state = gameTask.getState();
        GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
        if (gamePlayer == null) return;
        //CustomSGでLobbyでホストが抜けたらホストを解除して全員Hubに転送
        if (gameTask.getGameConfig().isCustomSG()) {
            if (state == GameState.Lobby) {
                if (gameTask.getHost() == player.getUniqueId()) {
                    gameTask.setHost(null);
                    gameTask.setHostWithPlay(false);
                    gameTask.setSponsor(false);
                    gameTask.setGracePeriodTimeSec(1800);
                    gameTask.setSpectateRosterOnly(false);
                    gameTask.getFightLog().clear();
                    RouteSG.getInstance().getWhitelistedPlayers().clear();
                    RouteSG.getInstance().getObserverPlayers().clear();
                    RouteSG.getInstance().getTeamManager().getTeams().clear();
                    for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                        ChatUtility.sendMessage(all, Style.HORIZONTAL_SEPARATOR);
                        ChatUtility.sendMessage(all, "Host不在のためCustomSGは中止されました");
                        ChatUtility.sendMessage(all, Style.HORIZONTAL_SEPARATOR);
                        BungeeUtils.connect(all, "SurvivalGames");
                    }
                }
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("CustomSG-Update-Host");
                out.writeUTF(RouteSG.getSettings().getServerName());
                out.writeUTF("Not Hosting");
                player.sendPluginMessage(RouteSG.getInstance(), "BungeeCord", out.toByteArray());
            }
        }

        if (gamePlayer.isWatching()) {
            PlayerUtility.clearEffects(player);
        }

        //生きているプレイヤーだったら
        if (!gamePlayer.isWatching()) {
            if (state != GameState.Lobby) {
                gameTask.addFightLog(gamePlayer.getName() + Style.YELLOW + " dead (left the game)");
            }
            GameTeam team = gameTask.getGamePlayer(player).getInTeam();
            if (team != null) {
                if (state != GameState.Lobby) {
                    gameTask.getGamePlayer(player).setAlive(false);
                    team.removePlayer(gamePlayer);
                    if (team.getAlivePlayers().size() <= 0) {
                        gameTask.addFightLog(Style.YELLOW + "Team #" + team.getTeamID() + Style.WHITE + " has been eliminated.");
                        ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);
                        ChatUtility.normalBroadcast(Style.YELLOW + "Team #" + team.getTeamID() + Style.WHITE + " has been eliminated!");
                        ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);
                        for (Player player1 : Bukkit.getServer().getOnlinePlayers()) {
                            player1.playSound(player1.getLocation(), Sound.WITHER_SPAWN, 0.7F, 1);
                        }
                    }
                } else {
                    team.removePlayer(gamePlayer);
                }
            }
            GamePlayerData gamePlayerData = RouteSG.getGameTask().getGamePlayerData(gamePlayer.getName());
            if (gamePlayerData != null) {
                gamePlayerData.setAlive(false);
                gamePlayerData.applyPlaceRank();
            }
            if (state == GameState.PreGame) {
                Location location = player.getLocation();
                location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
            }
            if (state == GameState.LiveGame || state == GameState.PreDeathmatch || state == GameState.DeathMatch) {
                gamePlayer.setWatching(true);
                World world = Bukkit.getWorld(RouteSG.getGameTask().getCurrentMap().getDefaultSpawn().getWorld().getName());
                Location location = player.getLocation();
                //チェストを開けていたら
                if (gamePlayer.getOpeningChest() != null) {
                    ChestPacketUtility.closeChestPacket(gamePlayer.getOpeningChest().getLocation());
                    gamePlayer.setOpeningChest(null);
                    player.getWorld().playSound(player.getLocation(), Sound.CHEST_CLOSE, 0.5F, 1.0F);
                }
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    if (itemStack != null && itemStack.getType() != Material.AIR) {
                        world.dropItemNaturally(location, itemStack);
                    }
                }
                //アイテム全部落とす
                for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                    if (itemStack != null && itemStack.getType() != Material.AIR) {
                        player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                    }
                }
                if (!gameTask.getGameConfig().isCustomSG()) {
                    ChatUtility.sendMessage(gamePlayer, ChatColor.DARK_AQUA + "You've lost "
                            + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + gamePlayer.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for dying");
                    gamePlayer.getPlayerData().withdrawPoint(gamePlayer.getPlayerData().calculatedPoint());
                }
                PlayerUtility.clearEffects(player);
                location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
                ChatUtility.broadcast(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + RouteAPI.getColoredName(player));
                if (gameTask.countAlive() <= 3) {
                    for (GamePlayer gamePlayer1 : gameTask.getGamePlayers()) {
                        if (!gamePlayer1.isWatching()) {
                            gamePlayer1.getPlayerData().updateTop3(1);
                        }
                    }
                }
            }
        }

        if (gameTask.getState() != GameState.EndGame && gameTask.getState() != GameState.Lobby && gameTask.countAlive() <= 1) {
            gameTask.onEndGame();
        }

        if (gamePlayer.getInTeam() != null) {
            gamePlayer.getInTeam().removePlayer(gamePlayer);
        }

        RouteSG.getGameTask().removeGamePlayer(gamePlayer);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
        if (gamePlayer == null) return;
        ItemStack itemStack = player.getItemInHand();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;
        if (RouteSG.getGameTask().getState() == GameState.Lobby) {
            if (itemStack.getType() == Material.ANVIL || itemStack.getType() == Material.CHEST || itemStack.getType() == Material.HOPPER || itemStack.getType() == Material.WORKBENCH) {
                event.setCancelled(true);
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            for (ClickActionItem clickActionItem : gamePlayer.getHotbar().getAdapter().getItems()) {
                if (clickActionItem != null) {
                    if (clickActionItem.equals(itemStack)) {
                        clickActionItem.clickAction(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            GameTask gameTask = RouteSG.getGameTask();
            GameState state = gameTask.getState();
            if (state == GameState.Lobby || state == GameState.PreGame) {
                e.setCancelled(true);
                return;
            }
            Player player = (Player) e.getEntity();
            GamePlayer gamePlayer = gameTask.getGamePlayer(player);
            if (gamePlayer.isWatching()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        GameTask gameTask = RouteSG.getGameTask();
        if (gameTask.getGamePlayer(event.getPlayer()).isWatching() || gameTask.getState() == GameState.Lobby) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        GameTask gameTask = RouteSG.getGameTask();
        if (gameTask.getGamePlayer(event.getPlayer()).isWatching()) {
            event.setCancelled(true);
        }
        if (event.getItem().getItemStack().getType() == Material.WOOD) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                event.setCancelled(true);
                return;
            }
            Player player = (Player) event.getEntity();
            GameTask gameTask = RouteSG.getGameTask();
            GameState state = gameTask.getState();
            GamePlayer gamePlayer = gameTask.getGamePlayer(player);
            if (gamePlayer == null) return;
            if (gamePlayer.isWatching()) {
                event.setCancelled(true);
                return;
            }
            if (state == GameState.Lobby) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            } else if (state == GameState.PreGame || state == GameState.PreDeathmatch || state == GameState.EndGame) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        GameTask gameTask = RouteSG.getGameTask();
        GameState state = gameTask.getState();
        //釣り竿vsプレイヤー
        /*
        if (event.getDamager() instanceof FishHook && ((FishHook) event.getDamager()).getShooter() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) ((FishHook) event.getDamager()).getShooter();
            GamePlayer damagerGP = gameTask.getGamePlayer(damager);
            Player damaged = (Player) event.getEntity();
            GamePlayer damagedGP = gameTask.getGamePlayer(damaged);
            if (!damagedGP.isWatching()) {
                if (state == GameState.Lobby) {
                    if (!damagerGP.getPlayerData().isLobbyPvP()) {
                        event.setCancelled(true);
                        return;
                    }
                    if(!damagedGP.getPlayerData().isLobbyPvP()) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (state != GameState.Lobby) {
                    if (gameTask.getTime() > gameTask.getGracePeriodTimeSec()) {
                        event.setCancelled(true);
                        damager.sendMessage(Style.RED + "PvP will be enabled in " + TimeUtility.timeFormat(gameTask.getGracePeriodTimeSec()));
                        return;
                    }
                }

                double horizontalMultiplier = RouteSG.getSettings().getFisihingrod_horizontalMultiplier();
                double verticalMultiplier = RouteSG.getSettings().getFisihingrod_verticalMultiplier();
                double sprintMultiplier = damager.isSprinting() ? 0.8D : 0.5D;
                double kbMultiplier = damager.getItemInHand() == null ? 0.0D : damager.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) * 0.2D;

                double airMultiplier = damaged.isOnGround() ? 1.0D : 0.5D;

                Vector knockback = damaged.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();
                knockback.setX((knockback.getX() * sprintMultiplier + kbMultiplier) * horizontalMultiplier);
                knockback.setY(0.35D * airMultiplier * verticalMultiplier);
                knockback.setZ((knockback.getZ() * sprintMultiplier + kbMultiplier) * horizontalMultiplier);
                try {
                    Object entityPlayer = damaged.getClass().getMethod("getHandle", new Class[0]).invoke(damaged, new Object[0]);
                    Object playerConnection = this.fieldPlayerConnection.get(entityPlayer);
                    Object packet = this.packetVelocity.newInstance(new Object[]{Integer.valueOf(damaged.getEntityId()), Double.valueOf(knockback.getX()), Double.valueOf(knockback.getY()), Double.valueOf(knockback.getZ())});
                    this.sendPacket.invoke(playerConnection, new Object[]{packet});
                } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }*/
        //プレイヤーvsプレイヤー
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            GamePlayer damagerGP = gameTask.getGamePlayer(damager);
            GamePlayer damagedGP = gameTask.getGamePlayer(damaged);
            if (damagerGP.isWatching()) {
                event.setCancelled(true);
                return;
            }
            if (state == GameState.PreGame || state == GameState.PreDeathmatch) {
                event.setCancelled(true);
            } else if (state == GameState.LiveGame) {
                if (gameTask.getTime() > gameTask.getGracePeriodTimeSec()) {
                    event.setCancelled(true);
                    damager.sendMessage(Style.RED + "PvP will be enabled in " + TimeUtility.timeFormat(gameTask.getGracePeriodTimeSec()));
                }
            } else if (state == GameState.Lobby) {
                if (!damagedGP.getPlayerData().isLobbyPvP()) {
                    event.setCancelled(true);
                    return;
                }
                if (!damagerGP.getPlayerData().isLobbyPvP()) {
                    event.setCancelled(true);
                    return;
                }
                event.setDamage(0D);
            } else if (damagedGP.isWatching()) {
                event.setCancelled(true);
            }
        }
        //Mobだったら
        else if (event.getDamager() instanceof Player && !(event.getEntity() instanceof Player)) {
            Player damager = (Player) event.getDamager();
            GamePlayer damagerGP = gameTask.getGamePlayer(damager);
            if (damagerGP.isWatching()) {
                event.setCancelled(true);
            }
        } else if (event.getDamager() instanceof Arrow) {
            if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
                if (state == GameState.LiveGame) {
                    if (gameTask.getTime() > gameTask.getGracePeriodTimeSec()) {
                        event.setCancelled(true);
                        Player damager = (Player) ((Arrow) event.getDamager()).getShooter();
                        damager.sendMessage(Style.RED + "PvP will be enabled in " + TimeUtility.timeFormat(gameTask.getGracePeriodTimeSec()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamageByBlock(EntityDamageByBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            GameTask gameTask = RouteSG.getGameTask();
            GamePlayer damagedGP = gameTask.getGamePlayer(damaged);
            if (damagedGP.isWatching()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * リスポーンしたときにwatching有効化
     *
     * @param event
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        GameTask gameTask = RouteSG.getGameTask();
        GamePlayer gamePlayer = gameTask.getGamePlayer(player);
        event.setRespawnLocation(gamePlayer.getRespawnLocation());
        gamePlayer.enableWatching();
        gamePlayer.setHotbar(PlayerHotbar.SPECTATE);
        ChatUtility.broadcast("" + ChatColor.GREEN + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + gameTask.countAlive() + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + " tributes remain!");
        ChatUtility.broadcast(ChatColor.GREEN + "There are " + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + gameTask.countWatching() + ChatColor.DARK_GRAY + "]"
                + ChatColor.GREEN + " spectators watching the game.");
        ChatUtility.sendMessage(player, ChatColor.WHITE + "If you want to spectate player: " + ChatColor.YELLOW + "/spec <player>");
        ChatUtility.sendMessage(player, ChatColor.WHITE + "If you want to send sponsor to player: " + ChatColor.YELLOW + "/sponsor <player>");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        //プレイヤー取得
        Player dead = e.getEntity();
        Player killer = dead.getKiller();
        GameTask gameTask = RouteSG.getGameTask();
        GamePlayer deadGP = gameTask.getGamePlayer(dead);

        if (deadGP == null) return;

        GamePlayerData deadData = RouteSG.getGameTask().getGamePlayerData(dead.getName());
        deadData.setAlive(false);
        deadData.applyPlaceRank();

        deadGP.setRespawnLocation(dead.getLocation());

        Profile profile = RouteAPI.getProfileByUUID(dead.getUniqueId());

        GameTeam team = gameTask.getGamePlayer(dead).getInTeam();
        if (team != null) {
            gameTask.getGamePlayer(dead).setAlive(false);
            team.broadcast(RouteAPI.getColoredName(dead) + Style.LIGHT_PURPLE + " has dead " + Style.GRAY + " (" + team.getAlivePlayers().size() + " alive)");
            if (team.getAlivePlayers().size() <= 0) {
                gameTask.addFightLog(Style.YELLOW + "Team #" + team.getTeamID() + Style.WHITE + " has been eliminated.");
                ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);
                ChatUtility.normalBroadcast(Style.YELLOW + "Team #" + team.getTeamID() + Style.WHITE + " has been eliminated!");
                ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);
                for (Player player1 : Bukkit.getServer().getOnlinePlayers()) {
                    player1.playSound(player1.getLocation(), Sound.WITHER_SPAWN, 0.7F, 1);
                }
            }
        }

        //自滅の場合①
        if (killer == null) {
            gameTask.addFightLog(deadGP.getSGName() + Style.YELLOW + " dead");
            Location location = dead.getLocation();
            location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
            deadGP.setWatching(true);
            e.setDroppedExp(0);
            if (!gameTask.getGameConfig().isCustomSG()) {
                ChatUtility.sendMessage(dead, ChatColor.DARK_AQUA + "You've lost " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for dying");
                deadGP.getPlayerData().withdrawPoint(deadGP.getPlayerData().calculatedPoint());
            }
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + (profile.isInClan() ? profile.getClan().getStyleTag() + " " : "") + deadGP.getSGName());

            deadGP.respawn();

            if (gameTask.countAlive() <= 1) {
                gameTask.onEndGame();
            }

            if (gameTask.countAlive() <= 3) {
                for (GamePlayer gamePlayer : gameTask.getGamePlayers()) {
                    if (!gamePlayer.isWatching()) {
                        gamePlayer.getPlayerData().
                                updateTop3(1);
                    }
                }
            }

            if (deadGP.getOpeningChest() != null) {
                ChestPacketUtility.closeChestPacket(deadGP.getOpeningChest().getLocation());
                deadGP.setOpeningChest(null);
                dead.getWorld().playSound(dead.getLocation(), Sound.CHEST_CLOSE, 0.5F, 1.0F);
            }

            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);
            dead.sendMessage(Style.AQUA + "You have been placed #" + deadData.getPlaceRank() + "/#" + RouteSG.getGameTask().getGamePlayerData().size());
            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);
            return;
        }
        //自滅の場合②
        if (killer.equals(dead)) {
            gameTask.addFightLog(deadGP.getSGName() + Style.YELLOW + " dead");
            Location location = dead.getLocation();
            location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
            deadGP.setWatching(true);
            e.setDroppedExp(0);
            ChatUtility.sendMessage(dead, ChatColor.DARK_AQUA + "You've lost " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for dying");
            deadGP.getPlayerData().withdrawPoint(deadGP.getPlayerData().calculatedPoint());
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + (profile.isInClan() ? profile.getClan().getStyleTag() + " " : "") + deadGP.getSGName());

            deadGP.respawn();

            if (gameTask.countAlive() <= 1) {
                gameTask.onEndGame();
            }
            if (!gameTask.getGameConfig().isCustomSG()) {
                if (gameTask.countAlive() <= 3) {
                    for (GamePlayer gamePlayer : gameTask.getGamePlayers()) {
                        if (!gamePlayer.isWatching()) {
                            gamePlayer.getPlayerData().updateTop3(1);
                        }
                    }
                }
            }

            if (deadGP.getOpeningChest() != null) {
                ChestPacketUtility.closeChestPacket(deadGP.getOpeningChest().getLocation());
                deadGP.setOpeningChest(null);
                dead.getWorld().playSound(dead.getLocation(), Sound.CHEST_CLOSE, 0.5F, 1.0F);
            }

            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);
            dead.sendMessage(Style.AQUA + "You have been placed #" + deadData.getPlaceRank() + "/#" + RouteSG.getGameTask().getGamePlayerData().size());
            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);
        } else {
            GamePlayer killerGP = gameTask.getGamePlayer(killer);

            //Friendly Kill
            if (killerGP.isInTeam() && deadGP.isInTeam() && deadGP.getInTeam().getTeamID() == killerGP.getInTeam().getTeamID()) {
                killerGP.sendMessage(Style.RED + Style.BOLD + "Friendly Kill" + Style.DARK_GRAY + " » " + Style.YELLOW + "You've killed your team mate!");
            } else {
                GamePlayerData gamePlayerData = RouteSG.getGameTask().getGamePlayerData(killer.getName());
                gamePlayerData.addKill();
                killer.sendMessage(Style.AQUA + "You have " + gamePlayerData.getKills() + " kills.");
                RouteSG.getGameTask().getGamePlayerData().sort((o1, o2) -> o1.getKills() > o2.getKills() ? -1 : 1);
                RouteAPI.getExperienceOfPlayer(killer).addExp(20);
                RouteAPI.getEconomyOfPlayer(killer).addCoin(3);
            }

            gameTask.addFightLog(deadGP.getSGName() + Style.YELLOW + " was killed by " + killerGP.getSGName());
            Location location = dead.getLocation();
            location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
            deadGP.setWatching(true);
            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);
            dead.sendMessage(Style.AQUA + "You have been placed #" + deadData.getPlaceRank() + "/#" + RouteSG.getGameTask().getGamePlayerData().size());
            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);

            if (!gameTask.getGameConfig().isCustomSG()) {
                killerGP.getPlayerData().updateKill(1);

                //殺したプレイヤーに通知
                ChatUtility.sendMessage(killer, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY
                        + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for killing " + RouteAPI.getColoredName(dead));

                //死んだプレイヤーに通知
                ChatUtility.sendMessage(dead, ChatColor.DARK_AQUA + "You've lost "
                        + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for dying");

                killerGP.getPlayerData().addPoint(deadGP.getPlayerData().calculatedPoint());
                deadGP.getPlayerData().withdrawPoint(deadGP.getPlayerData().calculatedPoint());
            }
            deadGP.setRespawnLocation(dead.getLocation());
            e.setDroppedExp(0);
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + (profile.isInClan() ? profile.getClan().getStyleTag() + " " : "") + deadGP.getSGName());

            deadGP.respawn();

            if (gameTask.countAlive() <= 1) {
                gameTask.onEndGame();
            }

            if (gameTask.countAlive() <= 3) {
                for (GamePlayer gamePlayer : gameTask.getGamePlayers()) {
                    if (!gamePlayer.isWatching()) {
                        gamePlayer.getPlayerData().updateTop3(1);
                        gamePlayer.sendMessage(Style.YELLOW + "Top 3 Bonus!");
                        RouteAPI.getExperienceOfPlayer(gamePlayer.getPlayer()).addExp(10);
                    }
                }
            }

            if (deadGP.getOpeningChest() != null) {
                ChestPacketUtility.closeChestPacket(deadGP.getOpeningChest().getLocation());
                deadGP.setOpeningChest(null);
                dead.getWorld().playSound(dead.getLocation(), Sound.CHEST_CLOSE, 0.5F, 1.0F);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        GameTask gameTask = RouteSG.getGameTask();
        GameState state = gameTask.getState();
        GamePlayer gamePlayer = gameTask.getGamePlayer(event.getPlayer());
        if (gamePlayer == null) return;
        Block block = event.getBlock();
        if (block.getType() == Material.BOAT) {
            Block substract = block.getLocation().subtract(0, 1, 0).getBlock();
            if (substract.getType() != Material.WATER || substract.getType() != Material.WATER_LILY) {
                event.setCancelled(true);
                return;
            }
        }

        if (gamePlayer.isWatching()) {
            event.setCancelled(true);
            return;
        }
        if (state == GameState.LiveGame || state == GameState.DeathMatch) {
            if (block.getType() == Material.FIRE || block.getType() == Material.CAKE) {
                return;
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        GameTask gameTask = RouteSG.getGameTask();
        GameState state = gameTask.getState();
        GamePlayer gamePlayer = gameTask.getGamePlayer(event.getPlayer());
        if (gamePlayer == null) return;
        if (gamePlayer.isWatching()) {
            event.setCancelled(true);
            return;
        }
        if (state == GameState.LiveGame || state == GameState.DeathMatch) {
            Block block = event.getBlock();
            Material type = block.getType();
            if (type == Material.LONG_GRASS || type == Material.LEAVES || type == Material.LEAVES_2 || type == Material.HUGE_MUSHROOM_1 || type == Material.HUGE_MUSHROOM_2 || type == Material.VINE ||
                    type == Material.DEAD_BUSH || type == Material.YELLOW_FLOWER || type == Material.RED_ROSE || type == Material.CAKE) {
                return;
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.getRecipe().getResult().getType() == Material.FLINT_AND_STEEL) {
            event.setCurrentItem(ItemUtility.createFlintAndSteel());
        } else if (event.getRecipe().getResult().getType() == Material.BUCKET) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() instanceof Player) {
            Player player = (Player) event.getAttacker();
            GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
            if (gamePlayer == null) return;
            if (gamePlayer.isWatching()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            Player player = (Player) event.getEntered();
            GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
            if (gamePlayer == null) return;
            if (gamePlayer.isWatching()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplosion(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            ItemStack itemStack = player.getItemInHand();
            if (itemStack.getType() == Material.AIR) return;
            if (itemStack.getType() == Material.FLINT_AND_STEEL) {
                GameTask gameTask = RouteSG.getGameTask();
                GameState state = RouteSG.getGameTask().getState();
                if (state == GameState.LiveGame) {
                    if (gameTask.getTime() > gameTask.getGracePeriodTimeSec()) {
                        event.setCancelled(true);
                        player.sendMessage(Style.RED + "PvP has been disabling. Will be enabled in " + TimeUtility.timeFormat(gameTask.getGracePeriodTimeSec()));
                        return;
                    }
                }
                if (itemStack.getDurability() < 61) {
                    itemStack.setDurability((short) 61);
                }
            }
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
        if (gamePlayer == null) return;
        if (gamePlayer.isWatching()) {
            if (event.getBlock().getType() == Material.FIRE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location location = event.getTo();
        CraftWorld cw = (CraftWorld) location.getWorld();
        cw.loadChunk(location.getBlockX(), location.getBlockZ());
        cw.refreshChunk(location.getBlockX(), location.getBlockZ());
    }

    @EventHandler
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof Player) {
            GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer((Player) event.getEntity());
            if (gamePlayer == null) return;
            if (gamePlayer.isWatching()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = RouteAPI.getProfileByPlayer(event.getPlayer());
        if (RouteSG.getGameTask().getGameConfig().isCustomSG()) {
            if (RouteSG.getGameTask().getHost() == player.getUniqueId()) {
                event.setFormat((profile.isInClan() ? profile.getClan().getStyleTag() + " " : "") + Style.WHITE + "(" + RouteAPI.getExperienceOfPlayer(player).getLevel() + Style.WHITE + ") " + Style.DARK_GRAY + "[" + Style.LIGHT_PURPLE + Style.BOLD + "Host" + Style.DARK_GRAY + "] " + RouteSG.getGameTask().getGamePlayer(player).getSGName() + ChatColor.GRAY + ": " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
                return;
            }
            if (RouteSG.getInstance().getObserverPlayers().contains(player.getName().toLowerCase())) {
                event.setFormat((profile.isInClan() ? profile.getClan().getStyleTag() + " " : "") + Style.WHITE + "(" + RouteAPI.getExperienceOfPlayer(player).getLevel() + Style.WHITE + ") " + Style.DARK_GRAY + "[" + Style.LIGHT_PURPLE + Style.BOLD + "Observer" + Style.DARK_GRAY + "] " + RouteSG.getGameTask().getGamePlayer(player).getSGName() + ChatColor.GRAY + ": " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
                return;
            }
        }
        if (RouteSG.getGameTask().getGamePlayer(player).isWatching()) {
            event.setCancelled(true);
            for (GamePlayer gamePlayer : RouteSG.getGameTask().getGamePlayers()) {
                if (RouteSG.getGameTask().getState() != GameState.EndGame) {
                    if (gamePlayer.isWatching()) {
                        gamePlayer.getPlayer().sendMessage((profile.isInClan() ? profile.getClan().getStyleTag() + " " : "") + Style.WHITE + "(" + RouteAPI.getExperienceOfPlayer(player).getLevel() + Style.WHITE + ") " + ChatColor.DARK_RED + "SPEC" + ChatColor.DARK_GRAY + "|" + RouteSG.getGameTask().getGamePlayer(player).getSGName() + ChatColor.GRAY + ": " + ChatColor.RESET + event.getMessage());
                    }
                } else {
                    gamePlayer.getPlayer().sendMessage((profile.isInClan() ? profile.getClan().getStyleTag() + " " : "") + Style.WHITE + "(" + RouteAPI.getExperienceOfPlayer(player).getLevel() + Style.WHITE + ") " + ChatColor.DARK_RED + "SPEC" + ChatColor.DARK_GRAY + "|" + RouteSG.getGameTask().getGamePlayer(player).getSGName() + ChatColor.GRAY + ": " + ChatColor.RESET + event.getMessage());
                }
            }
        } else {
            event.setFormat((profile.isInClan() ? profile.getClan().getStyleTag() + " " : "") + Style.WHITE + "(" + RouteAPI.getExperienceOfPlayer(player).getLevel() + Style.WHITE + ") " + RouteSG.getGameTask().getGamePlayer(player).getSGName() + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE + event.getMessage());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (RouteSG.getGameTask().getState() == GameState.Lobby || RouteSG.getGameTask().getState() == GameState.EndGame) {
            if (event.getMessage().contains("kill")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
            if (gamePlayer == null) return;
            if (player.getGameMode() != GameMode.CREATIVE) {
                if (RouteSG.getGameTask().getState() == GameState.Lobby || gamePlayer.isWatching()) {
                    Inventory inv = event.getClickedInventory();
                    Inventory playerInv = player.getInventory();
                    if (inv == null || playerInv == null) return;
                    if (inv.equals(playerInv)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);

        if (gamePlayer.isWatching() && event.getRightClicked() instanceof Player && player.getItemInHand() != null) {
            final Player target = (Player) event.getRightClicked();

            if (player.getItemInHand().getType() == Material.STICK &&
                    player.getItemInHand().hasItemMeta() &&
                    player.getItemInHand().getItemMeta().hasDisplayName() &&
                    player.getItemInHand().getItemMeta().getDisplayName().contains("View Inventory")) {
                new ViewPlayerMenu(target).openMenu(player);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
        if (gamePlayer != null) {
            if (!gamePlayer.isWatching()) {
                if (!gamePlayer.isAllowMovement()) {
                    Location from = event.getFrom();
                    double xfrom = event.getFrom().getX();
                    double zfrom = event.getFrom().getZ();
                    double xto = event.getTo().getX();
                    double zto = event.getTo().getZ();
                    if (!(xfrom == xto && zfrom == zto)) {
                        player.teleport(from);
                    }
                }
            }
        }
    }
}
