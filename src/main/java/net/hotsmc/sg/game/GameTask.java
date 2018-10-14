package net.hotsmc.sg.game;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.hotsmc.core.HotsCore;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.task.StrikeLightningTask;
import net.hotsmc.sg.utility.ChatUtility;
import net.hotsmc.sg.utility.FireworkGenerator;
import net.hotsmc.sg.utility.ServerUtility;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@Getter
@Setter
public class GameTask {

    private GameConfig gameConfig;
    private GameState state;
    private VoteManager voteManager;
    private ChestManager chestManager;
    private BountyManager bountyManager;
    private int time;
    private MapData currentMap = null;
    private List<GamePlayer> gamePlayers;
    private boolean timerFlag = false;

    private int circleSize = 57;


    public GameTask(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
        state = GameState.Lobby;
        time = gameConfig.getLobbyTime();
        gamePlayers = Lists.newArrayList();
        voteManager = new VoteManager();
        chestManager = new ChestManager();
        bountyManager = new BountyManager();
        chestManager.loadTierItems();
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(HSG.getInstance(), 0, 20);
    }

    /**
     * 一秒毎
     */
    public void tick() {
        ServerUtility.setMotd(state.name());

        //タイマー進行
        if (time >= 0 && timerFlag) {
            time--;
        }

        if (time <= 0) {
            if (state == GameState.Lobby) {
                onPreGame();
                return;
            }

            if (state == GameState.PreGame) {
                onLiveGame();
                return;
            }

            if (state == GameState.LiveGame) {
                onPreDeathMatch();
                return;
            }

            if (state == GameState.PreDeathMatch) {
                onDeathMatch();
                return;
            }

            if (state == GameState.DeathMatch) {
                onEndGame();
                return;
            }
            if (state == GameState.EndGame) {
                onLobby();
                return;
            }
        }

        //LobbyTask
        if (state == GameState.Lobby) {
            tickLobby();
        }

        if (state == GameState.PreGame) {
            tickPreGame();
        }

        //LiveGameTask
        if (state == GameState.LiveGame) {
            tickLiveGame();
        }

        if (state == GameState.PreDeathMatch) {
            tickPreDeathMatch();
        }

        if (state == GameState.DeathMatch) {
            tickDeathMatch();
        }

        //１０秒と残り5秒毎で呼び出される
        if (time <= 5 && time != 0 || time == 10) {
            countdownBroadcast();
        }
    }

