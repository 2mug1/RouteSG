package net.routemc.sg.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import me.trollcoding.requires.utils.objects.Style;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class MongoConnection {

    private boolean authEnabled;

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
            final MongoCredential credential = MongoCredential.createScramSha1Credential(username, databaseName, password.toCharArray());
            client = new MongoClient(new ServerAddress(host, port), Collections.singletonList(credential));
        } else {
            //非認証接続
            client = new MongoClient(new ServerAddress(host, port));
        }
        mongoDatabase = client.getDatabase(databaseName);
        players = mongoDatabase.getCollection("players");
    }

    public List<String> getTop10(String key){
        List<IntegerValueData> values= new ArrayList<>();
        int rank = 0;
        for (Document document : players.find().sort(Sorts.orderBy(Sorts.descending(key)))) {
            rank++;
            if(rank > 10){
                break;
            }
            values.add(new IntegerValueData(document.getString("NAME"), document.getInteger(key)));
        }
        List<String> format = new ArrayList<>();
        int valueRank = 1;
        for (IntegerValueData data : values) {
            format.add("" + ChatColor.YELLOW + valueRank + ". " + Style.WHITE +  data.getString() + ChatColor.GRAY + " - " + ChatColor.YELLOW + data.getValue());
            valueRank++;
        }
        format.add(0, Style.SCOREBAORD_SEPARATOR);
        format.add(Style.SCOREBAORD_SEPARATOR);
        return format;
    }
}
