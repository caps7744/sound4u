package it.polimi.dima.sound4u.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

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

    private long id;

    private User sender;

    private User receiver;

    private Sound sound;

    private String message;

    private boolean viewed;

    private Gift(final long id, final User sender, final User receiver, final Sound sound) {
        this.id = id;
        this.sender= sender;
        this.receiver = receiver;
        this.sound = sound;
        viewed = false;
    }

    private Gift(Parcel in) {
        this.id = in.readLong();
        this.sender = in.readParcelable(User.class.getClassLoader());
        this.receiver = in.readParcelable(User.class.getClassLoader());
        this.sound = in.readParcelable(Sound.class.getClassLoader());
        if(in.readByte() == PRESENT) {
            this.message = in.readString();
        }
        this.viewed = in.readByte() == VIEWED;
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

    public String getMessage() {
        return message;
    }

    public boolean isViewed() {
        return viewed;
    }

    public Gift withMessage(final String message) {
        if(message == null) {
            throw new IllegalArgumentException("Message cannot be null!");
        }
        this.message = message;
        return this;
    }

    public Gift markAsViewed() {
        if(viewed) {
            throw new IllegalStateException("Gift cannot be marked as viewed again!");
        }
        this.viewed = true;
        return this;
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
        if(!TextUtils.isEmpty(message)) {
            dest.writeByte(PRESENT);
            dest.writeString(message);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        dest.writeByte(viewed ? VIEWED : NOT_VIEWED);
    }
}
