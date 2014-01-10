package it.polimi.dima.sound4u.service;

import it.polimi.dima.sound4u.dummy.DummyContent;
import it.polimi.dima.sound4u.model.Gift;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.model.User;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by canidio-andrea on 09/01/14.
 */
public class GiftService {

    public static List<Gift> load(User owner) {
        List<Gift> results = new LinkedList<Gift>();
        for(Gift item: DummyContent.GIFTS) {
            if (item.getSender().getId() == owner.getId()) {
                results.add(item);
            } else if (item.getReceiver().getId() == owner.getId()){
                results.add(item);
            }
        }
        return results;
    }

    public static boolean sendGift(User sender, User receiver, Sound sound) {
        Gift item = Gift.create(DummyContent.GIFTS.size(), sender, receiver, sound);
        DummyContent.GIFTS.add(item);
        DummyContent.GIFT_MAP.put(item.getId(), item);
        return true;
    }
}
