package it.polimi.dima.sound4u.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by canidio-andrea on 29/12/13.
 */
public class Sound implements Parcelable{

    private static final byte PRESENT = 1;

    private static final byte NOT_PRESENT = 0;

    public static final Creator<Sound> CREATOR = new Creator<Sound>() {
        @Override
        public Sound createFromParcel(Parcel source) {
            return new Sound(source);
        }

        @Override
        public Sound[] newArray(int size) {
            return new Sound[size];
        }
    };

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String ARTWORK_URL = "artwork_url";
    private static final String STREAM_URL = "stream_url";
    public static final String USER = "user";

    private long id;
    private String title;
    private User author;
    private String coverURL;
    private String cover_big;
    private String streamURL;
    private Bitmap cover;

    private Sound(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        if(in.readByte() == PRESENT) {
            this.author = in.readParcelable(User.class.getClassLoader());
        }
        if(in.readByte() == PRESENT) {
            this.coverURL = in.readString();
        }
        if(in.readByte() == PRESENT) {
            this.cover_big = in.readString();
        }
        if(in.readByte() == PRESENT) {
            this.cover = in.readParcelable(Bitmap.class.getClassLoader());
        }
        if(in.readByte() == PRESENT) {
            this.streamURL = in.readString();
        }
    }

    private Sound(final long id, final String title) {
        this.id = id;
        this.title = title;
        this.coverURL = null;
        this.cover_big = null;
        this.cover = null;
    }

    private Sound(JsonObject jsonObject) {
        try{
            this.id = jsonObject.get(ID).asLong();
            this.title = jsonObject.get(TITLE).asString();
            if (!jsonObject.get(ARTWORK_URL).isNull())  {
                this.coverURL = jsonObject.get(ARTWORK_URL).asString();
                this.cover_big = this.coverURL.replace("large","t500x500");
            } else {
                this.coverURL = null;
                this.cover_big = null;
            }
            cover = null;
            JsonObject jsonUser = jsonObject.get(USER).asObject();
            this.author = User.create(jsonUser);
            if (jsonObject.get(STREAM_URL) != null && !jsonObject.get(STREAM_URL).isNull()) {
                this.streamURL = jsonObject.get(STREAM_URL).asString();
            } else {
                this.streamURL = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Sound create(final long id, final String title) {
        return new Sound(id, title);
    }

    public static Sound create(final JsonObject jsonObject) {
        return new Sound(jsonObject);
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public User getAuthor() {
        return author;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public String getCoverBig() { return cover_big; }

    public String getStreamURL() {
        return streamURL;
    }

    public Bitmap getCover() {
        return cover;
    }

    public Sound withAuthor(User author) {
        this.author = author;
        return this;
    }

    public Sound withCoverURL(String cover) {
        this.coverURL = cover;
        if(this.coverURL !=null){
        this.cover_big = cover.replace("large","t500x500");
        } else {
            this.cover_big = null;
        }
        return this;
    }

    public Sound withCover(Bitmap cover) {
        this.cover = cover;
        return this;
    }

    public Sound withStreamURL(String urlStream) {
        this.streamURL = urlStream;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        if(author != null) {
            dest.writeByte(PRESENT);
            dest.writeParcelable(author, flags);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(!TextUtils.isEmpty(coverURL)) {
            dest.writeByte(PRESENT);
            dest.writeString(coverURL);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(!TextUtils.isEmpty(cover_big)) {
            dest.writeByte(PRESENT);
            dest.writeString(cover_big);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(cover != null) {
            dest.writeByte(PRESENT);
            dest.writeParcelable(cover, flags);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(!TextUtils.isEmpty(streamURL)) {
            dest.writeByte(PRESENT);
            dest.writeString(streamURL);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
    }

    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        object.add(ID, id);
        object.add(TITLE, title);
        object.add(ARTWORK_URL, coverURL);
        object.add(STREAM_URL, streamURL);
        if (author != null) {
            object.add(USER, author.toJsonObject());
        }
        return object;
    }

    public static String listToJson(List<Sound> soundList) {
        JsonArray array = new JsonArray();
        for(Sound item: soundList) {
            JsonObject object = new JsonObject();
            object.add(ID, item.getId());
            object.add(TITLE, item.getTitle());
            object.add(USER, item.getAuthor().toJsonObject());
            object.add(ARTWORK_URL, item.getCoverURL());
            object.add(STREAM_URL, item.getStreamURL());
            array.add(object);
        }
        return array.toString();
    }

    public static List<Sound> jsonToList(String jsonString) {
        List<Sound> list = new LinkedList<Sound>();
        JsonArray array = JsonArray.readFrom(jsonString);
        for(JsonValue item: array) {
            Sound soundItem = Sound.create((JsonObject) item);
            list.add(soundItem);
        }
        return list;
    }
}
