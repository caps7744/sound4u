package it.polimi.dima.sound4u.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.activity.MyGiftsActivity;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.LoginService;

/**
 * Created by canidio-andrea on 06/01/14.
 */
public class FirstAccessFragment extends Fragment {

    public interface FirstAccessListener {

        User doLogin(String username, String password);

        void doFacebookLogin();

    }

    private FirstAccessListener mListener;

    private EditText usernameEditText;

    private EditText passwordEditText;

    private TextView errorTextView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof FirstAccessListener) {
            mListener = (FirstAccessListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View firstAccessView = inflater.inflate(R.layout.fragment_first, null);
        usernameEditText = (EditText) firstAccessView.findViewById(R.id.username_edittext);
        passwordEditText = (EditText) firstAccessView.findViewById(R.id.password_edittext);
        errorTextView = (TextView) firstAccessView.findViewById(R.id.error_message_label);
        firstAccessView.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    errorTextView.setVisibility(View.INVISIBLE);
                    final Editable usernameEditable = usernameEditText.getText();
                    if (TextUtils.isEmpty(usernameEditable)) {
                        final String usernameMandatory = getResources().getString(R.string.mandatory_field_error, "username");
                        errorTextView.setText(usernameMandatory);
                        errorTextView.setVisibility(View.VISIBLE);
                        return;
                    }
                    final Editable passwordEditable = passwordEditText.getText();
                    if(TextUtils.isEmpty(passwordEditable)) {
                        final String passwordMandatory = getResources().getString(R.string.mandatory_field_error, "password");
                        errorTextView.setText(passwordMandatory);
                        errorTextView.setVisibility(View.VISIBLE);
                        return;
                    }
                    final String username = usernameEditable.toString();
                    final String password = passwordEditable.toString();
                    final User user = mListener.doLogin(username, password);
                    if(user == null) {
                        errorTextView.setText(R.string.wrong_credential_error);
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        firstAccessView.findViewById(R.id.facebook_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.doFacebookLogin();
                }
            }
        });
        return firstAccessView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }
}