package net.routemc.sg;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import me.trollcoding.requires.Requires;
import me.trollcoding.requires.sidebar.BoardManager;
import me.trollcoding.requires.utils.objects.Cooldown;
import net.routemc.sg.config.ConfigCursor;
import net.routemc.sg.config.FileConfig;
import net.routemc.sg.database.MongoConfig;
import net.routemc.sg.database.MongoConnection;
import net.routemc.sg.listener.ListenerHandler;
import net.routemc.sg.map.MapData;
import net.routemc.sg.map.MapManager;
import net.routemc.sg.preset.PresetManager;
import net.routemc.sg.preset.impl.ClanWarPreset;
import net.routemc.sg.preset.impl.RouteTournamentPreset;
import net.routemc.sg.preset.impl.MatsuLeaguePreset;
import net.routemc.sg.team.TeamManager;
import net.routemc.sg.command.*;
import net.routemc.sg.game.GameConfig;
import net.routemc.sg.game.GameState;
import net.routemc.sg.game.GameTask;
import net.routemc.sg.menu.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RouteSG extends JavaPlugin {

    @Getter
    private static RouteSG instance;

    @Getter
    private static GameTask gameTask;

    @Getter
    private static MongoConnection mongoConnection;

    @Getter
    private static MapManager mapManager;

    private TeamManager teamManager;
    private PresetManager presetManager;

    @Getter
    private static Settings settings;

    private SpectateMenu spectateMenu;
    private VoteMenu voteMenu;
    private SelectMapMenu selectMapMenu;
    private PresetMenu presetMenu;
    private TeamListMenu teamListMenu;

    private List<String> whitelistedPlayers;
    private List<String> observerPlayers;

    @Setter
    private Cooldown broadcastCooldown = new Cooldown(0);

    @Override
    public void onEnable() {
        instance = this;
        presetManager = new PresetManager();
        settings = new Settings(new ConfigCursor(new FileConfig(this, "Settings.yml"), "settings"));
        mapManager = new MapManager(this);
        mapManager.load();
        gameTask = new GameTask(new GameConfig(new ConfigCursor(new FileConfig(this, "GameConfig.yml"), "GameConfig")).load());
        gameTask.start();
        ListenerHandler.loadListenersFromPackage(this, "net.routemc.sg.listener.listeners");
        registerCommands();
        mongoConnection = new MongoConnection(new MongoConfig().load());
        mongoConnection.open();
        Requires.instance.setupBoardManager(new BoardManager(this, new GameScoreboardAdapter()));
        voteMenu = new VoteMenu();
        spectateMenu = new SpectateMenu();
        teamManager = new TeamManager();
        if(gameTask.getGameConfig().isCustomSG()) {
            whitelistedPlayers = new ArrayList<>();
            observerPlayers = new ArrayList<>();
            selectMapMenu = new SelectMapMenu();
            presetMenu = new PresetMenu();
            teamListMenu = new TeamListMenu();
            presetManager.register(new ClanWarPreset());
            presetManager.register(new MatsuLeaguePreset());
            presetManager.register(new RouteTournamentPreset());
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
        this.getCommand("rsg").setExecutor(new SettingCommand());
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
        this.getCommand("kc").setExecutor(new KillCountCommand());
        this.getCommand("team").setExecutor(new TeamCommand());
        this.getCommand("tc").setExecutor(new TeamChatCommand());
        this.getCommand("tl").setExecutor(new TeamListCommand());
        this.getCommand("fl").setExecutor(new FightLogCommand());
        this.getCommand("kt").setExecutor(new KillTopCommand());
    }

    public static List<Player> getOnlinePlayers() {
        List<Player> list = Lists.newArrayList();
        for (World world : Bukkit.getWorlds()) {
            list.addAll(world.getPlayers());
        }
        return Collections.unmodifiableList(list);
    }
}