package net.hotsmc.sg.listener.listeners;

import net.hotsmc.core.HotsCore;
import net.hotsmc.core.ServerType;
import net.hotsmc.core.gui.ClickActionItem;
import net.hotsmc.sg.database.data.PlayerData;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.*;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.ChestPacketUtility;
import net.hotsmc.sg.utility.ItemUtility;
import net.hotsmc.sg.utility.PlayerUtility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Boat;
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

public class PlayerListener implements Listener {

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (HSG.getGameTask().getState() == GameState.PreGame) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + HSG.getSettings().getServerName() + " is preparing the game");
            return;
        }
        if (HSG.getGameTask().getState() == GameState.EndGame) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + HSG.getSettings().getServerName() + " is ending the game");
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
        gameTask.addGamePlayer(gamePlayer);
        GameConfig gameConfig = gameTask.getGameConfig();
        GameState state = gameTask.getState();
        if (state == GameState.Lobby) {
            player.teleport(gameConfig.getLobbyLocation());
            player.setAllowFlight(false);
            player.setFlying(false);
            if (gameTask.isTimerFlag()) {
                gameTask.getVoteManager().send(player);
            }
        }
        if (state == GameState.LiveGame || state == GameState.PreDeathmatch || state == GameState.DeathMatch) {
            player.teleport(gameTask.getCurrentMap().getDefaultSpawn());
            //観戦者同士互いに見えないようにする
            for (GamePlayer gp : gameTask.getGamePlayers()) {
                if (gp.isWatching()) {
                    player.hidePlayer(gp.getPlayer());
                    gp.getPlayer().hidePlayer(player);
                }
            }
            GamePlayer gamePlayer1 = gameTask.getGamePlayer(player);
            gamePlayer1.enableWatching();
            gamePlayer1.setSpectateItem();
            ChatUtility.sendMessage(gamePlayer1, ChatColor.WHITE + "If you want to spectate player: " + ChatColor.YELLOW + "/spec <player>");
            ChatUtility.sendMessage(gamePlayer1, ChatColor.WHITE + "If you want to send sponsor to player: " + ChatColor.YELLOW + "/sponsor <player>");
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
        if (gamePlayer.isWatching()) {
            PlayerUtility.clearEffects(player);
        }
        //生きているプレイヤーだったら
        if (!gamePlayer.isWatching()) {
            if(state == GameState.PreGame){
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
                ChatUtility.sendMessage(gamePlayer, ChatColor.DARK_AQUA + "You've lost "
                        + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + gamePlayer.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for dying");
                gamePlayer.getPlayerData().withdrawPoint(gamePlayer.getPlayerData().calculatedPoint());
                PlayerUtility.clearEffects(player);
                location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
                ChatUtility.broadcast(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + HotsCore.getHotsPlayer(player).getColorName());
                if (gameTask.getState() != GameState.EndGame && gameTask.countAlive() <= 1) {
                    gameTask.onEndGame();
                }
                if (gameTask.countAlive() <= 3) {
                    for (GamePlayer gamePlayer1 : gameTask.getGamePlayers()) {
                        if (!gamePlayer1.isWatching()) {
                            gamePlayer1.getPlayerData().updateTop3(1);
                        }
                    }
                }
            }
        }
        HSG.getGameTask().removeGamePlayer(gamePlayer);
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
        if (gameTask.getState() == GameState.Lobby) {
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
            }
            if (state == GameState.Lobby || state == GameState.PreGame || state == GameState.PreDeathmatch || state == GameState.EndGame) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        //観戦者ダメージ無効化
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            GameTask gameTask = HSG.getGameTask();
            GameState state = gameTask.getState();
            if (state == GameState.Lobby || state == GameState.PreGame || state == GameState.PreDeathmatch) {
                event.setCancelled(true);
                return;
            }
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            GamePlayer damagerGP = gameTask.getGamePlayer(damager);
            GamePlayer damagedGP = gameTask.getGamePlayer(damaged);
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
        gamePlayer.setSpectateItem();
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

        //自滅の場合①
        if (killer == null) {
            Location location = dead.getLocation();
            location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
            GamePlayer deadGP = gameTask.getGamePlayer(dead);
            deadGP.setWatching(true);
            deadGP.setRespawnLocation(dead.getLocation());
            e.setDroppedExp(0);
            ChatUtility.sendMessage(dead, ChatColor.DARK_AQUA + "You've lost " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for dying");
            deadGP.getPlayerData().withdrawPoint(deadGP.getPlayerData().calculatedPoint());
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + HotsCore.getHotsPlayer(dead).getColorName());

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
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + HotsCore.getHotsPlayer(dead).getColorName());

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
        } else {
            Location location = dead.getLocation();
            location.getWorld().strikeLightningEffect(location.add(0, 3, 0));
            GamePlayer deadGP = gameTask.getGamePlayer(dead);
            deadGP.setWatching(true);
            GamePlayer killerGP = gameTask.getGamePlayer(killer);
            killerGP.getPlayerData().updateKill(1);

            //殺したプレイヤーに通知
            ChatUtility.sendMessage(killer, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY
                    + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for killing " + HotsCore.getHotsPlayer(dead).getColorName());

            //死んだプレイヤーに通知
            ChatUtility.sendMessage(dead, ChatColor.DARK_AQUA + "You've lost "
                    + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for dying");

            killerGP.getPlayerData().addPoint(deadGP.getPlayerData().calculatedPoint());
            deadGP.getPlayerData().withdrawPoint(deadGP.getPlayerData().calculatedPoint());
            deadGP.setRespawnLocation(dead.getLocation());
            e.setDroppedExp(0);
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + HotsCore.getHotsPlayer(dead).getColorName());

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
        if (gamePlayer.isWatching()) {
            event.setCancelled(true);
            return;
        }
        if (state == GameState.LiveGame || state == GameState.DeathMatch) {
            if (event.getBlock().getType() == Material.FIRE || event.getBlock().getType() == Material.CAKE) {
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