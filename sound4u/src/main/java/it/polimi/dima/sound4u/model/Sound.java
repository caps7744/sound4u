package it.polimi.dima.sound4u.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

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

    private long id;

    private String title;

    private String author;

    private Bitmap cover;

    private Sound(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        if(in.readByte() == PRESENT) {
            this.author = in.readString();
        }
        if(in.readByte() == PRESENT) {
            this.cover = in.readParcelable(Bitmap.class.getClassLoader());
        }
    }

    private Sound(final long id, final String title) {
        this.id = id;
        this.title = title;
        this.cover = null;
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

    public String getAuthor() {
        return author;
    }

    public Bitmap getCover() {
        return cover;
    }

    public Sound withAuthor(String author) {
        if(author == null) {
            throw new IllegalArgumentException("Author cannot be null!");
        }
        this.author = author;
        return this;
    }

    public Sound withCover(Bitmap cover) {
        if(cover == null) {
            throw new IllegalArgumentException("Cover cannot be null!");
        }
        this.cover = cover;
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
        if(!TextUtils.isEmpty(author)) {
            dest.writeByte(PRESENT);
            dest.writeString(author);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(cover != null) {
            dest.writeByte(PRESENT);
            dest.writeParcelable(cover, flags);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
    }
}
