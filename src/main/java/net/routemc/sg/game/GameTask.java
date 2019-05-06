package net.routemc.sg.game;

import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import me.signatured.ezqueuespigot.EzQueueAPI;
import me.trollcoding.requires.utils.objects.Style;
import net.routemc.core.RouteAPI;
import net.routemc.core.RouteCore;
import net.routemc.sg.RouteSG;
import net.routemc.sg.chest.ChestManager;
import net.routemc.sg.hotbar.PlayerHotbar;
import net.routemc.sg.map.MapData;
import net.routemc.sg.map.VoteManager;
import net.routemc.sg.player.GamePlayer;
import net.routemc.sg.player.GamePlayerData;
import net.routemc.sg.player.HealthComparator;
import net.routemc.sg.player.PlayerHealth;
import net.routemc.sg.reflection.BukkitReflection;
import net.routemc.sg.task.StrikeLightningTask;
import net.routemc.sg.team.GameTeam;
import net.routemc.sg.utility.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
@Setter
public class GameTask {

    private UUID host = null;
    private GameConfig gameConfig;
    private GameState state;
    private VoteManager voteManager;
    private ChestManager chestManager;
    private int time;
    private MapData currentMap = null;
    private List<GamePlayer> gamePlayers;
    private boolean timerFlag = false;
    private boolean hostWithPlay = false;
    private boolean sponsor;
    private boolean spectateRosterOnly = false;
    private List<GamePlayerData> gamePlayerData = new ArrayList<>();
    private List<String> fightLog = new ArrayList<>();
    private int gracePeriodTimeSec = 1800;

    private int circleSize = 63;

    public GameTask(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
        state = GameState.Lobby;
        gamePlayers = Lists.newArrayList();
        voteManager = new VoteManager();
        chestManager = new ChestManager();
        chestManager.loadTierItems();
        if(gameConfig.isCustomSG()){
            sponsor = false;
            time = 30;
        }else{
            sponsor = true;
            time = gameConfig.getLobbyTime();
        }
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(RouteSG.getInstance(), 0, 20);
    }

