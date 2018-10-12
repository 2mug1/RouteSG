package net.hotsmc.sg.database;

import lombok.Getter;
import lombok.Setter;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.database.MongoConnection;
import net.hotsmc.sg.utility.MongoUtility;
import org.bson.Document;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
public class PlayerData {

    private String uuid;

    private String name;

    private Timestamp firstPlayed;

    private int win;

    private int played;

    private int kill;

    private int point;

    private int chests;

    private int highestRank;

    private boolean sidebarMinimize;

    public PlayerData(String uuid){
        this.uuid = uuid;
    }

    private static MongoConnection getMongoConnection() {
        return HSG.getInstance().getMongoConnection();
    }

    private Document findByUUID() {
        return getMongoConnection().getPlayers().find(MongoUtility.find("UUID", uuid)).first();
    }

    private Document findByName() {
        return getMongoConnection().getPlayers().find(MongoUtility.find("NAME", uuid)).first();
    }

    private void updateOne(Document updateDocument) {
        getMongoConnection().getPlayers().updateOne(findByUUID(), new Document("$set", updateDocument));
    }

    public void insertNewPlayerData() {
        Document document = new Document();

        document.put("UUID", uuid);
        document.put("NAME", name);
        document.put("FIRST_PLAYED", new Timestamp(System.currentTimeMillis()).getTime());
        document.put("WIN", 0);
        document.put("PLAYED", 0);
        document.put("KILL", 0);
        document.put("POINT", 500);
        document.put("CHESTS", 0);
        document.put("HIGHEST_RANK", 0);
        document.put("SIDEBAR_MINIMIZE", false);

        getMongoConnection().getPlayers().insertOne(document);
    }

    public void loadData() {
        Document document = findByUUID();

        //なかったら新規作成
        if (document == null) {

            insertNewPlayerData();

        } else {

            //値取得
            setWin(document.getInteger("WIN"));
            setPlayed(document.getInteger("PLAYED"));
            setKill(document.getInteger("KILL"));
            setPoint(document.getInteger("POINT"));
            setChests(document.getInteger("CHESTS"));
            setHighestRank(document.getInteger("HIGHEST_RANK"));
            setSidebarMinimize(document.getBoolean("SIDEBAR_MINIMIZE"));

            //データベースに登録されている名前と違ったら更新
            if (!name.equals(document.getString("NAME"))) {
                updateName();
            }
        }
    }

    private void updateName() {
        updateOne(findByUUID().append("NAME", name));
    }

    private void updateInteger(String key, int amount){
        updateOne(findByUUID().append(key, amount));
    }

    public void updateWin(int amount){
        this.win = win+amount;
        updateInteger("WIN", this.win);
    }

    public void updatePlayed(int amount){
        this.played = played + amount;
        updateInteger("PLAYED", this.played);
    }

    public void updateKill(int amount){
        this.kill = kill+amount;
        updateInteger("KILL", this.kill);
    }

    public void addPoint(int amount){
        this.point = amount+point;
        updateInteger("POINT", this.point);
    }

    public void withdrawPoint(int amount){
        this.point = point-amount;
        updateInteger("POINT", this.point);
    }

    public void updateChests(int amount){
        this.chests = chests+amount;
        updateInteger("CHESTS", this.chests);
    }

    public void updateHighestRank(int highestRank){
        this.highestRank = highestRank;
        updateInteger("HIGHEST_RANK", this.highestRank);
    }

    public void updateSidebarMinimize(boolean sidebarMinimize){
        this.sidebarMinimize = sidebarMinimize;
        updateOne(findByUUID().append("SIDEBAR_MINIMIZE", sidebarMinimize));
    }
}
