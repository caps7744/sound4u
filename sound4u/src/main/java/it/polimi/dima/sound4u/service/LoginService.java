package it.polimi.dima.sound4u.service;

import it.polimi.dima.sound4u.model.User;

/**
 * Created by canidio-andrea on 29/12/13.
 */
public class LoginService {

    private static final String DUMMY_USERNAME = "dummy";

    private static final String DUMMY_PASSWORD = "password";

    private static LoginService instance;

    public synchronized static LoginService get() {
        if (instance == null) {
            instance = new LoginService();
        }
        return instance;
    }

    public User login(String username, String password) {
        User user = null;
        if(DUMMY_USERNAME.equalsIgnoreCase(username) && DUMMY_PASSWORD.equalsIgnoreCase(password)) {
            user = User.create(1L, username, password);
        }
        return user;
    }
}
