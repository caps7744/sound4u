package it.polimi.dima.sound4u.service;

import it.polimi.dima.sound4u.dummy.DummyContent;
import it.polimi.dima.sound4u.model.User;

import java.util.List;

/**
 * Created by canidio-andrea on 10/01/14.
 */
public class UserService {

    public static List<User> load() {
        return DummyContent.USERS;
    }
}
