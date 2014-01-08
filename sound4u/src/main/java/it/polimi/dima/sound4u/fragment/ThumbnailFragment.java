package it.polimi.dima.sound4u.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import it.polimi.dima.sound4u.R;

/**
 * Created by Caps on 07/01/14.
 */
public class ThumbnailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thumbnail, container, false);
    }
}