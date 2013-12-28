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

    private long sender_id;

    private long receiver_id;

    private long sound_id;

    private String message;

    private byte viewed;

    private Gift(final long id, final long sender_id, final long receiver_id, final long sound_id) {
        this.id = id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.sound_id = sound_id;
        viewed = VIEWED;
    }

    private Gift(Parcel in) {
        this.id = in.readLong();
        this.sender_id = in.readLong();
        this.receiver_id = in.readLong();
        this.sound_id = in.readLong();
        if(in.readByte() == PRESENT) {
            this.message = in.readString();
        }
        this.viewed = in.readByte();
    }

    public static Gift create(final long id, final long sender_id, final long receiver_id, final long sound_id) {
        return  new Gift(id, sender_id, receiver_id, sound_id);
    }

    public long getId() {
        return id;
    }

    public long getSenderId() {
        return sender_id;
    }

    public long getReceiverId() {
        return receiver_id;
    }

    public long getSoundId() {
        return sound_id;
    }

    public String getMessage() {
        return message;
    }

    public boolean isViewed() {
        return viewed == VIEWED;
    }

    public Gift withMessage(final String message) {
        if(message == null) {
            throw new IllegalArgumentException("Message cannot be null!");
        }
        this.message = message;
        return this;
    }

    public Gift markAsViewed() {
        if(viewed == NOT_VIEWED) {
            throw new IllegalStateException("Gift cannot be marked as viewed again!");
        }
        this.viewed = VIEWED;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(sender_id);
        dest.writeLong(receiver_id);
        dest.writeLong(sound_id);
        if(!TextUtils.isEmpty(message)) {
            dest.writeByte(PRESENT);
            dest.writeString(message);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        dest.writeByte(viewed);
    }
}
