package it.polimi.dima.sound4u.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.fragment.EqualizerFragment;
import it.polimi.dima.sound4u.fragment.ThumbnailFragment;

public class PlayerActivity extends ActionBarActivity {

    public static final String PLAYER_ACTION = Const.PKG + ".action.PLAYER_ACTION";

    public static final String SOUND_EXTRA = Const.PKG + ".extra.SOUND_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        if (savedInstanceState == null) {
            showThumbnailFragment();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player, menu);
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

    /*
    Not implemented yet, it works just for the interface
     */
    public void doSoundSearch(View view) {
        Intent intent;
        intent = new Intent(this, SoundSearchActivity.class);
        startActivity(intent);
    }

    public void showEqualizer(View view) {
        EqualizerFragment fragment = new EqualizerFragment();

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.thumbnailEqualizerContainer, fragment);
        transaction.commit();
    }

    public void showThumbnail(View view) {
        showThumbnailFragment();
    }

    private void showThumbnailFragment() {
        ThumbnailFragment fragment = new ThumbnailFragment();

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.thumbnailEqualizerContainer, fragment);
        transaction.commit();
    }
}
