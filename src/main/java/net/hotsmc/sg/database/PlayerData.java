package net.hotsmc.sg.database;

import com.mongodb.client.MongoCursor;
import lombok.Getter;
import lombok.Setter;
import net.hotsmc.sg.HSG;
import net.hotsmc.sg.utility.MongoUtility;
import org.bson.Document;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

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

    private boolean sidebarMinimize;

    public PlayerData(String uuid){
        this.uuid = uuid;
    }

    private static MongoConnection getMongoConnection() {
        return HSG.getMongoConnection();
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

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        document.put("UUID", uuid);
        document.put("NAME", name);
        document.put("FIRST_PLAYED", timestamp.getTime());
        document.put("WIN", 0);
        document.put("PLAYED", 0);
        document.put("KILL", 0);
        document.put("POINT", 1000);
        document.put("CHESTS", 0);
        document.put("HIGHEST_RANK", 0);
        document.put("SIDEBAR_MINIMIZE", false);

        setFirstPlayed(timestamp);
        setWin(document.getInteger("WIN"));
        setPlayed(document.getInteger("PLAYED"));
        setKill(document.getInteger("KILL"));
        setPoint(document.getInteger("POINT"));
        setChests(document.getInteger("CHESTS"));
        setSidebarMinimize(document.getBoolean("SIDEBAR_MINIMIZE"));

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
            setFirstPlayed(new Timestamp(document.getLong("FIRST_PLAYED")));
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
        this.point = amount + this.point;
        updateInteger("POINT", this.point);
    }

    public void withdrawPoint(int amount){
        this.point = this.point - amount;
        updateInteger("POINT", this.point);
    }


    public int calculatedPoint(){
        return (int) (this.point*1.08) - this.point;
    }

    /**
     * 勝ったら持ってるポイントの30％プラス
     * @return
     */
    public int calculatedWinAddPoint(){
        int add = (int) (this.point*1.30) - this.point;
        addPoint(add);
        return add;
    }

    public void updateChests(int amount){
        this.chests = chests+amount;
        updateInteger("CHESTS", this.chests);
    }

    public int getRank() {
        int rank = 1;
        for (Document document : getMongoConnection().getPlayers().find()) {
            if (!document.getString("UUID").equals(uuid)) {
                rank++;
            }
            return rank;
        }
        return rank;
    }

    public void updateSidebarMinimize(boolean sidebarMinimize){
        this.sidebarMinimize = sidebarMinimize;
        updateOne(findByUUID().append("SIDEBAR_MINIMIZE", sidebarMinimize));
    }
}
