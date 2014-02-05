package it.polimi.dima.sound4u.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.eclipsesource.json.JsonObject;

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
    private String cover;
    private String urlStream;

    private Sound(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        if(in.readByte() == PRESENT) {
            this.author = in.readParcelable(User.class.getClassLoader());
        }
        if(in.readByte() == PRESENT) {
            this.cover = in.readString();
        }
        if(in.readByte() == PRESENT) {
            this.urlStream = in.readString();
        }
    }

    private Sound(final long id, final String title) {
        this.id = id;
        this.title = title;
        this.cover = null;
    }

    private Sound(JsonObject jsonObject) {
        try{
            this.id = jsonObject.get(ID).asLong();
            this.title = jsonObject.get(TITLE).asString();
            if(!(jsonObject.get(ARTWORK_URL)==null))  {
            this.cover = jsonObject.get(ARTWORK_URL).asString();
            } else {
                this.cover = "";
            }
            JsonObject jsonUser = jsonObject.get(USER).asObject();
            this.author = User.create(jsonUser);
            this.urlStream = jsonObject.get(STREAM_URL).asString();
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

    public String getCover() {
        return cover;
    }

    public String getURLStream() {
        return urlStream;
    }

    public Sound withAuthor(User author) {
        this.author = author;
        return this;
    }

    public Sound withCover(String cover) {
        this.cover = cover;
        return this;
    }

    public Sound withURLStream(String urlStream) {
        this.urlStream = urlStream;
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
        if(!TextUtils.isEmpty(cover)) {
            dest.writeByte(PRESENT);
            dest.writeString(cover);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(!TextUtils.isEmpty(urlStream)) {
            dest.writeByte(PRESENT);
            dest.writeString(urlStream);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
    }
}
