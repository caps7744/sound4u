package it.polimi.dima.sound4u.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.fragment.EqualizerFragment;
import it.polimi.dima.sound4u.fragment.ThumbnailFragment;

public class PlayerActivity extends ActionBarActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, View.OnClickListener, View.OnTouchListener {

    public static final String PLAYER_ACTION = Const.PKG + ".action.PLAYER_ACTION";

    public static final String SOUND_EXTRA = Const.PKG + ".extra.SOUND_EXTRA";

    /*
    Streaming player variables
     */
    private Button btn_play,
            btn_pause,
            btn_stop;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private int lengthOfAudio;

    // url stream example
    private final String URL = "https://api.soundcloud.com/tracks/113949500/stream?client_id=5e96474adefe6378b0ec309d43d383c6";

    private final Handler handler = new Handler();
    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            updateSeekProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        init();

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
        transaction.replace(R.id.thumbnail_equalizer_container, fragment);
        transaction.commit();
    }

    public void showThumbnail(View view) {
        showThumbnailFragment();
    }

    private void showThumbnailFragment() {
        ThumbnailFragment fragment = new ThumbnailFragment();

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.thumbnail_equalizer_container, fragment);
        transaction.commit();
    }




    /*
    Streaming player methods
     */

    private void init() {
        btn_play = (Button)findViewById(R.id.btn_play);
        btn_play.setOnClickListener(this);
        btn_pause = (Button)findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(this);
        btn_pause.setEnabled(false);
        btn_stop = (Button)findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);
        btn_stop.setEnabled(false);

        seekBar = (SeekBar)findViewById(R.id.seek_bar);
        seekBar.setOnTouchListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        btn_play.setEnabled(true);
        btn_pause.setEnabled(false);
        btn_stop.setEnabled(false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mediaPlayer.isPlaying()) {
            SeekBar tmpSeekBar = (SeekBar)v;
            mediaPlayer.seekTo((lengthOfAudio / 100) * tmpSeekBar.getProgress() );
        }
        return false;
    }

    @Override
    public void onClick(View view) {

        try {
            mediaPlayer.setDataSource(URL);
            mediaPlayer.prepare();
            lengthOfAudio = mediaPlayer.getDuration();
        } catch (Exception e) {
            //Log.e("Error", e.getMessage());
        }

        switch (view.getId()) {
            case R.id.btn_play:
                playAudio();
                break;
            case R.id.btn_pause:
                pauseAudio();
                break;
            case R.id.btn_stop:
                stopAudio();
                break;
            default:
                break;
        }

        updateSeekProgress();
    }

    private void updateSeekProgress() {
        if (mediaPlayer.isPlaying()) {
            seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition() / lengthOfAudio) * 100));
            handler.postDelayed(r, 1000);
        }
    }

    private void stopAudio() {
        mediaPlayer.stop();
        btn_play.setEnabled(true);
        btn_pause.setEnabled(false);
        btn_stop.setEnabled(false);
        seekBar.setProgress(0);
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        btn_play.setEnabled(true);
        btn_pause.setEnabled(false);
    }

    private void playAudio() {
        mediaPlayer.start();
        btn_play.setEnabled(false);
        btn_pause.setEnabled(true);
        btn_stop.setEnabled(true);
    }
}
