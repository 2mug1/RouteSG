package net.hotsmc.sg;

import lombok.Getter;
import net.hotsmc.sg.config.ConfigCursor;

@Getter
public class Settings {

    private String serverName;
    private String serverRegion;

    public Settings(ConfigCursor configCursor){
        this.serverName = configCursor.getString("ServerName");
        this.serverRegion = configCursor.getString("ServerRegion");
    }
}
