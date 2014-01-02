package it.polimi.dima.sound4u;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.SignupService;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SignupActivity extends ActionBarActivity {

    public static final String SIGNUP_ACTION = Const.PKG + ".action.SIGNUP_ACTION";

    public static final String USER_EXTRA = Const.PKG + ".extra.USER_EXTRA";

    private ImageView avatarImageView;

    private EditText usernameEditText;

    private EditText passwordEditText;

    private EditText confirmPasswordEditText;

    private EditText emailEditText;

    private EditText nameEditText;

    private EditText surnameEditText;

    private EditText birthDateEditText;

    private EditText birthPlaceEditText;

    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        avatarImageView = (ImageView) findViewById(R.id.avatar_view);
        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        confirmPasswordEditText = (EditText) findViewById(R.id.password2_edittext);
        emailEditText = (EditText) findViewById(R.id.email_edittext);
        nameEditText = (EditText) findViewById(R.id.name_edittext);
        surnameEditText = (EditText) findViewById(R.id.surname_edittext);
        birthDateEditText = (EditText) findViewById(R.id.birthdate_picker);
        birthPlaceEditText = (EditText) findViewById(R.id.birthplace_edittext);
        errorTextView = (TextView) findViewById(R.id.error_message_label);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.singup, menu);
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

    public void doSignup(View signupButton) {
        errorTextView.setVisibility(View.INVISIBLE);
        final Editable usernameEditable = usernameEditText.getText();
        if(TextUtils.isEmpty(usernameEditable)) {
            final String usernameMandatory = getResources().getString(R.string.mandatory_field_error,"username");
            this.errorTextView.setText(usernameMandatory);
            this.errorTextView.setVisibility(View.VISIBLE);
            return;
        }
        final Editable passwordEditable = passwordEditText.getText();
        final Editable confirmPasswordEditable = confirmPasswordEditText.getText();
        if (TextUtils.isEmpty(passwordEditable) || TextUtils.isEmpty(confirmPasswordEditable)) {
            final String passwordMandatory = getResources().getString(R.string.mandatory_field_error,"password");
            this.errorTextView.setText(passwordMandatory);
            this.errorTextView.setVisibility(View.VISIBLE);
            return;
        }
        final String password = passwordEditable.toString();
        final String confirmPassword = confirmPasswordEditable.toString();
        if(!password.equals(confirmPassword)) {
            final String passwordCheck = getResources().getString(R.string.passwords_equals);
            this.errorTextView.setText(passwordCheck);
            this.errorTextView.setVisibility(View.VISIBLE);
            return;
        }
        final String username = usernameEditable.toString();
        final Drawable avatar = avatarImageView.getDrawable();
        final String email = emailEditText.getText().toString();
        final String name = nameEditText.getText().toString();
        final String surname = surnameEditText.getText().toString();
        final String birthDateString = birthDateEditText.getText().toString();
        final String birthPlace = birthPlaceEditText.getText().toString();
        if (SignupService.get().alreadyExists(username)) {
            String usernameUsed = getResources().getString(R.string.username_already_used, username);
            this.errorTextView.setText(usernameUsed);
            this.errorTextView.setVisibility(View.VISIBLE);
        } else {
            User user  = SignupService.get().signup(username, password);
            // TODO Management of the image. We have to study this well.
            if (!TextUtils.isEmpty(email)) {
                user = SignupService.get().addEmail(user, email);
            }
            if (!TextUtils.isEmpty(name)) {
                user = SignupService.get().addName(user, name);
            }
            if (!TextUtils.isEmpty(surname)) {
                user = SignupService.get().addSurname(user, surname);
            }
            if (!TextUtils.isEmpty(birthDateString)) {
                try {
                    final long birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(birthDateString).getTime();
                    user = SignupService.get().addBirthDate(user, birthDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(birthPlace)) {
                user = SignupService.get().addBirthPlace(user, birthPlace);
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra(USER_EXTRA, user);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

}
