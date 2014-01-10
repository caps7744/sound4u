package it.polimi.dima.sound4u.dummy;

import it.polimi.dima.sound4u.model.Gift;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<Gift> GIFTS = new ArrayList<Gift>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<Long, Gift> GIFT_MAP = new HashMap<Long, Gift>();

    public static List<User> USERS = new ArrayList<User>();

    public static Map<Long, User> USER_MAP = new HashMap<Long, User>();

    public static List<Sound> SOUNDS = new ArrayList<Sound>();

    public static Map<Long, Sound> SOUND_MAP = new HashMap<Long, Sound>();

    static {
        // Create 3 sample Users
        User user1 = User.create(1L, "canidio-andrea").withPassword("canidio-andrea");
        User user2 = User.create(2L, "Calico Jack (folk metal)").withPassword("calico-jack").withAvatar("https://i1.sndcdn.com/avatars-000056522945-ux8xxk-large.jpg?d53bf9f");
        User user3 = User.create(3L, "caputo-paolo").withPassword("caputo-paolo");
        User user4 = User.create(4L, "brambilla-davide").withPassword("brambilla-davide");
        // Add 3 sample Users
        addItem(user1);
        addItem(user2);
        addItem(user3);
        addItem(user4);
        // Create 3 sample Sounds
        Sound sound1 = Sound.create(113950405L, "Calico Jack - Deadly Day in Bounty Bay").withAuthor(user2)
                .withCover("https://i1.sndcdn.com/artworks-000059391690-x9xipr-large.jpg?d53bf9f")
                .withURLStream("https://api.soundcloud.com/tracks/113950405/stream");
        Sound sound2 = Sound.create(113950124L, "Calico Jack - Grog Jolly Grog").withAuthor(user2)
                .withCover("https://i1.sndcdn.com/artworks-000059391548-bgy0rc-large.jpg?d53bf9f")
                .withURLStream("https://api.soundcloud.com/tracks/113950124/stream");
        Sound sound3 = Sound.create(113949799L, "Calico Jack - House of Jewelry").withAuthor(user2)
                .withCover("https://i1.sndcdn.com/artworks-000059391362-421y1d-large.jpg?d53bf9f")
                .withURLStream("https://api.soundcloud.com/tracks/113949799/stream");
        // Add 3 sample Sounds
        addItem(sound1);
        addItem(sound2);
        addItem(sound3);
        // Add 3 sample Gifts
        addItem(Gift.create(1L, user1, user2, sound1));
        addItem(Gift.create(2L, user2, user3, sound2));
        addItem(Gift.create(3L, user2, user3, sound1));
    }

    private static void addItem(User item) {
        USERS.add(item);
        USER_MAP.put(item.getId(), item);
    }

    private static void addItem(Sound item) {
        SOUNDS.add(item);
        SOUND_MAP.put(item.getId(), item);
    }

    private static void addItem(Gift item) {
        GIFTS.add(item);
        GIFT_MAP.put(item.getId(), item);
    }
}
