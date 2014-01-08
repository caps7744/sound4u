package it.polimi.dima.sound4u.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by canidio-andrea on 28/12/13.
 */
public class User implements Parcelable{

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private long id;

    private String username;

    private String password;

    private User(final long id, final String username, final String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    private User(Parcel in) {
        this.id = in.readLong();
        this.username = in.readString();
        this.password = in.readString();
    }

    public static User create(final long id, final String username, final String password) {
        final User user = new User(id, username, password);
        return user;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeString(password);
    }
}
