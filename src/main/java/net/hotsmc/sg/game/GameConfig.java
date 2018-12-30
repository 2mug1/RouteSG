package net.hotsmc.sg.game;

import lombok.Getter;
import net.hotsmc.sg.config.ConfigCursor;
import org.bukkit.Location;

@Getter
public class GameConfig {

    ConfigCursor configCursor;
    private boolean customSG;
    private int lobbyTime;
    private int pregameTime;
    private int livegameTime;
    private int predeathmatchTime;
    private int deathmatchTime;
    private int endgameTime;
    private int startPlayerSize;
    private Location lobbyLocation;

    public GameConfig(ConfigCursor configCursor){
        this.configCursor = configCursor;
    }

    public GameConfig load(){
        customSG = configCursor.getBoolean("CustomSG");
        lobbyTime = configCursor.getInt("LobbyTime");
        pregameTime = configCursor.getInt("PreGameTime");
        livegameTime = configCursor.getInt("LiveGameTime");
        predeathmatchTime = configCursor.getInt("PreDeathMatchTime");
        deathmatchTime = configCursor.getInt("DeathMatchTime");
        endgameTime = configCursor.getInt("EndGameTime");
        startPlayerSize = configCursor.getInt("StartPlayerSize");
        lobbyLocation = configCursor.getLocation("Lobby");
        return this;
    }
}
