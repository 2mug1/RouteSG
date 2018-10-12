package net.hotsmc.sg;

import com.google.common.collect.Lists;
import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi;
import lombok.Getter;
import net.hotsmc.core.HotsCore;
import net.hotsmc.sg.command.*;
import net.hotsmc.sg.config.ConfigCursor;
import net.hotsmc.sg.config.FileConfig;
import net.hotsmc.sg.database.MongoConfig;
import net.hotsmc.sg.database.MongoConnection;
import net.hotsmc.sg.game.*;
import net.hotsmc.sg.listener.ListenerHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class HSG extends JavaPlugin {

    @Getter
    private static HSG instance;

    @Getter
    private static GameTask gameTask;

    @Getter
    private static MongoConnection mongoConnection;

    @Getter
    private static MapManager mapManager;

    @Getter
    private static Settings settings;

    @Getter
    private BungeeChannelApi bungeeChannelApi;

    private HotsCore hotsCore;

    @Override
    public void onEnable() {
        instance = this;
        bungeeChannelApi = BungeeChannelApi.of(this);
        hotsCore = HotsCore.getInstance();
        settings = new Settings(new ConfigCursor(new FileConfig(this, "Settings.yml"), "settings"));
        mapManager = new MapManager(this);
        mapManager.load();
        gameTask = new GameTask(new GameConfig(new ConfigCursor(new FileConfig(this, "GameConfig.yml"), "GameConfig")).load());
        gameTask.start();
        ListenerHandler.loadListenersFromPackage(this, "net.hotsmc.sg.listener.listeners");
        registerCommands();
        mongoConnection = new MongoConnection(new MongoConfig().load());
        mongoConnection.open();
    }

    @Override
    public void onDisable() {
        MapData mapData = gameTask.getCurrentMap();
        if(mapData != null && gameTask.getState() != GameState.Lobby){
            mapData.unloadWorld();
        }
    }

    private void registerCommands() {
        this.getCommand("hsg").setExecutor(new SettingCommand());
        this.getCommand("vote").setExecutor(new VoteCommand());
        this.getCommand("sidebar").setExecutor(new SidebarCommand());
        this.getCommand("spec").setExecutor(new SpectateCommand());
    }

    public static List<Player> getOnlinePlayers() {
        List<Player> list = Lists.newArrayList();
        for (World world : Bukkit.getWorlds()) {
            list.addAll(world.getPlayers());
        }
        return Collections.unmodifiableList(list);
    }
}
