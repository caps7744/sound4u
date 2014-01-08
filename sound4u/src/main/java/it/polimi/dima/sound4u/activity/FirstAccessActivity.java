package it.polimi.dima.sound4u.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.fragment.FirstAccessFragment;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.LoginService;

public class FirstAccessActivity extends ActionBarActivity implements FirstAccessFragment.FirstAccessListener {

    private static final int LOGIN_REQUEST_ID = 1;

    public static final String USER_EXTRA = Const.PKG + ".extra.USER_EXTRA";

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
    public User doLogin(String username, String password) {
        final User user = LoginService.get().login(username, password);
        if(user != null) {
            Intent giftsIntent = new Intent(this, MyGiftsActivity.class);
            giftsIntent.putExtra(MyGiftsActivity.USER_EXTRA, user);
            startActivity(giftsIntent);
            finish();
        }
        return user;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN_REQUEST_ID) {
            switch (resultCode) {
                case RESULT_OK:
                    final User user = (User) data.getParcelableExtra(FacebookLoginActivity.USER_EXTRA);
                    final Intent giftsIntent = new Intent(this, MyGiftsActivity.class);
                    giftsIntent.putExtra(MyGiftsActivity.USER_EXTRA, user);
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
