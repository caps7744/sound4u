package it.polimi.dima.sound4u;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.User;

public class FirstAccessActivity extends ActionBarActivity {

    private static final int LOGIN_REQUEST_ID = 1;

    private static final int SIGNUP_REQUEST_ID = 2;

    public static final String USER_EXTRA = Const.PKG + ".extra.USER_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        final Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        final Button signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignUp();
            }
        });
    }

    private void doSignUp() {
        final Intent signupIntent = new Intent(SignupActivity.SIGNUP_ACTION);
        startActivityForResult(signupIntent, SIGNUP_REQUEST_ID);
    }

    private void doLogin() {
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
                    final User user = (User) data.getParcelableExtra(SignupActivity.USER_EXTRA);
                    final Intent menuIntent = new Intent(this, MyGiftsActivity.class);
                    menuIntent.putExtra(MyGiftsActivity.USER_EXTRA, user);
                    startActivity(menuIntent);
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
