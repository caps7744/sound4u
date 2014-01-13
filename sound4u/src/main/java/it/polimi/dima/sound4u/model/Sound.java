package it.polimi.dima.sound4u.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

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

        public Sound createFromJSOM(JSONObject jsonObject){
            return new Sound(jsonObject);
        }

        @Override
        public Sound[] newArray(int size) {
            return new Sound[size];
        }
    };

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

    public Sound(JSONObject s) {
        try {
            this.id = s.getLong("id");
            this.title = s.getString("title");
            this.cover = s.getString("waveform_url");
            this.urlStream = s.getString("stream_url");
        } catch (JSONException e) {
            // TO IMPLEMENT
            e.printStackTrace();
        }
    }

    public static Sound create(final long id, final String title) {
        return new Sound(id, title);
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
