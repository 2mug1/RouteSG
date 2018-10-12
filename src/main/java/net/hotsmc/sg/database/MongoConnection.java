package net.hotsmc.sg.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.util.Collections;

@Getter
public class MongoConnection {

    private boolean authEnabled = false;

    private MongoClient client;
    private String host;
    private int port;
    private String username;
    private String password;
    private String databaseName;
    private MongoDatabase mongoDatabase;

    private MongoCollection<Document> players;

    public MongoConnection(MongoConfig mongoConfig) {
        this.authEnabled = mongoConfig.isAuthEnabled();
        this.host = mongoConfig.getHost();
        this.port = mongoConfig.getPort();
        this.databaseName = mongoConfig.getDatabaseName();
        if(mongoConfig.isAuthEnabled()) {
            this.username = mongoConfig.getUsername();
            this.password = mongoConfig.getPassword();
        }
    }
    /**
     * 接続処理
     */
    public void open() {
        //認証接続
        if (authEnabled) {
            final MongoCredential credential = MongoCredential.createCredential(username, databaseName, password.toCharArray());
            client = new MongoClient(new ServerAddress(host, port), Collections.singletonList(credential));
        } else {
            //非認証接続
            client = new MongoClient(new ServerAddress(host, port));
        }
        mongoDatabase = client.getDatabase(databaseName);
        players = mongoDatabase.getCollection("players");
    }
}
