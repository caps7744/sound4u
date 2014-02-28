package it.polimi.dima.sound4u.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.soundcloud.api.Token;
import it.polimi.dima.sound4u.conf.Const;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by canidio-andrea on 28/12/13.
 */
public class User implements Parcelable{

    private static final byte PRESENT = 1;

    private static final byte NOT_PRESENT = 0;

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            try {
                return new User(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private static final String ID_KEY = Const.PKG + ".key.ID_KEY";

    private static final String USERNAME_KEY = Const.PKG + ".key.USERNAME_KEY";

    private static final String AVATAR_KEY = Const.PKG + ".key.AVATAR_KEY";

    private static final String TOKEN_KEY = Const.PKG + ".key.TOKEN_KEY";

    private static final String FULLNAME_KEY = Const.PKG + ".key.FULLNAME_KEY";

    private static final String ID = "id";

    private static final String USERNAME = "username";

    private static final String FULLNAME = "full_name";

    private static final String AVATAR = "avatar_url";

    private long id;

    private String username;

    private String full_name;

    private Token token;

    private String avatarURL;

    private Bitmap avatar;

    private User(final long id, final String username) {
        this.id = id;
        this.username = username;
        this.avatar = null;
    }

    private User(Parcel in) throws IOException {
        this.id = in.readLong();
        this.username = in.readString();
        if(in.readByte() == PRESENT) {
            this.full_name = in.readString();
        }
        if(in.readByte() == PRESENT) {
            this.avatarURL = in.readString();
        }
        if(in.readByte() == PRESENT) {
            this.avatar = in.readParcelable(Bitmap.class.getClassLoader());
        }
    }

    private User(JsonObject jsonObject) {
        this.id = jsonObject.get(ID).asLong();
        this.username = jsonObject.get(USERNAME).asString();
        if(jsonObject.get(FULLNAME) != null && !jsonObject.get(FULLNAME).isNull()) {
            this.full_name = jsonObject.get(FULLNAME).asString();
        } else {
            this.full_name = "";
        }
        if(!jsonObject.get(AVATAR).isNull()) {
            this.avatarURL = jsonObject.get(AVATAR).asString();
        } else {
            this.avatarURL = "";
        }
    }

    public static User create(final long id, final String username) {
        final User user = new User(id, username);
        return user;
    }

    public static User create(final JsonObject jsonString){
        return new User(jsonString);
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return full_name;
    }

    public Token getToken() {
        return token;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public User withToken(final Token token) {
        this.token = token;
        return this;
    }

    public User withAvatarURL(final String avatar) {
        this.avatarURL = avatar;
        return this;
    }

    public User withAvatar(final Bitmap avatar) {
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
        if(!TextUtils.isEmpty(full_name)) {
            dest.writeByte(PRESENT);
            dest.writeString(full_name);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(!TextUtils.isEmpty(avatarURL)) {
            dest.writeByte(PRESENT);
            dest.writeString(avatarURL);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(avatar != null) {
            dest.writeByte(PRESENT);
            dest.writeParcelable(avatar, flags);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
    }

    public void save(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(ID_KEY, id);
        editor.putString(FULLNAME_KEY, full_name);
        editor.putString(USERNAME_KEY, username);
        editor.putString(AVATAR_KEY, avatarURL);
        editor.putString(TOKEN_KEY, token.access);
        editor.commit();
    }

    public static User load(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        long id = preferences.getLong(ID_KEY, 0);
        String username = preferences.getString(USERNAME_KEY, null);
        User user = null;
        if (username != null) {
            user = new User(id, username);
            user.full_name = preferences.getString(FULLNAME_KEY, null);
            user.avatarURL = preferences.getString(AVATAR_KEY, null);
            user.token = new Token(preferences.getString(TOKEN_KEY, null), null, Token.SCOPE_NON_EXPIRING);
        }
        return user;
    }

    public void logout(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
    }

    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        object.add(ID, id);
        object.add(USERNAME, username);
        object.add(FULLNAME, full_name);
        object.add(AVATAR, avatarURL);
        return  object;
    }

    public static List<User> jsonToList(String jsonString) {
        List<User> list = new LinkedList<User>();
        JsonArray array = JsonArray.readFrom(jsonString);
        for (JsonValue item: array) {
            User user = User.create((JsonObject) item);
            list.add(user);
        }
        return list;
    }

    public static String listToJson(List<User> list) {
        JsonArray array = new JsonArray();
        for(User item: list) {
            JsonObject object = new JsonObject();
            object.add(ID, item.getId());
            object.add(USERNAME, item.getUsername());
            object.add(FULLNAME, item.getFullName());
            object.add(AVATAR, item.getAvatarURL());
            array.add(object);
        }
        return array.toString();
    }


}
