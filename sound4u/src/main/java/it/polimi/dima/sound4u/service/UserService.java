package it.polimi.dima.sound4u.service;

import it.polimi.dima.sound4u.dummy.DummyContent;
import it.polimi.dima.sound4u.model.User;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by canidio-andrea on 10/01/14.
 */
public class UserService {

    public static List<User> load(String query) {
        List<User> results = new LinkedList<User>();
        for(User item: DummyContent.USERS) {
            if(item.getFullName().toLowerCase().matches("(.*)" + query.toLowerCase() + "(.*)")) {
                results.add(item);
            }
        }
        return results;
    }
}