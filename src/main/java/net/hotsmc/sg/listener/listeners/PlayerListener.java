package net.hotsmc.sg.listener.listeners;

import net.hotsmc.core.HotsCore;
import net.hotsmc.sg.database.PlayerData;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.game.*;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.ItemUtility;
import net.hotsmc.sg.utility.PlayerUtility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().clear();
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
        gamePlayer.startScoreboard(playerData.isSidebarMinimize());
        gamePlayer.setPlayerData(playerData);
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
        if (state == GameState.LiveGame || state == GameState.PreDeathMatch || state == GameState.DeathMatch) {
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
            gamePlayer.disableWatching();
        }
        //生きているプレイヤーだったら
        if (!gamePlayer.isWatching() && state != GameState.Lobby) {
            World world = Bukkit.getWorld(HSG.getGameTask().getCurrentMap().getDefaultSpawn().getWorld().getName());
            Location location = player.getLocation();
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    world.dropItemNaturally(location, itemStack);
                }
            }
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                }
            }
            PlayerUtility.clearEffects(player);
            //world.strikeLightningEffect(location);
            //ChatUtility.broadcast(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + HotsCore.getHotsPlayer(player).getColorName());
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
            if (state == GameState.Lobby || state == GameState.PreGame || state == GameState.PreDeathMatch || state == GameState.EndGame) {
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
            if (state == GameState.Lobby || state == GameState.PreGame || state == GameState.PreDeathMatch) {
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

        event.setRespawnLocation(gameTask.getCurrentMap().getDefaultSpawn());

        GamePlayer gamePlayer = gameTask.getGamePlayer(player);

        if (player.isOnline()) {
            gamePlayer.enableWatching();
            ChatUtility.broadcast("" + ChatColor.GREEN + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + gameTask.countAlive() + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + " tributes remain!");
            ChatUtility.broadcast(ChatColor.GREEN + "There are " + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + gameTask.countWatching() + ChatColor.DARK_GRAY + "]"
                    + ChatColor.GREEN + " spectators watching the game.");
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        //プレイヤー取得
        Player dead = e.getEntity();
        Player killer = dead.getKiller();
        GameTask gameTask = HSG.getGameTask();
        BountyManager bountyManager = gameTask.getBountyManager();

        //自滅の場合①
        if (killer == null) {
            Location location = dead.getLocation();
            location.getWorld().strikeLightningEffect(location);
            GamePlayer deadGP = gameTask.getGamePlayer(dead);
            e.setDroppedExp(0);
            deadGP.respawn();
            deadGP.getPlayerData().withdrawPoint(deadGP.getPlayerData().calculatedPoint());
            ChatUtility.sendMessage(dead, ChatColor.DARK_AQUA + "You've lost " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA  + " points for dying");
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + HotsCore.getHotsPlayer(dead).getColorName());

            //Bountyされていたら
            if(bountyManager.wasBountiedMe(deadGP)){
                List<BountyData> bountyData = bountyManager.getBounties(deadGP);
                for(BountyData bountyData1 : bountyData) {
                    GamePlayer sender = bountyData1.getSender();
                    if (sender.getPlayer().isOnline()) {
                        int points = bountyData1.getPoints();

                        //賭けたプレイヤーから賭けたポイント分引く
                        bountyData1.getSender().getPlayerData().withdrawPoint(points);

                        ChatUtility.sendMessage(sender, ChatColor.DARK_AQUA + "You've lost " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + points + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " points for dying " + HotsCore.getHotsPlayer(dead).getColorName());
                    }
                }
            }
            return;
        }
        //自滅の場合②
        if (killer.equals(dead)) {
            Location location = dead.getLocation();
            location.getWorld().strikeLightningEffect(location);
            GamePlayer deadGP = gameTask.getGamePlayer(dead);
            e.setDroppedExp(0);
            deadGP.getPlayerData().withdrawPoint(deadGP.getPlayerData().calculatedPoint());
            ChatUtility.sendMessage(dead, ChatColor.DARK_AQUA + "You've lost " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA  + " points for dying");
            deadGP.respawn();
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + HotsCore.getHotsPlayer(dead).getColorName());

            if(bountyManager.wasBountiedMe(deadGP)) {
                List<BountyData> bountyData = bountyManager.getBounties(deadGP);
                for (BountyData bountyData1 : bountyData) {
                    GamePlayer sender = bountyData1.getSender();
                    if (sender.getPlayer().isOnline()) {
                        int points = bountyData1.getPoints();

                        //賭けたプレイヤーから賭けたポイント分引く
                        bountyData1.getSender().getPlayerData().withdrawPoint(points);

                        ChatUtility.sendMessage(sender, ChatColor.DARK_AQUA + "You've lost " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + points + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " points for dying " + HotsCore.getHotsPlayer(dead).getColorName());
                    }
                }
            }
        } else {
            Location location = dead.getLocation();
            location.getWorld().strikeLightningEffect(location);
            GamePlayer deadGP = gameTask.getGamePlayer(dead);
            GamePlayer killerGP = gameTask.getGamePlayer(killer);
            killerGP.getPlayerData().updateKill(1);
            killerGP.getPlayerData().addPoint(deadGP.getPlayerData().calculatedPoint());
            deadGP.getPlayerData().withdrawPoint(deadGP.getPlayerData().calculatedPoint());
            ChatUtility.sendMessage(killer, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY
                    + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA  + " points for killing " + HotsCore.getHotsPlayer(dead).getColorName());
            ChatUtility.sendMessage(dead, ChatColor.DARK_AQUA + "You've lost " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + deadGP.getPlayerData().calculatedPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA  + " points for dying");
            e.setDroppedExp(0);
            deadGP.respawn();
            e.setDeathMessage(ChatColor.GOLD + "A cannon be heard in the distance in memorial for " + HotsCore.getHotsPlayer(dead).getColorName());

            //死んだプレイヤーサイド
            if(bountyManager.wasBountiedMe(deadGP)) {
                List<BountyData> bountyData = bountyManager.getBounties(deadGP);
                for (BountyData bountyData1 : bountyData) {
                    GamePlayer sender = bountyData1.getSender();
                    if (sender.getPlayer().isOnline()) {
                        int points = bountyData1.getPoints();

                        //賭けたプレイヤーから賭けたポイント分引く
                        bountyData1.getSender().getPlayerData().withdrawPoint(points);

                        ChatUtility.sendMessage(sender, ChatColor.DARK_AQUA + "You've lost " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + points + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " points for dying " + HotsCore.getHotsPlayer(dead).getColorName());
                    }
                }
            }

            //殺したプレイヤーサイド
            if(bountyManager.wasBountiedMe(killerGP)) {
                List<BountyData> bountyData = bountyManager.getBounties(killerGP);
                for (BountyData bountyData1 : bountyData) {
                    GamePlayer sender = bountyData1.getSender();
                    if (sender.getPlayer().isOnline()) {
                        int points = bountyData1.getPoints();

                        //8％
                        int addPoints = (int) (points * 1.08) - points;

                        //賭けたプレイヤーに賭けたポイント分の8％あげる
                        bountyData1.getSender().getPlayerData().addPoint(addPoints);

                        ChatUtility.sendMessage(sender, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + points + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " points for killing " + HotsCore.getHotsPlayer(killer).getColorName());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
        if (gamePlayer.isFrozen()) {
            Location from = event.getFrom();
            double fromX = from.getX();
            double fromZ = from.getZ();

            Location to = event.getTo();
            double toX = to.getX();
            double toZ = to.getZ();

            if (fromX != toX || fromZ != toZ) {
                player.teleport(from);
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
            if (event.getBlock().getType() == Material.FIRE) {
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
                    type == Material.DEAD_BUSH || type == Material.YELLOW_FLOWER || type == Material.RED_ROSE) {
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
    public void onEnter(VehicleEnterEvent event){
        if(event.getEntered() instanceof Player){
            Player player = (Player) event.getEntered();
            GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
            if (gamePlayer == null) return;
            if (gamePlayer.isWatching()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = HSG.getGameTask().getGamePlayer(player);
        if (gamePlayer == null) return;
        if (gamePlayer.isWatching()) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                event.setCancelled(true);
            }
        }
    }
}