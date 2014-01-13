package it.polimi.dima.sound4u.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.conf.SoundCloudConst;
import it.polimi.dima.sound4u.dummy.DummyContent;
import it.polimi.dima.sound4u.model.User;
import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Created by canidio-andrea on 29/12/13.
 */
public class LoginService extends IntentService {

    private static final String LOG_TAG = LoginService.class.getName();

    private static final String LOGIN_ACTION = Const.PKG + ".action.LOGIN_ACTION";

    private static final String USERNAME_EXTRA = Const.PKG + ".extra.USERNAME_EXTRA";

    private static final String PASSWORD_EXTRA = Const.PKG + ".extra.PASSWORD_EXTRA";

    private String username;

    private String password;

    public LoginService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Get the Extras from the Intent
        username = intent.getStringExtra(USERNAME_EXTRA);
        password = intent.getStringExtra(PASSWORD_EXTRA);
        // Instantiate the APIWrapper of SoundCloud
        ApiWrapper wrapper = new ApiWrapper(
                SoundCloudConst.CLIENT_ID,
                SoundCloudConst.CLIENT_SECRET,
                null,
                null
        );
        // Log in
        try {
            wrapper.login(username, password, Token.SCOPE_DEFAULT);
            HttpResponse resp = wrapper.get(Request.to("/me"));
            Log.w(LOG_TAG, resp.getEntity().toString());
        } catch (IOException e) {
            Log.w(LOG_TAG, e.getMessage());
        }
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
