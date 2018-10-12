package net.hotsmc.sg.utility;

import net.hotsmc.sg.HSG;
import org.bson.Document;

public class NameUtility {

    public static String getNameByUUID(String uuid){
        Document document = HSG.getInstance().getMongoConnection().getPlayers().find(MongoUtility.find("UUID", uuid)).first();
        if(document == null){
            return null;
        }
        return document.getString("NAME");
    }

    public static String getUUIDByName(String name){
        Document document = HSG.getInstance().getMongoConnection().getPlayers().find(MongoUtility.find("NAME", name)).first();
        if(document == null){
            return null;
        }
        return document.getString("UUID");
    }
}
