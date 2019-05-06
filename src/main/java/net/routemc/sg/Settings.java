package net.routemc.sg;

import lombok.Getter;
import net.routemc.sg.config.ConfigCursor;

@Getter
public class Settings {

    private ConfigCursor configCursor;
    private String serverName;
    private String serverRegion;
    private double fisihingrod_horizontalMultiplier;
    private double fisihingrod_verticalMultiplier;
    public Settings(ConfigCursor configCursor){
        this.configCursor = configCursor;
        this.serverName = configCursor.getString("ServerName");
        this.serverRegion = configCursor.getString("ServerRegion");
        this.fisihingrod_horizontalMultiplier = configCursor.getDouble("fisihingrod_horizontalMultiplier");
        this.fisihingrod_verticalMultiplier = configCursor.getDouble("fisihingrod_verticalMultiplier");
    }

    public void reloadFRKB(){
        this.fisihingrod_horizontalMultiplier = configCursor.getDouble("fisihingrod_horizontalMultiplier");
        this.fisihingrod_verticalMultiplier = configCursor.getDouble("fisihingrod_verticalMultiplier");
    }
}
