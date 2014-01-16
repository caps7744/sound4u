package it.polimi.dima.sound4u.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.fragment.FirstAccessFragment;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.LoginTask;

public class FirstAccessActivity extends ActionBarActivity implements FirstAccessFragment.FirstAccessListener {

    private static final int LOGIN_REQUEST_ID = 1;

    public static final String USER_EXTRA = Const.PKG + ".extra.USER_EXTRA";

    public static final String TAG_LOG = FirstAccessActivity.class.getName();

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
    public void doLogin(String username, String password) {
        new LoginTask(this, username, password).execute();
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
