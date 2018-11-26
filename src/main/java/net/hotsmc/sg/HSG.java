package net.hotsmc.sg;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Sorts;
import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi;
import lombok.Getter;
import net.hotsmc.core.HotsCore;
import net.hotsmc.core.gui.ClickActionItem;
import net.hotsmc.core.scoreboard.BoardManager;
import net.hotsmc.sg.command.*;
import net.hotsmc.sg.config.ConfigCursor;
import net.hotsmc.sg.config.FileConfig;
import net.hotsmc.sg.database.MongoConfig;
import net.hotsmc.sg.database.MongoConnection;
import net.hotsmc.sg.game.*;
import net.hotsmc.sg.listener.ListenerHandler;
import net.hotsmc.sg.menu.SpectateMenu;
import net.hotsmc.sg.utility.ItemUtility;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

    @Getter
    private SpectateMenu spectateMenu;

    @Getter
    private static List<ClickActionItem> clickActionItems;

    @Override
    public void onEnable() {
        instance = this;
        bungeeChannelApi = BungeeChannelApi.of(this);
        settings = new Settings(new ConfigCursor(new FileConfig(this, "Settings.yml"), "settings"));
        mapManager = new MapManager(this);
        mapManager.load();
        gameTask = new GameTask(new GameConfig(new ConfigCursor(new FileConfig(this, "GameConfig.yml"), "GameConfig")).load());
        gameTask.start();
        ListenerHandler.loadListenersFromPackage(this, "net.hotsmc.sg.listener.listeners");
        registerCommands();
        mongoConnection = new MongoConnection(new MongoConfig().load());
        mongoConnection.open();
        initClickItems();
        HotsCore.getInstance().setBoardManager(new BoardManager(this, new GameScoreboardAdapter()));
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
        this.getCommand("sidebar").setExecutor(new SidebarCommand());
        this.getCommand("spec").setExecutor(new SpectateCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());
        this.getCommand("list").setExecutor(new ListCommand());
        this.getCommand("sponsor").setExecutor(new SponsorCommand());
        this.getCommand("gf").setExecutor(new GroundFixCommand());
    }

    public static List<Player> getOnlinePlayers() {
        List<Player> list = Lists.newArrayList();
        for (World world : Bukkit.getWorlds()) {
            list.addAll(world.getPlayers());
        }
        return Collections.unmodifiableList(list);
    }

    private void initClickItems(){
        clickActionItems = Lists.newArrayList();
        spectateMenu = new SpectateMenu();
        clickActionItems.add(new ClickActionItem(ItemUtility.createItemStack(""+ ChatColor.RED + ChatColor.BOLD + "Spectate Player" + ChatColor.GRAY + " -  Right Click to select the player", Material.COMPASS, false)) {
            @Override
            public void clickAction(Player player) {
                spectateMenu.openMenu(player, 27);
            }
        });
    }
}