    /**
     * ゲームプレイヤー追加
     *
     * @param gamePlayer
     */
    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayers.add(gamePlayer);
    }

    /**
     * @param gamePlayer
     */
    public void removeGamePlayer(GamePlayer gamePlayer) {
        gamePlayers.remove(gamePlayer);
    }

    /**
     * @param player
     * @return
     */
    public GamePlayer getGamePlayer(Player player) {
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getPlayer().getName().equals(player.getName())) {
                return gamePlayer;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public int countAlive() {
        int count = 0;
        for (GamePlayer gamePlayer : gamePlayers) {
            if (!gamePlayer.isWatching()) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return
     */
    public int countWatching() {
        int count = 0;
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.isWatching()) {
                count++;
            }
        }
        return count;
    }

    /**
     *
     */
    private void teleportToDeathmatchSpawn() {
        List<GamePlayer> alive = Lists.newArrayList();
        for (GamePlayer gamePlayer : gamePlayers) {
            if (!gamePlayer.isWatching()) {
                alive.add(gamePlayer);
            }
        }
        for (int i = 0; i < alive.size(); i++) {
            GamePlayer gamePlayer = alive.get(i);
            gamePlayer.teleport(getCurrentMap().getDeathmatchSpawns().get(i));
            gamePlayer.setFrozen(true);
        }
    }

    /**
     *
     */
    private void teleportToSpawn() {
        for (int i = 0; i < gamePlayers.size(); i++) {
            GamePlayer gamePlayer = gamePlayers.get(i);
            gamePlayer.teleport(getCurrentMap().getSpawns().get(i));
            gamePlayer.setFrozen(true);
            gamePlayer.getPlayer().getInventory().clear();
            gamePlayer.getPlayer().updateInventory();
        }
    }

    /**
     *
     */
    private void onPreGame() {
        voteManager.setVoting(false);
        for (GamePlayer gamePlayer : gamePlayers) {
            gamePlayer.setVoted(false);
            gamePlayer.getPlayerData().updatePlayed(1);
        }
        currentMap = voteManager.getDecidedMapData();
        ChatUtility.broadcast(ChatColor.YELLOW + "Loading " + currentMap.getName() + "...");
        currentMap.loadWorld();
        teleportToSpawn();
        ChatUtility.broadcast(ChatColor.YELLOW + "Map name" + ChatColor.DARK_GRAY + ": " + ChatColor.DARK_GREEN + currentMap.getName());
        ChatUtility.broadcast(ChatColor.DARK_RED + "Please wait " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + getGameConfig().getPregameTime() + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "seconds before the games begin.");
        ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + getGameConfig().getPregameTime() + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until the games begin!");
        chestManager.loadAllMapTier2Chest(currentMap);
        time = gameConfig.getPregameTime();
        state = GameState.PreGame;
    }

    /**
     *
     */
    private void onLiveGame() {
        for (GamePlayer gamePlayer : gamePlayers) {
            gamePlayer.setFrozen(false);
        }
        ChatUtility.broadcast(ChatColor.DARK_AQUA + "The games have begun!");
        ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "30" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "minutes until deathmatch!");
        Bukkit.getServer().setMaxPlayers(100);
        time = gameConfig.getLivegameTime();
        state = GameState.LiveGame;
    }

    /**
     *
     */
    private void onPreDeathMatch() {
        teleportToDeathmatchSpawn();
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.isWatching()) {
                gamePlayer.teleport(currentMap.getDefaultSpawn());
            }
        }
        ChatUtility.broadcast(ChatColor.DARK_RED + "Please allow " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + gameConfig.getPredeathmatchTime() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_RED + " seconds for all players to load the map.");
        time = gameConfig.getPredeathmatchTime();
        state = GameState.PreDeathMatch;
    }

    /**
     *
     */
    private void onDeathMatch() {
        for (GamePlayer gamePlayer : gamePlayers) {
            if (!gamePlayer.isWatching()) {
                gamePlayer.setFrozen(false);
            }
        }
        ChatUtility.broadcast(ChatColor.RED + "Fight to the death!");
        ChatUtility.broadcast("" + ChatColor.RED + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "3" + ChatColor.DARK_GRAY + "]" + ChatColor.RED + " minutes until the deathmatch ends!");
        setCircleSize(57);
        //3秒間に1ブロック狭める
        new BukkitRunnable() {
            @Override
            public void run() {
                if (HSG.getGameTask().getState() != GameState.DeathMatch || circleSize <= 0) {
                    this.cancel();
                    return;
                }
                circleSize--;
            }
        }.runTaskTimer(HSG.getInstance(), 0, 60);
        time = gameConfig.getDeathmatchTime();
        state = GameState.DeathMatch;
        //雷タスク
        for (GamePlayer gamePlayer : gamePlayers) {
            if (!gamePlayer.isWatching()) {
                if (gamePlayer.getPlayer() != null) {
                    new StrikeLightningTask(gamePlayer.getPlayer()).runTaskTimer(HSG.getInstance(), 0, 100);
                }
            }
        }
    }

    /**
     *
     */
    public void onEndGame() {
        time = gameConfig.getEndgameTime();
        state = GameState.EndGame;
        ChatUtility.broadcast(ChatColor.GREEN + "The games have ended!");
        Bukkit.getServer().setMaxPlayers(24);
        List<PlayerHealth> playerHealths = Lists.newArrayList();
        GamePlayer winner = null;

        if(countAlive() == 3){
            for(GamePlayer gamePlayer : gamePlayers){
                if(!gamePlayer.isWatching()){
                    playerHealths.add(new PlayerHealth(gamePlayer, (double) gamePlayer.getPlayer().getHealth()));
                }
            }
            playerHealths.sort(new HealthComparator());
            winner = playerHealths.get(0).getGamePlayer();
            winner.getPlayerData().updateWin(1);
            ChatUtility.broadcast(HotsCore.getHotsPlayer(winner.getPlayer()).getColorName() + ChatColor.GREEN + " has won the Survival Games!");
            ChatUtility.sendMessage(winner, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY
                    + "[" + ChatColor.YELLOW + winner.getPlayerData().calculatedWinAddPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA  + " points for won the game!");
        }

        if(countAlive() == 2){
            for(GamePlayer gamePlayer : gamePlayers){
                if(!gamePlayer.isWatching()){
                    playerHealths.add(new PlayerHealth(gamePlayer, (double) gamePlayer.getPlayer().getHealth()));
                }
            }
            playerHealths.sort(new HealthComparator());
            winner = playerHealths.get(0).getGamePlayer();
            winner.getPlayerData().updateWin(1);
            ChatUtility.broadcast(HotsCore.getHotsPlayer(winner.getPlayer()).getColorName() + ChatColor.GREEN + " has won the Survival Games!");
            ChatUtility.sendMessage(winner, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY
                    + "[" + ChatColor.YELLOW + winner.getPlayerData().calculatedWinAddPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA  + " points for won the game!");
        }

        if(countAlive() == 1) {
            List<GamePlayer> a = Lists.newArrayList();
            for (GamePlayer gamePlayer1 : getGamePlayers()) {
                if (!gamePlayer1.isWatching()) {
                    a.add(gamePlayer1);
                }
            }
             winner = a.get(0);
            winner.getPlayerData().updateWin(1);
            ChatUtility.broadcast(HotsCore.getHotsPlayer(winner.getPlayer()).getColorName() + ChatColor.GREEN + " has won the Survival Games!");
            ChatUtility.sendMessage(winner, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY
                    + "[" + ChatColor.YELLOW + winner.getPlayerData().calculatedWinAddPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA  + " points for won the game!");
        }

        //Bountyされていたら
        if(bountyManager.wasBountiedMe(winner)) {
            List<BountyData> bountyData = bountyManager.getBounties(winner);
            for (BountyData bountyData1 : bountyData) {
                GamePlayer sender = bountyData1.getSender();
                if (sender.getPlayer().isOnline()) {
                    int points = bountyData1.getPoints();

                    int addPoints = (int) (points * 1.70) - points;

                    //優勝したら賭けたプレイヤーに賭けたポイント分の70%あげる
                    bountyData1.getSender().getPlayerData().addPoint(addPoints);

                    ChatUtility.sendMessage(sender, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + points + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " points for won the game " + HotsCore.getHotsPlayer(winner.getPlayer()).getColorName());
                }
            }
        }
        
        Bukkit.getWorld(getCurrentMap().getName()).setTime(18000);

        //打上花火
        for (int i = 0; i < getCurrentMap().getSpawns().size(); i++) {
            FireworkGenerator fwg = new FireworkGenerator(HSG.getInstance());
            fwg.setLocation(getCurrentMap().getSpawns().get(i));
            fwg.setPower(2);
            fwg.setEffect(FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).withFlicker().withTrail().withColor(Color.BLUE).withColor(Color.GREEN).build());
            fwg.setLifeTime(20);
            fwg.spawn();
        }
    }

    /**
     *
     */
    private void onLobby(){
        for (GamePlayer gamePlayer : gamePlayers) {
            gamePlayer.disableWatching();
            gamePlayer.teleport(gameConfig.getLobbyLocation());
            gamePlayer.resetPlayer();
        }
        chestManager.getOpenedChestLocations().clear();
        chestManager.getTier2ChestLocations().clear();
        currentMap.unloadWorld();
        voteManager.selectRandomVoteMaps();
        voteManager.setVoting(true);
        //Hubに転送
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            ChatUtility.sendMessage(player, ChatColor.GRAY + "Connecting to " + ChatColor.GREEN + "Hub");
            HSG.getInstance().getBungeeChannelApi().connect(player, "hub");
        }
        bountyManager.clearBountyData();
        time = gameConfig.getLobbyTime();
        state = GameState.Lobby;
    }

    /**
     *
     */
    private void tickLobby(){
        int online = getGamePlayers().size();
        if (timerFlag) {
            //人数が既定の人数より少なくなってしまったら
            if (online < gameConfig.getStartPlayerSize()) {
                setTimerFlag(false);
                setTime(gameConfig.getLobbyTime());
                ChatUtility.broadcast(ChatColor.RED + "Not enough players stopping countdown.");
                voteManager.setVoting(false);
            }
        }
        if (!timerFlag) {
            if (online >= gameConfig.getStartPlayerSize()) {
                voteManager.selectRandomVoteMaps();
                voteManager.broadcast();
                setTimerFlag(true);
                voteManager.setVoting(true);
            }
        }
        if (time == 45) {
            voteManager.broadcast();
            return;
        }
        if (time == 30) {
            voteManager.broadcast();
            return;
        }
        if (time == 15) {
            voteManager.broadcast();
            return;
        }
        if (time == 6) {
            voteManager.broadcast();
        }
    }

    /**
     *
     */
    private void tickPreGame(){
        //スコップわたす～～
        if(time == 15){
            for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                for (int i = 0; i < 9; i++) {
                    player.getInventory().setItem(i, new ItemStack(Material.WOOD_SPADE));
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!player.isOnline()){
                            this.cancel();
                            return;
                        }
                        player.getInventory().clear();
                        player.updateInventory();
                        this.cancel();
                    }
                }.runTaskLater(HSG.getInstance(), 1);
            }
        }
        if(countAlive() == 1){
            onEndGame();
        }
    }

    /**
     *
     */
    private void tickLiveGame(){
        if(countAlive() == 1){
            onEndGame();
        }
        if (countAlive() <= 3 && time > 60) {
            time = 60;
            ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + time + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until teleport to deathmatch.");
            return;
        }
        //Chest Refill
        //17分
        if (time == 1020) {
            chestManager.getOpenedChestLocations().clear();
            ChatUtility.broadcast(ChatColor.AQUA + "All of the chest have refilled!");
            for(Player player : Bukkit.getServer().getOnlinePlayers()){
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
            }
            return;
        }
        if (time == 420) {
            chestManager.getOpenedChestLocations().clear();
            ChatUtility.broadcast(ChatColor.AQUA + "All of the chest have refilled");
            for(Player player : Bukkit.getServer().getOnlinePlayers()){
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
            }
        }
    }

    /**
     *
     */
    private void tickPreDeathMatch(){
        if(countAlive() == 1){
            onEndGame();
        }
    }

    /**
     *
     */
    private void tickDeathMatch(){
        if(countAlive() == 1){
            onEndGame();
        }
    }

    /**
     *
     */
    private void countdownBroadcast(){
        if (state == GameState.Lobby) {
            ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + time + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until teleport to map.");
            return;
        }
        if (state == GameState.PreGame) {
            ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + time + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until the games begin.");
            return;
        }
        if (state == GameState.LiveGame) {
            ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + time + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until teleport to deathmatch.");
            return;
        }
        if (state == GameState.PreDeathMatch) {
            ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + time + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until the deathmatch begin.");
            return;
        }
        if (state == GameState.DeathMatch) {
            ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + time + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until the deathmatch ends.");
            return;
        }
        if (state == GameState.EndGame) {
            ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + time + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until sending to Hub");
        }
    }
}
