package net.routemc.sg.database;

import lombok.Data;
import net.routemc.sg.RouteSG;
import net.routemc.sg.config.ConfigCursor;
import net.routemc.sg.config.FileConfig;
import net.routemc.sg.utility.ConsoleUtility;

@Data
public class MongoConfig {

    private String host;
    private int port;
    private String databaseName;
    private boolean authEnabled;
    private String username, password;

    public MongoConfig load() {
        ConfigCursor cursor = new ConfigCursor(new FileConfig(RouteSG.getInstance(), "MongoConfig.yml"), "mongo");
        if (!cursor.exists("host")
                || !cursor.exists("port")
                || !cursor.exists("databaseName")
                || !cursor.exists("authEnabled")
                || !cursor.exists("username")
                || !cursor.exists("password")){
            throw new RuntimeException("Failed to load mongoConfig.yml");
        }
        setHost(cursor.getString("host"));
        setPort(cursor.getInt("port"));
        setDatabaseName(cursor.getString("databaseName"));
        setAuthEnabled(cursor.getBoolean("authEnabled"));
        if(cursor.getBoolean("authEnabled")) {
            setUsername(cursor.getString("username"));
            setPassword(cursor.getString("password"));
        }
        ConsoleUtility.sendMessage("MongoConfig.yml has loaded");
        return this;
    }
}
