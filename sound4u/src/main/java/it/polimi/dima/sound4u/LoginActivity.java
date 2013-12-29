package it.polimi.dima.sound4u;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.LoginService;

public class LoginActivity extends ActionBarActivity {

    public static final String LOGIN_ACTION = "it.polimi.dima.sound4u.action.LOGIN_ACTION";

    public static final String USER_DATA_EXTRA = "it.polimi.dima.sound4u.extra.USER_DATA_EXTRA";

    private EditText usernameEditText;

    private EditText passwordEditText;

    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        errorTextView = (TextView) findViewById(R.id.error_message_label);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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

    public void doLogin(View loginButton) {
        this.errorTextView.setVisibility(View.INVISIBLE);
        final Editable usernameEditable = usernameEditText.getText();
        if (TextUtils.isEmpty(usernameEditable)) {
            final String usernameMandatory = getResources().getString(R.string.mandatory_field_error, "username");
            this.errorTextView.setText(usernameMandatory);
            this.errorTextView.setVisibility(View.VISIBLE);
            return;
        }
        final Editable passwordEditable = passwordEditText.getText();
        if(TextUtils.isEmpty(passwordEditable)) {
            final String passwordMandatory = getResources().getString(R.string.mandatory_field_error, "password");
            this.errorTextView.setText(passwordMandatory);
            this.errorTextView.setVisibility(View.VISIBLE);
            return;
        }
        final String username = usernameEditable.toString();
        final String password = passwordEditable.toString();
        final User user = LoginService.get().login(username, password);
        if(user != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(USER_DATA_EXTRA, user);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            this.errorTextView.setText(R.string.wrong_credential_error);
            this.errorTextView.setVisibility(View.VISIBLE);
        }
    }

}
