package it.polimi.dima.sound4u.service;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import it.polimi.dima.sound4u.model.User;

/**
 * Created by canidio-andrea on 30/12/13.
 */
public class SignupService {

    private static SignupService instance;

    public synchronized static SignupService get(){
        if (instance == null) {
            instance = new SignupService();
        }
        return instance;
    }

    public User signup(final String username, final String password) {
        User user = User.create(1L, username, password);
        return user;
    }

    public User addAvatar(final User user, final Bitmap avatar) {
        return user.withAvatar(avatar);
    }

    public User addName(final User user, final String name) {
        return user.withName(name);
    }

    public User addSurname(final User user, final String surname) {
        return user.withSurname(surname);
    }

    public User addEmail(final User user, final String email) {
        return user.withEmail(email);
    }

    public User addBirthDate(final User user, final long birthDate) {
        return user.withBirthDate(birthDate);
    }

    public User addBirthPlace(final User user, final String birthPlace) {
        return user.withBirthPlace(birthPlace);
    }

    public boolean alreadyExists(String username) {
        return false;
    }
}
