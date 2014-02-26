package it.polimi.dima.sound4u.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by canidio-andrea on 28/12/13.
 */
public class Gift implements Parcelable{

    private static final byte PRESENT = 1;

    private static final byte NOT_PRESENT = 0;

    public static final byte VIEWED = 1;

    public static final byte NOT_VIEWED = 0;

    public static final Creator<Gift> CREATOR = new Creator<Gift>() {
        @Override
        public Gift createFromParcel(Parcel source) {
            return new Gift(source);
        }

        @Override
        public Gift[] newArray(int size) {
            return new Gift[size];
        }
    };
    public static final String ID = "id";
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String SOUND = "sound";

    private long id;

    private User sender;

    private User receiver;

    private Sound sound;

    private Gift(final long id, final User sender, final User receiver, final Sound sound) {
        this.id = id;
        this.sender= sender;
        this.receiver = receiver;
        this.sound = sound;
    }

    private Gift(Parcel in) {
        this.id = in.readLong();
        Log.w(Gift.class.getName(), "letto id");
        this.sender = in.readParcelable(User.class.getClassLoader());
        Log.w(Gift.class.getName(), "letto sender");
        this.receiver = in.readParcelable(User.class.getClassLoader());
        Log.w(Gift.class.getName(), "letto receiver");
        this.sound = in.readParcelable(Sound.class.getClassLoader());
        Log.w(Gift.class.getName(), "letto sound");
    }

    public static Gift create(final long id, final User sender, final User receiver, final Sound sound) {
        return  new Gift(id, sender, receiver, sound);
    }

    public long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public Sound getSound() {
        return sound;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(sender, flags);
        dest.writeParcelable(receiver, flags);
        dest.writeParcelable(sound, flags);
    }

    public static String listToJson(List<Gift> giftList) {
        JsonArray array = new JsonArray();
        for(Gift item: giftList) {
            JsonObject object = new JsonObject();
            object.add(ID, item.getId());
            object.add(SENDER, item.getSender().toJsonObject());
            object.add(RECEIVER, item.getReceiver().toJsonObject());
            object.add(SOUND, item.getSound().toJsonObject());
            array.add(object);
        }
        return array.toString();
    }

    public static List<Gift> jsonToList(String jsonString) {
        List<Gift> list = new LinkedList<Gift>();
        JsonArray array = JsonArray.readFrom(jsonString);
        for(JsonValue item: array) {
            JsonObject object = (JsonObject) item;
            Long id = object.get(ID).asLong();
            JsonObject jsonSender = object.get(SENDER).asObject();
            User sender = User.create(jsonSender);
            JsonObject jsonReceiver = object.get(RECEIVER).asObject();
            User receiver = User.create(jsonReceiver);
            JsonObject jsonSound = object.get(SOUND).asObject();
            Sound sound = Sound.create(jsonSound);
            Gift gift = new Gift(id, sender, receiver, sound);
            list.add(gift);
        }
        return list;
    }
}
