package net.hotsmc.sg;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.hotsmc.core.HotsCore;
import net.hotsmc.core.scoreboard.BoardManager;
import net.hotsmc.sg.command.*;
import net.hotsmc.sg.config.ConfigCursor;
import net.hotsmc.sg.config.FileConfig;
import net.hotsmc.sg.database.MongoConfig;
import net.hotsmc.sg.database.MongoConnection;
import net.hotsmc.sg.game.*;
import net.hotsmc.sg.listener.ListenerHandler;
import net.hotsmc.sg.map.MapData;
import net.hotsmc.sg.map.MapManager;
import net.hotsmc.sg.menu.*;
import net.hotsmc.sg.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class HSG extends JavaPlugin {

    @Getter
    private static HSG instance;

    @Getter
    private static GameTask gameTask;

    @Getter
    private static MongoConnection mongoConnection;

    @Getter
    private static MapManager mapManager;

    private TeamManager teamManager;

    @Getter
    private static Settings settings;

    private SpectateMenu spectateMenu;
    private VoteMenu voteMenu;
    private SelectMapMenu selectMapMenu;
    private PresetMenu presetMenu;
    private TeamListMenu teamListMenu;

    private List<String> whitelistedPlayers;
    private List<String> observerPlayers;

    @Override
    public void onEnable() {
        instance = this;
        settings = new Settings(new ConfigCursor(new FileConfig(this, "Settings.yml"), "settings"));
        mapManager = new MapManager(this);
        mapManager.load();
        gameTask = new GameTask(new GameConfig(new ConfigCursor(new FileConfig(this, "GameConfig.yml"), "GameConfig")).load());
        gameTask.start();
        ListenerHandler.loadListenersFromPackage(this, "net.hotsmc.sg.listener.listeners");
        registerCommands();
        mongoConnection = new MongoConnection(new MongoConfig().load());
        mongoConnection.open();
        HotsCore.getInstance().setBoardManager(new BoardManager(this, new GameScoreboardAdapter()));
        voteMenu = new VoteMenu();
        spectateMenu = new SpectateMenu();
        if(gameTask.getGameConfig().isCustomSG()) {
            whitelistedPlayers = new ArrayList<>();
            observerPlayers = new ArrayList<>();
            selectMapMenu = new SelectMapMenu();
            presetMenu = new PresetMenu();
            teamListMenu = new TeamListMenu();
            teamManager = new TeamManager();
        }
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        MapData mapData = gameTask.getCurrentMap();
        if (mapData != null && gameTask.getState() != GameState.Lobby) {
            mapData.unloadWorld();
        }
    }

    private void registerCommands() {
        this.getCommand("hsg").setExecutor(new SettingCommand());
        this.getCommand("vote").setExecutor(new VoteCommand());
        this.getCommand("spec").setExecutor(new SpectateCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());
        this.getCommand("list").setExecutor(new ListCommand());
        this.getCommand("sponsor").setExecutor(new SponsorCommand());
        this.getCommand("gf").setExecutor(new GroundFixCommand());
        this.getCommand("host").setExecutor(new HostCommand());
        this.getCommand("givehost").setExecutor(new GiveHostCommand());
        this.getCommand("register").setExecutor(new RegisterPlayerCommand());
        this.getCommand("unregister").setExecutor(new UnregisterPlayerCommand());
        this.getCommand("frkbreload").setExecutor(new FRKBReloadCommand());
        this.getCommand("roster").setExecutor(new RosterCommand());
        this.getCommand("observer").setExecutor(new ObserverCommand());
        this.getCommand("kt").setExecutor(new KillTotalCommand());
        this.getCommand("team").setExecutor(new TeamCommand());
        this.getCommand("tc").setExecutor(new TeamChatCommand());
        this.getCommand("tl").setExecutor(new TeamListCommand());
        this.getCommand("fl").setExecutor(new FightLogCommand());

    }

    public static List<Player> getOnlinePlayers() {
        List<Player> list = Lists.newArrayList();
        for (World world : Bukkit.getWorlds()) {
            list.addAll(world.getPlayers());
        }
        return Collections.unmodifiableList(list);
    }
}