package it.polimi.dima.sound4u.service;

import it.polimi.dima.sound4u.dummy.DummyContent;
import it.polimi.dima.sound4u.model.Gift;

import java.util.List;

/**
 * Created by canidio-andrea on 09/01/14.
 */
public class GiftService {

    public static List<Gift> load() {
        return DummyContent.GIFTS;
    }
}
