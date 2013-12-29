package it.polimi.dima.sound4u.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

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

    private long id;

    private String username;

    private String password;

    /**
     * Initialized to null to represent that no avatar is available.
     */
    private Bitmap avatar = null;

    private String name;

    private String surname;

    private String email;

    /**
     * Initialized to MIN_VALUE to represent that no birthDate is available.
     */
    private long birthDate = Long.MIN_VALUE;

    private String birthPlace;

    private User(final long id, final String username, final String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    private User(Parcel in) {
        this.id = in.readLong();
        this.username = in.readString();
        this.password = in.readString();
        if(in.readByte() == PRESENT) {
            this.avatar = in.readParcelable(Bitmap.class.getClassLoader());
        }
        if(in.readByte() == PRESENT) {
            this.name = in.readString();
        }
        if(in.readByte() == PRESENT) {
            this.surname = in.readString();
        }
        if(in.readByte() == PRESENT) {
            this.email = in.readString();
        }
        if(in.readByte() == PRESENT) {
            this.birthDate = in.readLong();
        }
        if(in.readByte() == PRESENT) {
            this.birthPlace = in.readString();
        }
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

    public Bitmap getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public long getBirthDate() {
        return birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public User withAvatar(final Bitmap avatar) {
        if(avatar == null) {
            throw new IllegalArgumentException("Avatar cannot be null!");
        }
        this.avatar = avatar;
        return this;
    }

    public User removeAvatar() {
        if(!hasAvatar()) {
            throw new IllegalStateException("User already have no avatar!");
        }
        this.avatar = null;
        return  this;
    }

    public boolean hasAvatar() {
        return avatar != null;
    }

    public User withName(final String name) {
        if(name == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        }
        this.name = name;
        return this;
    }

    public User withSurname(final String surname) {
        if(surname == null) {
            throw new IllegalArgumentException("Surname cannot be null!");
        }
        this.surname = surname;
        return this;
    }

    public User withEmail(final String email) {
        if(email == null) {
            throw new IllegalArgumentException("Email cannot be null!");
        }
        this.email = email;
        return this;
    }

    public User withBirthDate(final long birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public User withBirthPlace(final String birthPlace) {
        if(birthPlace == null) {
            throw new IllegalArgumentException("Birth place cannot be null!");
        }
        this.birthPlace = birthPlace;
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
        if(avatar != null ) {
            dest.writeByte(PRESENT);
            dest.writeParcelable(avatar, flags);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(!TextUtils.isEmpty(name)) {
            dest.writeByte(PRESENT);
            dest.writeString(name);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(!TextUtils.isEmpty(surname)) {
            dest.writeByte(PRESENT);
            dest.writeString(surname);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(!TextUtils.isEmpty(email)) {
            dest.writeByte(PRESENT);
            dest.writeString(email);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(birthDate != Long.MIN_VALUE) {
            dest.writeByte(PRESENT);
            dest.writeLong(birthDate);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(!TextUtils.isEmpty(birthPlace)) {
            dest.writeByte(PRESENT);
            dest.writeString(birthPlace);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
    }
}
