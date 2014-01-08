package it.polimi.dima.sound4u.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.fragment.FirstAccessFragment;
import it.polimi.dima.sound4u.model.User;

public class FirstAccessActivity extends FragmentActivity implements FirstAccessFragment.FirstAccessListener {

    private static final int LOGIN_REQUEST_ID = 1;

    private static final int SIGNUP_REQUEST_ID = 2;

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

    public void doSignUp() {
        // TODO Nothing, I have only to refactor a bit.
    }

    public void doLogin() {
        final Intent loginIntent = new Intent(LoginActivity.LOGIN_ACTION);
        startActivityForResult(loginIntent, LOGIN_REQUEST_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN_REQUEST_ID) {
            switch (resultCode) {
                case RESULT_OK:
                    final User user = (User) data.getParcelableExtra(LoginActivity.USER_EXTRA);
                    final Intent menuIntent = new Intent(this, MyGiftsActivity.class);
                    menuIntent.putExtra(MyGiftsActivity.USER_EXTRA, user);
                    startActivity(menuIntent);
                    finish();
                    break;
                case RESULT_CANCELED:
                    break;
            }
        } else if (requestCode == SIGNUP_REQUEST_ID) {
            switch (resultCode) {
                case RESULT_OK:
                    finish();
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first_access, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
