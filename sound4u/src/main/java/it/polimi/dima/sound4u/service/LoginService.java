package it.polimi.dima.sound4u.service;

import it.polimi.dima.sound4u.dummy.DummyContent;
import it.polimi.dima.sound4u.model.User;

/**
 * Created by canidio-andrea on 29/12/13.
 */
public class LoginService {

    private static LoginService instance;

    public synchronized static LoginService get() {
        if (instance == null) {
            instance = new LoginService();
        }
        return instance;
    }

    public User login(String username, String password) {
        for(User user: DummyContent.USERS) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
