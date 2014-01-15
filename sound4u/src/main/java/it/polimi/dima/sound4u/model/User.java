package it.polimi.dima.sound4u.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.eclipsesource.json.JsonObject;
import it.polimi.dima.sound4u.conf.Const;

/**
 * Created by canidio-andrea on 28/12/13.
 */
public class User implements Parcelable{

    private static final byte PRESENT = 1;

    private static final byte NOT_PRESENT = 0;

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

    private static final String ID_KEY = Const.PKG + ".key.ID_KEY";

    private static final String USERNAME_KEY = Const.PKG + ".key.USERNAME_KEY";

    private static final String AVATAR_KEY = Const.PKG + ".key.AVATAR_KEY";

    private static final String PASSWORD_KEY = Const.PKG + ".key.PASSWORD_KEY";

    private static final String ID = "id";

    private static final String USERNAME = "username";

    private static final String AVATAR = "avatar_url";

    private long id;

    private String username;

    private String password;

    private String avatar;

    private User(final long id, final String username) {
        this.id = id;
        this.username = username;
    }

    private User(Parcel in) {
        this.id = in.readLong();
        this.username = in.readString();
        this.password = in.readString();
        if(in.readByte() == PRESENT) {
            this.avatar = in.readString();
        }
    }

    private User(String JsonString) {
        JsonObject jsonObject = JsonObject.readFrom(JsonString);
        this.id = jsonObject.get(ID).asLong();
        this.username = jsonObject.get(USERNAME).asString();
        this.avatar = jsonObject.get(AVATAR).asString();
    }

    public static User create(final long id, final String username) {
        final User user = new User(id, username);
        return user;
    }

    public static User create(final String jsonString){
        return new User(jsonString);
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

    public String getAvatar() {
        return avatar;
    }

    public User withPassword(final String password) {
        this.password = password;
        return this;
    }

    public User withAvatar(final String avatar) {
        this.avatar = avatar;
        return this;
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
        if(!TextUtils.isEmpty(avatar)) {
            dest.writeByte(PRESENT);
            dest.writeString(avatar);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
    }

    public void save(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(ID_KEY, id);
        editor.putString(USERNAME_KEY, username);
        editor.putString(AVATAR_KEY, avatar);
        editor.putString(PASSWORD_KEY, password);
        editor.commit();
    }

    public static User load(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        long id = preferences.getLong(ID_KEY, 0);
        String username = preferences.getString(USERNAME_KEY, null);
        User user = null;
        if (username != null) {
            user = new User(id, username);
            user.avatar = preferences.getString(AVATAR_KEY, null);
            user.password = preferences.getString(PASSWORD_KEY, null);
        }
        return user;
    }

    public void logout(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
    }
}
