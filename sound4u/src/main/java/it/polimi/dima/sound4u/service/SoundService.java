package it.polimi.dima.sound4u.service;

import it.polimi.dima.sound4u.dummy.DummyContent;
import it.polimi.dima.sound4u.model.Sound;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by canidio-andrea on 09/01/14.
 */
public class SoundService {

    public static List<Sound> load(String query) {
        List<Sound> results = new LinkedList<Sound>();
        for(Sound item: DummyContent.SOUNDS) {
            if(item.getTitle().toLowerCase().matches("(.*)" + query.toLowerCase() + "(.*)")) {
                results.add(item);
            }
        }
        return results;
    }

}