    /**
     * 一秒毎
     */
    private void tick() {

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

            if (state == GameState.PreDeathmatch) {
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
            if(gameConfig.isCustomSG()) {
               tickCustomSGLobby();
            }else{
                tickLobby();
            }
        }

        if (state == GameState.PreGame) {
            tickPreGame();
        }

        //LiveGameTask
        if (state == GameState.LiveGame) {
            tickLiveGame();
        }

        if (state == GameState.PreDeathmatch) {
            tickPreDeathMatch();
        }

        if (state == GameState.DeathMatch) {
            tickDeathMatch();
        }

        if(state == GameState.EndGame){
            tickEndGame();
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
        if(alive.size() > 3){
            if(gameConfig.isCustomSG()) {
                teleportToSpawnCustomSG();
            }else{
                teleportToSpawn();
            }
            return;
        }
        for (int i = 0; i < alive.size(); i++) {
            GamePlayer gamePlayer = alive.get(i);
            Location location = getCurrentMap().getDeathmatchSpawns().get(i);
            gamePlayer.teleport(location);
            gamePlayer.startFreezingTask(location);
        }
    }

    /**
     *
     */
    private void teleportToSpawn() {
        for (int i = 0; i < gamePlayers.size(); i++) {
            GamePlayer gamePlayer = gamePlayers.get(i);
            Location location = getCurrentMap().getSpawns().get(i);
            gamePlayer.teleport(location);
            gamePlayer.getPlayer().getInventory().clear();
            gamePlayer.getPlayer().updateInventory();
            gamePlayer.startFreezingTask(location);
        }
    }

    private void teleportToSpawnCustomSG() {
        if(!hostWithPlay){
            Player hostPlayer = Bukkit.getPlayer(host);
            if(hostPlayer != null){
                hostPlayer.teleport(currentMap.getDefaultSpawn());
                GamePlayer hostGP = getGamePlayer(hostPlayer);
                if(hostGP != null){
                    hostGP.setHotbar(PlayerHotbar.SPECTATE);
                    hostGP.enableWatching();
                }
            }
        }
        if(RouteSG.getInstance().getObserverPlayers().size() >= 1){
            for(String name : RouteSG.getInstance().getObserverPlayers()){
                Player obs = Bukkit.getPlayer(name);
                if(obs != null){
                    obs.teleport(currentMap.getDefaultSpawn());
                    GamePlayer obsGP = getGamePlayer(obs);
                    if(obsGP != null){
                        obsGP.setHotbar(PlayerHotbar.SPECTATE);
                        obsGP.enableWatching();
                    }
                }
            }
        }
        for (int i = 0; i < getCustomSGPlayers().size(); i++) {
            GamePlayer gamePlayer = getCustomSGPlayers().get(i);
            Location location = getCurrentMap().getSpawns().get(i);
            gamePlayer.teleport(location);
            gamePlayer.getPlayer().getInventory().clear();
            gamePlayer.getPlayer().updateInventory();
            gamePlayer.startFreezingTask(location);
        }
    }

    /**
     *
     */
    private void onPreGame() {
        gamePlayerData.clear();
        if(!gameConfig.isCustomSG()) {
            voteManager.setVoting(false);
            for (GamePlayer gamePlayer : gamePlayers) {
                gamePlayer.setVoted(false);
            }
            currentMap = voteManager.getDecidedMapData();
        }
        ChatUtility.broadcast(ChatColor.YELLOW + "Loading " + currentMap.getName() + "...");
        currentMap.loadWorld();
        chestManager.loadAllChest(currentMap);
        if(gameConfig.isCustomSG()) {
            teleportToSpawnCustomSG();
            for(GamePlayer gamePlayer : getCustomSGPlayers()){
                GameTeam team = null;
                if(gamePlayer.getInTeam() != null){
                    team = gamePlayer.getInTeam();
                }
                gamePlayerData.add(new GamePlayerData(gamePlayer.getName(), team));
                gamePlayer.sendMessage(Style.YELLOW + "Play Bonus!");
                RouteAPI.getExperienceOfPlayer(gamePlayer.getPlayer()).addExp(3);
            }
        }else{
            teleportToSpawn();
            for(GamePlayer gamePlayer : getGamePlayers()){
                gamePlayerData.add(new GamePlayerData(gamePlayer.getName(),null));
                gamePlayer.sendMessage(Style.YELLOW + "Play Bonus!");
                RouteAPI.getExperienceOfPlayer(gamePlayer.getPlayer()).addExp(3);
            }
        }
        ChatUtility.broadcast(ChatColor.YELLOW + "Map name" + ChatColor.DARK_GRAY + ": " + ChatColor.DARK_GREEN + currentMap.getName());
        ChatUtility.broadcast(ChatColor.DARK_RED + "Please wait " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + getGameConfig().getPregameTime() + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "seconds before the games begin.");
        ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + getGameConfig().getPregameTime() + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until the games begin!");
        if(!gameConfig.isCustomSG()) {
            ChatUtility.broadcast("" + ChatColor.YELLOW + ChatColor.BOLD + "WARNING " + ChatColor.RED + "Team is up to 4 people. / チームは4人まで");
        }else{
            ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);
            ChatUtility.normalBroadcast(Style.GRAY + " ● " + Style.LIGHT_PURPLE + Style.BOLD +  "Custom SG");
            ChatUtility.normalBroadcast("");
            ChatUtility.normalBroadcast(Style.YELLOW + " Host With Play" + Style.GRAY + ": " + (hostWithPlay ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
            ChatUtility.normalBroadcast(Style.YELLOW + " Sponsor" + Style.GRAY + ": " + (sponsor ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
            ChatUtility.normalBroadcast(Style.YELLOW + " Spectate Roster Only" + Style.GRAY + ": " + (spectateRosterOnly ? Style.GREEN + Style.BOLD + "Enabled" : Style.RED + Style.BOLD + "Disabled"));
            if(gracePeriodTimeSec < 1800){
                ChatUtility.normalBroadcast(Style.YELLOW + " Grace Period" + Style.GRAY + ": " + Style.GREEN + TimeUtility.timeFormat(gracePeriodTimeSec));
            }
            ChatUtility.normalBroadcast("");
            ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);
        }
        time = gameConfig.getPregameTime();
        state = GameState.PreGame;
    }

    /**
     *
     */
    private void onLiveGame() {
        for(GamePlayer gamePlayer : gamePlayers){
            gamePlayer.stopFreezingTask();
            if(!gameConfig.isCustomSG()) {
                gamePlayer.getPlayerData().updatePlayed(1);
            }
            gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.NOTE_PIANO, 2, 2);
        }
        BukkitReflection.setMaxPlayers(RouteSG.getInstance().getServer(), 40);
        ChatUtility.broadcast(ChatColor.DARK_AQUA + "The games have begun!");
        ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "30" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "minutes until deathmatch!");
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
        state = GameState.PreDeathmatch;
    }

    /**
     *
     */
    private void onDeathMatch() {
        for(GamePlayer gamePlayer : gamePlayers){
            gamePlayer.stopFreezingTask();
        }
        ChatUtility.broadcast(ChatColor.RED + "Fight to the death!");
        ChatUtility.broadcast("" + ChatColor.RED + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "3" + ChatColor.DARK_GRAY + "]" + ChatColor.RED + " minutes until the deathmatch ends!");
        setCircleSize(57);
        //3秒間に1ブロック狭める
        new BukkitRunnable() {
            @Override
            public void run() {
                if (RouteSG.getGameTask().getState() != GameState.DeathMatch || circleSize <= 0) {
                    this.cancel();
                    return;
                }
                circleSize--;
            }
        }.runTaskTimer(RouteSG.getInstance(), 0, 60);
        time = gameConfig.getDeathmatchTime();
        state = GameState.DeathMatch;
        //雷タスク
        for (GamePlayer gamePlayer : gamePlayers) {
            if (!gamePlayer.isWatching()) {
                if (gamePlayer.getPlayer() != null) {
                    new StrikeLightningTask(gamePlayer.getPlayer()).runTaskTimer(RouteSG.getInstance(), 0, 100);
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
            ChatUtility.normalBroadcast(winner.getSGName() + ChatColor.GREEN + " has won the Survival Games!");
            ChatUtility.normalBroadcast(winner.getSGName() + ChatColor.GREEN + " has won the Survival Games!");
            ChatUtility.normalBroadcast(winner.getSGName() + ChatColor.GREEN + " has won the Survival Games!");
            if(!RouteSG.getGameTask().getGameConfig().isCustomSG()) {
                winner.getPlayerData().updateWin(1);
                ChatUtility.sendMessage(winner, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY
                        + "[" + ChatColor.YELLOW + winner.getPlayerData().calculatedWinAddPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for won the game!");
            }
        }

        if(countAlive() == 2){
            for(GamePlayer gamePlayer : gamePlayers){
                if(!gamePlayer.isWatching()){
                    playerHealths.add(new PlayerHealth(gamePlayer, (double) gamePlayer.getPlayer().getHealth()));
                }
            }
            playerHealths.sort(new HealthComparator());
            winner = playerHealths.get(0).getGamePlayer();
            ChatUtility.normalBroadcast(winner.getSGName() + ChatColor.GREEN + " has won the Survival Games!");
            ChatUtility.normalBroadcast(winner.getSGName() + ChatColor.GREEN + " has won the Survival Games!");
            ChatUtility.normalBroadcast(winner.getSGName() + ChatColor.GREEN + " has won the Survival Games!");
            if(!RouteSG.getGameTask().getGameConfig().isCustomSG()) {
                winner.getPlayerData().updateWin(1);
                ChatUtility.sendMessage(winner, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY
                        + "[" + ChatColor.YELLOW + winner.getPlayerData().calculatedWinAddPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for won the game!");
            }
        }

        if(countAlive() == 1) {
            List<GamePlayer> a = Lists.newArrayList();
            for (GamePlayer gamePlayer1 : getGamePlayers()) {
                if (!gamePlayer1.isWatching()) {
                    a.add(gamePlayer1);
                }
            }
            winner = a.get(0);
            ChatUtility.normalBroadcast(winner.getSGName() + ChatColor.GREEN + " has won the Survival Games!");
            ChatUtility.normalBroadcast(winner.getSGName() + ChatColor.GREEN + " has won the Survival Games!");
            ChatUtility.normalBroadcast(winner.getSGName() + ChatColor.GREEN + " has won the Survival Games!");
            if (!RouteSG.getGameTask().getGameConfig().isCustomSG()) {
                winner.getPlayerData().updateWin(1);
                ChatUtility.sendMessage(winner, ChatColor.DARK_AQUA + "You've gained " + ChatColor.DARK_GRAY
                        + "[" + ChatColor.YELLOW + winner.getPlayerData().calculatedWinAddPoint() + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + " points for won the game!");
            }
        }

        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5F, 1F);
        }

        if(winner != null) {
            GamePlayerData winnerData = getGamePlayerData(winner.getName());
            winnerData.setAlive(false);
            winnerData.applyPlaceRank();
            winner.sendMessage(Style.SCOREBAORD_SEPARATOR);
            winner.sendMessage(Style.GOLD + Style.BOLD + " Winner Bonus!");
            RouteAPI.getExperienceOfPlayer(winner.getPlayer()).addExp(2000);
            RouteAPI.getEconomyOfPlayer(winner.getPlayer()).addCoin(10);
            winner.sendMessage(Style.SCOREBAORD_SEPARATOR);
        }

        gamePlayerData.sort((o1, o2) -> o1.getPlaceRank() > o2.getPlaceRank() ? 1 : -1);

        ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);
        ChatUtility.normalBroadcast(Style.YELLOW + Style.BOLD + " Alive Rank");
        int aliveRank = 1;
        for(GamePlayerData data : gamePlayerData){
            ChatUtility.normalBroadcast(Style.YELLOW + "#" + aliveRank + Style.DARK_GRAY + ": " + (data.getTeam() == null ? Style.WHITE + data.getName() : data.getTeam().getPrefix() + data.getName()) + Style.GRAY + " - " + Style.WHITE + data.getKills() + " kills");
            aliveRank++;
        }
        ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);

        //夜にする
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            player.setPlayerTime(18000, false);
        }

        //打上花火
        for (int i = 0; i < getCurrentMap().getSpawns().size(); i++) {
            FireworkGenerator fwg = new FireworkGenerator(RouteSG.getInstance());
            fwg.setLocation(getCurrentMap().getSpawns().get(i));
            fwg.setPower(1);
            fwg.setEffect(FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.BALL).withFlicker().withTrail().withColor(Color.FUCHSIA).withColor(Color.WHITE).build());
            fwg.setLifeTime(90);
            fwg.spawn();
        }
    }

    /**
     *
     */
    private void onLobby(){
        gamePlayerData.clear();
        fightLog.clear();
        for (GamePlayer gamePlayer : gamePlayers) {
            gamePlayer.disableWatching();
            gamePlayer.teleport(gameConfig.getLobbyLocation());
            gamePlayer.resetPlayer();
            gamePlayer.setHotbar(PlayerHotbar.LOBBY);
        }
        RouteSG.getInstance().getTeamManager().getTeams().clear();
        //Hubに転送
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            BungeeUtils.connect(player, "SurvivalGames");
        }
        voteManager.selectRandomVoteMaps();
        voteManager.setVoting(true);
        currentMap.unloadWorld();
        if(gameConfig.isCustomSG()){
            BukkitReflection.setMaxPlayers(RouteSG.getInstance().getServer(), 25);
            host = null;
            sponsor = false;
            gracePeriodTimeSec = 1800;
            spectateRosterOnly = false;
            hostWithPlay = false;
            RouteSG.getInstance().getWhitelistedPlayers().clear();
            RouteSG.getInstance().getObserverPlayers().clear();
        } else {
            BukkitReflection.setMaxPlayers(RouteSG.getInstance().getServer(), 24);
        }
        time = gameConfig.getLobbyTime();
        state = GameState.Lobby;
    }

    /**
     *
     */
    private void tickCustomSGLobby(){
        if (timerFlag) {
            if(!hostWithPlay) {
                if (getCustomSGPlayers().size() < gameConfig.getStartPlayerSize()) {
                    setTimerFlag(false);
                    setTime(30);
                    ChatUtility.broadcast(ChatColor.RED + "Not enough players stopping countdown.");
                }
            }
            else if(getGamePlayers().size() < gameConfig.getStartPlayerSize()){
                setTimerFlag(false);
                setTime(30);
                ChatUtility.broadcast(ChatColor.RED + "Not enough players stopping countdown.");
            }
        }
        if(time <= 5){
            for(Player player : Bukkit.getServer().getOnlinePlayers()){
                player.playSound(player.getLocation(), Sound.CLICK, 2, 1);
            }
        }
        ServerUtility.setMotd("0," + time);
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
                for(GamePlayer gamePlayer:gamePlayers){
                    gamePlayer.setVoted(false);
                    gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.CLICK, 2, 1);
                }
            }
        }
        if(time <= 5){
            for(Player player : Bukkit.getServer().getOnlinePlayers()){
                player.playSound(player.getLocation(), Sound.CLICK, 2, 1);
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
        if (time == 10) {
            voteManager.broadcast();
            return;
        }
        if (time == 6) {
            voteManager.broadcast();
        }
        ServerUtility.setMotd("0," + time);
    }

    /**
     *
     */
    private void tickPreGame(){
        if(time == 15) {
            if (gameConfig.isCustomSG()) {
                for (GamePlayer gamePlayer : getCustomSGPlayers()) {
                    for (int i = 0; i < 9; i++) {
                        gamePlayer.getPlayer().getInventory().setItem(i, new ItemStack(Material.WOOD_SPADE));
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!gamePlayer.isOnline()) {
                                this.cancel();
                                return;
                            }
                            gamePlayer.getPlayer().getInventory().clear();
                            gamePlayer.getPlayer().updateInventory();
                            this.cancel();
                        }
                    }.runTaskLater(RouteSG.getInstance(), 1);
                }
            }else{
                for (GamePlayer gamePlayer : getGamePlayers()) {
                    for (int i = 0; i < 9; i++) {
                        gamePlayer.getPlayer().getInventory().setItem(i, new ItemStack(Material.WOOD_SPADE));
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!gamePlayer.isOnline()) {
                                this.cancel();
                                return;
                            }
                            gamePlayer.getPlayer().getInventory().clear();
                            gamePlayer.getPlayer().updateInventory();
                            this.cancel();
                        }
                    }.runTaskLater(RouteSG.getInstance(), 1);
                }
            }
        }
        if(time <= 5){
            for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
            }
        }
        ServerUtility.setMotd("1," + time);
    }

    /**
     *
     */
    private void tickLiveGame(){
        if (countAlive() <= 3 && time > 60) {
            time = 60;
            ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + time + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until teleport to deathmatch.");
            return;
        }

        if(time < 1800) {
            if (time == gracePeriodTimeSec) {
                ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);
                ChatUtility.normalBroadcast(ChatColor.AQUA + "PvP has been enabled!");
                ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);
                for(Player player : Bukkit.getServer().getOnlinePlayers()){
                    player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1, 1);
                }
            }
        }

        if (time == 1020) {
            //Chest Refill 17分
            chestManager.refillChest();
            ChatUtility.broadcast(ChatColor.AQUA + "All of the chest have refilled!");
            for(Player player : Bukkit.getServer().getOnlinePlayers()){
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
            }
        }
        if (time == 420) {
            chestManager.refillChest();
            ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);
            ChatUtility.normalBroadcast(ChatColor.AQUA + "All of the chest have been refilled!");
            ChatUtility.normalBroadcast(Style.HORIZONTAL_SEPARATOR);
            for(Player player : Bukkit.getServer().getOnlinePlayers()){
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
            }
        }
        ServerUtility.setMotd("2," + time);
    }

    /**
     *
     */
    private void tickPreDeathMatch() {
        ServerUtility.setMotd("3," + time);
    }

    /**
     *
     */
    private void tickDeathMatch(){
        ServerUtility.setMotd("4," + time);
    }

    private void tickEndGame(){
        ServerUtility.setMotd("5," + time);
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
        if (state == GameState.PreDeathmatch) {
            ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + time + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until the deathmatch begin.");
            return;
        }
        if (state == GameState.DeathMatch) {
            ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + time + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until the deathmatch ends.");
            return;
        }
        if (state == GameState.EndGame) {
            ChatUtility.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + time + ChatColor.DARK_GRAY + "] " + ChatColor.RED + " seconds until sending to Lobby");
        }
    }

    public void updateHost(Player player){
        this.host = player.getUniqueId();
        RouteSG.getInstance().getWhitelistedPlayers().add(player.getName().toLowerCase());
        ChatUtility.sendMessage(player, Style.YELLOW + "You currently are the host.");
        GamePlayer gamePlayer = RouteSG.getGameTask().getGamePlayer(player);
        gamePlayer.setHotbar(PlayerHotbar.CUSTOMSG_HOST_LOBBY);
        if(!hostWithPlay) {
            RouteSG.getGameTask().getGamePlayer(player).enableWatching();
        }
        player.getInventory().setItem(0, ItemUtility.createItemStack(ChatColor.AQUA + "Lobby Sword", Material.STONE_SWORD, true));
        player.getInventory().setItem(1, ItemUtility.createItemStack(ChatColor.AQUA + "Lobby Rod", Material.FISHING_ROD, true));
        player.updateInventory();
    }

    public List<GamePlayer> getCustomSGPlayers(){
        List<GamePlayer> toReturn = new ArrayList<>();
        for(GamePlayer gamePlayer : gamePlayers){
            if(!hostWithPlay){
                if(gamePlayer.getPlayer().getUniqueId() != host && !RouteSG.getInstance().getObserverPlayers().contains(gamePlayer.getName().toLowerCase())){
                    toReturn.add(gamePlayer);
                }
            }else{
                if(!RouteSG.getInstance().getObserverPlayers().contains(gamePlayer.getName().toLowerCase())){
                    toReturn.add(gamePlayer);
                }
            }
        }
        return toReturn;
    }

    public GamePlayerData getGamePlayerData(String name){
        for(GamePlayerData data : gamePlayerData){
            if(data.getName().equalsIgnoreCase(name)){
                return data;
            }
        }
        return null;
    }

    public int getAliveSizeByGamePlayerData(){
        int size = 1;
        for(GamePlayerData data : gamePlayerData){
            if(data.isAlive()){
                size++;
            }
        }
        return size;
    }

    public List<GamePlayer> getAlivePlayers(){
        List<GamePlayer> alive = new LinkedList<>();
        for(GamePlayer gamePlayer : gamePlayers){
            if(!gamePlayer.isWatching()){
                alive.add(gamePlayer);
            }
        }
        return alive;
    }

    public void addFightLog(String log){
        fightLog.add(Style.WHITE + "- (" + state.name() + " " + getFormatTime() + ") " + log);
    }

    public String getFormatTime(){
        return TimeUtility.timeFormat(time);
    }

    public void refreshServerInfo(){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("UPDATE-SG-SERVER-INFO");
        out.writeUTF(RouteCore.get().getMainConfig().getString("SERVER_NAME"));
        out.writeUTF(state.name());
        out.writeUTF(String.valueOf(time));

        String hostPlayerName = "Not Hosting";
        Player hostPlayer = Bukkit.getPlayer(host);
        if(hostPlayer != null) {
            hostPlayerName = hostPlayer.getName();
        }
        out.writeUTF(hostPlayerName);
        Bukkit.getServer().sendPluginMessage(RouteSG.getInstance(), "BungeeCord", out.toByteArray());
    }
}
