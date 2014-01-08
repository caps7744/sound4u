package it.polimi.dima.sound4u.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import it.polimi.dima.sound4u.R;

/**
 * Created by canidio-andrea on 06/01/14.
 */
public class FirstAccessFragment extends Fragment {

    public interface FirstAccessListener {

        void doLogin();

        void doSignUp();

    }

    private FirstAccessListener mListener;

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
        firstAccessView.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.doLogin();
                }
            }
        });
        firstAccessView.findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.doSignUp();
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