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
        User user1 = User.create(1L, "dummy1", "dummy1");
        User user2 = User.create(2L, "dummy2", "dummy2");
        User user3 = User.create(3L, "dummy3", "dummy3");
        // Add 3 sample Users
        addItem(user1);
        addItem(user2);
        addItem(user3);
        // Create 3 sample Sounds
        Sound sound1 = Sound.create(1L, "dummysong1").withAuthor("dummyartist1");
        Sound sound2 = Sound.create(2L, "dummysong2").withAuthor("dummyartist2");
        Sound sound3 = Sound.create(3L, "dummysong3").withAuthor("dummyartist3");
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
