package net.hotsmc.sg.listener.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.hotsmc.core.HotsCore;
import net.hotsmc.core.gui.ClickActionItem;
import net.hotsmc.core.other.Style;
import net.hotsmc.core.player.PlayerRank;
import net.hotsmc.sg.database.PlayerData;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.*;
import net.hotsmc.sg.hotbar.PlayerHotbar;
import net.hotsmc.sg.team.TeamType;
import net.hotsmc.sg.utility.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Objects;

public class PlayerListener implements Listener {

    private Field fieldPlayerConnection;
    private Method sendPacket;
    private Constructor<?> packetVelocity;

    public PlayerListener()
    {
        try
        {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
            Class<?> packetVelocityClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutEntityVelocity");
            Class<?> playerConnectionClass = Class.forName("net.minecraft.server." + version + ".PlayerConnection");

            this.fieldPlayerConnection = entityPlayerClass.getField("playerConnection");
            this.sendPacket = playerConnectionClass.getMethod("sendPacket", new Class[] { packetVelocityClass.getSuperclass() });
            this.packetVelocity = packetVelocityClass.getConstructor(new Class[] { Integer.TYPE, Double.TYPE, Double.TYPE, Double.TYPE });
        }
        catch (ClassNotFoundException|NoSuchFieldException|SecurityException|NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        final GameTask game = HSG.getGameTask();
        final GameState state = game.getState();

        if (state == GameState.PreGame) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + HSG.getSettings().getServerName() + " is preparing the game");
            return;
        }
        if (state == GameState.EndGame) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + HSG.getSettings().getServerName() + " is ending the game");
        }


        //ここからCustomSGのログイン処理
        if (game.getGameConfig().isCustomSG()) {
            if (game.getHost() != null) {
                if (game.getHost() == event.getUniqueId()) {
                    return;
                }
            }

            PlayerRank playerRank = PlayerRank.valueOf(
                    Objects.requireNonNull(
                            HotsCore.getMongoConnection().getPlayers().find
                                    (net.hotsmc.core.utility.MongoUtility.find(
                                            "UUID", event.getUniqueId().toString())).first()).getString(
                            "RANK"));

            //Lobby状態でサーバーのプレイヤー数が0人でホストがいなかったらログイン許可
            if (state == GameState.Lobby && Bukkit.getServer().getOnlinePlayers().length < 1 && game.getHost() == null) {
                return;
            }

            if(state == GameState.Lobby) {
                if (!HSG.getInstance().getWhitelistedPlayers().contains(event.getName().toLowerCase())) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Style.AQUA + "You are not registered to roster.");
                }
            }else {
                if(game.isSpectateRosterOnly()) {
                    if (!HSG.getInstance().getWhitelistedPlayers().contains(event.getName().toLowerCase())) {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Style.AQUA + "Roster only to spectate.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
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
        GameTask gameTask = HSG.getGameTask();
        GameConfig gameConfig = gameTask.getGameConfig();
        GameState state = gameTask.getState();
        if (state == GameState.Lobby) {
            if (gameConfig.isCustomSG()) {
                if (Bukkit.getServer().getOnlinePlayers().length <= 1) {
                    player.sendMessage(Style.YELLOW + "Require: /host");
                    player.sendMessage(Style.YELLOW + "Add to roster: /register <player1> <player2> <player3>...");
                    player.sendMessage(Style.YELLOW + "Remove from roster: /unregister <player>");
                    player.sendMessage(Style.YELLOW + "Change Host: /givehost <player>");
                    player.sendMessage(Style.YELLOW + "Show Roster: /roster");
                    player.sendMessage(Style.YELLOW + "Add Observer: /observer <player1> <player2> <player3>...");
                    player.sendMessage(Style.YELLOW + "Add to team: /team <team> <player1> <player2> <player3>...");
                    player.sendMessage(Style.YELLOW + "Teams: " + Style.AQUA + "AQUA" + Style.GRAY + ", " + Style.RED + "RED");
                }
                if(HSG.getInstance().getObserverPlayers().contains(player.getName().toLowerCase())){
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
                out.writeUTF(HSG.getSettings().getServerName());
                out.writeUTF("Not Hosting");
                player.sendPluginMessage(HSG.getInstance(), "BungeeCord", out.toByteArray());
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
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameTask gameTask = HSG.getGameTask();
        GameState state = gameTask.getState();
        GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
        if (gamePlayer == null) return;
        //CustomSGでLobbyでホストが抜けたらホストを解除して全員Hubに転送
        if (gameTask.getGameConfig().isCustomSG()) {
            if (state == GameState.Lobby) {
                if (gameTask.getHost() == player.getUniqueId()) {
                    gameTask.setHost(null);
                    gameTask.setHostWithPlay(false);
                    gameTask.setSponsor(false);
                    gameTask.setGracePeriod3Minutes(false);
                    gameTask.setTeamFight(false);
                    gameTask.setSpectateRosterOnly(false);
                    HSG.getInstance().getWhitelistedPlayers().clear();
                    HSG.getInstance().getObserverPlayers().clear();
                    for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                        ChatUtility.sendMessage(all, Style.HORIZONTAL_SEPARATOR);
                        ChatUtility.sendMessage(all, "Host不在のためCustomSGは中止されました");
                        ChatUtility.sendMessage(all, Style.HORIZONTAL_SEPARATOR);
                        HotsCore.getInstance().getBungeeChannelApi().connect(all, "hub");
                    }
                }
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("CustomSG-Update-Host");
                out.writeUTF(HSG.getSettings().getServerName());
                out.writeUTF("Not Hosting");
                player.sendPluginMessage(HSG.getInstance(), "BungeeCord", out.toByteArray());
            }
        }

        if (gamePlayer.isWatching()) {
            PlayerUtility.clearEffects(player);
        }

        //生きているプレイヤーだったら

        if (!gamePlayer.isWatching()) {
            GamePlayerData gamePlayerData = HSG.getGameTask().getGamePlayerData(gamePlayer.getName());
            if(gamePlayerData != null) {
                gamePlayerData.setAlive(false);
                gamePlayerData.applyPlaceRank();
            }
            if (state == GameState.PreGame) {
                Location location = player.getLocation();
                location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
            }
            if (state == GameState.LiveGame || state == GameState.PreDeathmatch || state == GameState.DeathMatch) {
                gamePlayer.setWatching(true);
                World world = Bukkit.getWorld(HSG.getGameTask().getCurrentMap().getDefaultSpawn().getWorld().getName());
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
                if(!gameTask.getGameConfig().isCustomSG()) {
                    ChatUtility.sendMessage(gamePlayer, ChatColor.DARK_AQUA + "You've lost "
                            + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + gamePlayer.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for dying");
                    gamePlayer.getPlayerData().withdrawPoint(gamePlayer.getPlayerData().calculatedPoint());
                }
                PlayerUtility.clearEffects(player);
                location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
                if (HotsCore.getHotsPlayer(player) != null) {
                    ChatUtility.broadcast(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + HotsCore.getHotsPlayer(player).getColorName());
                }
                if (gameTask.countAlive() <= 3) {
                    for (GamePlayer gamePlayer1 : gameTask.getGamePlayers()) {
                        if (!gamePlayer1.isWatching()) {
                            gamePlayer1.getPlayerData().updateTop3(1);
                        }
                    }
                }
            }

            if (gameTask.getState() != GameState.EndGame && gameTask.getState() != GameState.Lobby && gameTask.countAlive() <= 1) {
                gameTask.onEndGame();
            }
        }

        if(gamePlayer.getInTeam() != null){
            gamePlayer.getInTeam().getPlayers().remove(gamePlayer);
        }

        HSG.getGameTask().removeGamePlayer(gamePlayer);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
        if (gamePlayer == null) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemStack = player.getItemInHand();
            if (itemStack == null || itemStack.getType() == Material.AIR) return;
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
            GameTask gameTask = HSG.getGameTask();
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
        GameTask gameTask = HSG.getGameTask();
        if (gameTask.getGamePlayer(event.getPlayer()).isWatching() || gameTask.getState() == GameState.Lobby) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        GameTask gameTask = HSG.getGameTask();
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
            Player player = (Player) event.getEntity();
            GameTask gameTask = HSG.getGameTask();
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
        GameTask gameTask = HSG.getGameTask();
        GameState state = gameTask.getState();
        if (event.getDamager() instanceof FishHook && ((FishHook) event.getDamager()).getShooter() instanceof Player) {
            Player damager = (Player) ((FishHook) event.getDamager()).getShooter();
            GamePlayer damagerGP = gameTask.getGamePlayer(damager);
            Player damaged = (Player) event.getEntity();
            GamePlayer damagedGP = gameTask.getGamePlayer(damaged);
            if(state == GameState.Lobby) {
                if (!damagerGP.getPlayerData().isLobbyPvP()) {
                    event.setCancelled(true);
                    return;
                }
                if (!damagedGP.getPlayerData().isLobbyPvP()) {
                    event.setCancelled(true);
                    return;
                }
            }
            if(state != GameState.Lobby){
                if(HSG.getGameTask().isGracePeriod3Minutes()){
                    if (gameTask.getTime() > 1620) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            double horizontalMultiplier = HSG.getSettings().getFisihingrod_horizontalMultiplier();
            double verticalMultiplier = HSG.getSettings().getFisihingrod_verticalMultiplier();
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
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            GamePlayer damagerGP = gameTask.getGamePlayer(damager);
            GamePlayer damagedGP = gameTask.getGamePlayer(damaged);
            if (state == GameState.PreGame || state == GameState.PreDeathmatch) {
                event.setCancelled(true);
                return;
            }
            if (state == GameState.LiveGame) {
                if (gameTask.isGracePeriod3Minutes()) {
                    if (gameTask.getTime() > 1620) {
                        event.setCancelled(true);
                        ChatUtility.sendMessage(damager, Style.RED + "PvP is disabled. Will be enabled in 27:00.");
                        return;
                    }
                }
            }
            if (state == GameState.Lobby) {
                if (!damagedGP.getPlayerData().isLobbyPvP()) {
                    event.setCancelled(true);
                    return;
                }
                if (!damagerGP.getPlayerData().isLobbyPvP()) {
                    event.setCancelled(true);
                    return;
                }
                event.setDamage(0D);
                return;
            }
            if (damagedGP.isWatching() || damagerGP.isWatching()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamageByBlock(EntityDamageByBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            GameTask gameTask = HSG.getGameTask();
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
        GameTask gameTask = HSG.getGameTask();
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
        GameTask gameTask = HSG.getGameTask();

        if(gameTask.isTeamFight()){
            if(gameTask.getGamePlayer(dead).getInTeam() != null) {
                gameTask.getGamePlayer(dead).setAlive(false);
                if(gameTask.getGamePlayer(dead).getInTeam().getAlivePlayers().size() <= 0) {
                    if (gameTask.getGamePlayer(dead).getInTeam().getTeamType() == TeamType.RED) {
                        for (String red : TeamType.RED.getLogo()) {
                            ChatUtility.normalBroadcast(red);
                        }
                    } else if (gameTask.getGamePlayer(dead).getInTeam().getTeamType() == TeamType.AQUA) {
                        for (String aqua : TeamType.AQUA.getLogo()) {
                            ChatUtility.normalBroadcast(aqua);
                        }
                    }
                    for(Player player1 : Bukkit.getServer().getOnlinePlayers()){
                        player1.playSound(player1.getLocation(), Sound.ENDERDRAGON_DEATH, 1F, 1);
                        killer.getLocation().getWorld().createExplosion(killer.getLocation(), 2, false);
                    }
                }
            }
        }

        //自滅の場合①
        if (killer == null) {
            Location location = dead.getLocation();
            location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
            GamePlayer deadGP = gameTask.getGamePlayer(dead);
            deadGP.setWatching(true);
            deadGP.setRespawnLocation(dead.getLocation());
            e.setDroppedExp(0);
            if(!gameTask.getGameConfig().isCustomSG()) {
                ChatUtility.sendMessage(dead, ChatColor.DARK_AQUA + "You've lost " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for dying");
                deadGP.getPlayerData().withdrawPoint(deadGP.getPlayerData().calculatedPoint());
            }
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + deadGP.getSGName());

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

            GamePlayerData gamePlayerData = HSG.getGameTask().getGamePlayerData(dead.getName());
            gamePlayerData.setAlive(false);
            gamePlayerData.applyPlaceRank();
            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);
            dead.sendMessage(Style.AQUA + "You have been placed #" + gamePlayerData.getPlaceRank() + "/#" + HSG.getGameTask().getGamePlayerData().size());
            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);

            return;
        }
        //自滅の場合②
        if (killer.equals(dead)) {
            Location location = dead.getLocation();
            location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
            GamePlayer deadGP = gameTask.getGamePlayer(dead);
            deadGP.setWatching(true);
            deadGP.setRespawnLocation(dead.getLocation());
            e.setDroppedExp(0);
            ChatUtility.sendMessage(dead, ChatColor.DARK_AQUA + "You've lost " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for dying");
            deadGP.getPlayerData().withdrawPoint(deadGP.getPlayerData().calculatedPoint());
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + deadGP.getSGName());

            deadGP.respawn();

            if (gameTask.countAlive() <= 1) {
                gameTask.onEndGame();
            }
            if(!gameTask.getGameConfig().isCustomSG()) {
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

            GamePlayerData gamePlayerData = HSG.getGameTask().getGamePlayerData(dead.getName());
            gamePlayerData.setAlive(false);
            gamePlayerData.applyPlaceRank();
            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);
            dead.sendMessage(Style.AQUA + "You have been placed #" + gamePlayerData.getPlaceRank() + "/#" + HSG.getGameTask().getGamePlayerData().size());
            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);
        } else {
            Location location = dead.getLocation();
            location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
            GamePlayer deadGP = gameTask.getGamePlayer(dead);
            deadGP.setWatching(true);
            GamePlayer killerGP = gameTask.getGamePlayer(killer);

            GamePlayerData deadData = HSG.getGameTask().getGamePlayerData(killer.getName());
            deadData.setAlive(false);
            deadData.applyPlaceRank();
            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);
            dead.sendMessage(Style.AQUA + "You have been placed #" + deadData.getPlaceRank() + "/#" + HSG.getGameTask().getGamePlayerData().size());
            dead.sendMessage(Style.HORIZONTAL_SEPARATOR);

            GamePlayerData gamePlayerData = HSG.getGameTask().getGamePlayerData(killer.getName());
            gamePlayerData.addKill();
            killer.sendMessage(Style.AQUA + "You have " + gamePlayerData.getKills() + " kills.");

            HSG.getGameTask().getGamePlayerData().sort(new Comparator<GamePlayerData>() {
                public int compare(GamePlayerData o1, GamePlayerData o2) {
                    return o1.getKills() > o2.getKills() ? -1 : 1;
                }
            });

            if(!gameTask.getGameConfig().isCustomSG()) {
                killerGP.getPlayerData().updateKill(1);

                //殺したプレイヤーに通知
                ChatUtility.sendMessage(killer, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY
                        + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for killing " + HotsCore.getHotsPlayer(dead).getColorName());

                //死んだプレイヤーに通知
                ChatUtility.sendMessage(dead, ChatColor.DARK_AQUA + "You've lost "
                        + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for dying");

                killerGP.getPlayerData().addPoint(deadGP.getPlayerData().calculatedPoint());
                deadGP.getPlayerData().withdrawPoint(deadGP.getPlayerData().calculatedPoint());
            }
            deadGP.setRespawnLocation(dead.getLocation());
            e.setDroppedExp(0);
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + deadGP.getSGName());

            deadGP.respawn();

            if (gameTask.countAlive() <= 1) {
                gameTask.onEndGame();
            }

            if (gameTask.countAlive() <= 3) {
                for (GamePlayer gamePlayer : gameTask.getGamePlayers()) {
                    if (!gamePlayer.isWatching()) {
                        gamePlayer.getPlayerData().updateTop3(1);
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
        GameTask gameTask = HSG.getGameTask();
        GameState state = gameTask.getState();
        GamePlayer gamePlayer = gameTask.getGamePlayer(event.getPlayer());
        if (gamePlayer == null) return;
        Block block = event.getBlock();
        if(block.getType() == Material.BOAT){
            Block substract = block.getLocation().subtract(0, 1, 0).getBlock();
            if(substract.getType() != Material.WATER ||substract.getType() != Material.WATER_LILY){
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
        GameTask gameTask = HSG.getGameTask();
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
        }
        else if(event.getRecipe().getResult().getType() == Material.BUCKET){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() instanceof Player) {
            Player player = (Player) event.getAttacker();
            GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
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
            GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
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
        if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            Player player = event.getPlayer();
            ItemStack itemStack = player.getItemInHand();
            if (itemStack.getType() == Material.AIR) return;
            if (itemStack.getType() == Material.FLINT_AND_STEEL) {
                if (itemStack.getDurability() < 61) {
                    itemStack.setDurability((short) 61);
                }
            }
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
        if (gamePlayer == null) return;
        if (gamePlayer.isWatching())
            if (event.getBlock().getType() == Material.FIRE) {
                event.setCancelled(true);
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
            GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer((Player) event.getEntity());
            if (gamePlayer == null) return;
            if (gamePlayer.isWatching()) {
                event.setCancelled(true);
            }
        }
    }
}