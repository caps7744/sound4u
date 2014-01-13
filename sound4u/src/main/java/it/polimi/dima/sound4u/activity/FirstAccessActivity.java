package it.polimi.dima.sound4u.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.conf.SoundCloudConst;
import it.polimi.dima.sound4u.fragment.FirstAccessFragment;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.LoginService;

import java.io.IOException;

public class FirstAccessActivity extends ActionBarActivity implements FirstAccessFragment.FirstAccessListener {

    private static final int LOGIN_REQUEST_ID = 1;

    public static final String USER_EXTRA = Const.PKG + ".extra.USER_EXTRA";

    private static final String TAG_LOG = FirstAccessActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        if (savedInstanceState == null) {
            final FirstAccessFragment fragment = new FirstAccessFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.anchor_point, fragment).commit();
        }
    }

    @Override
    public void doFacebookLogin() {
        final Intent loginIntent = new Intent(FacebookLoginActivity.LOGIN_ACTION);
        startActivityForResult(loginIntent, LOGIN_REQUEST_ID);
    }

    @Override
    public void doLogin(String username, String password) {
        new LoginTask(username, password).execute();
    }

    private class LoginTask extends AsyncTask <Void, Void, User> {

        private String username;

        private String password;

        private ApiWrapper wrapper;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            wrapper = new ApiWrapper(
                    SoundCloudConst.CLIENT_ID,
                    SoundCloudConst.CLIENT_SECRET,
                    null,
                    null
            );
        }

        @Override
        protected User doInBackground(Void... params) {
            User user = null;
            try {
                wrapper.login(username, password, Token.SCOPE_DEFAULT);
                String message = wrapper.get(Request.to("/me")).getEntity().toString();
                Log.w(TAG_LOG, message);
                user = User.create(1L, username).withPassword(password);
                Toast.makeText(FirstAccessActivity.this, "Logged in as " + username, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.w(TAG_LOG, e.getMessage());
                Toast.makeText(FirstAccessActivity.this, "Wrong credentials" + username, Toast.LENGTH_SHORT).show();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            if(user != null) {
                user.save(FirstAccessActivity.this);
                Intent giftsIntent = new Intent(FirstAccessActivity.this, MyGiftsActivity.class);
                startActivity(giftsIntent);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN_REQUEST_ID) {
            switch (resultCode) {
                case RESULT_OK:
                    final User user = (User) data.getParcelableExtra(FacebookLoginActivity.USER_EXTRA);
                    user.save(this);
                    final Intent giftsIntent = new Intent(this, MyGiftsActivity.class);
                    startActivity(giftsIntent);
                    finish();
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.first_access, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
