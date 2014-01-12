package it.polimi.dima.sound4u.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;

public class PlayerActivity extends ActionBarActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, View.OnClickListener,
        View.OnTouchListener, SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener {

    public static final String PLAYER_ACTION = Const.PKG + ".action.PLAYER_ACTION";

    public static final String SOUND_EXTRA = Const.PKG + ".extra.SOUND_EXTRA";

    CheckBox btn_equalizer = null;
    View thumbAndTxt = null;
    View equalizer = null;

    /*
    Equalizer Variables
     */
    TextView bass_boost_label = null;
    SeekBar bass_boost = null;
    CheckBox enabled = null;
    Button flat = null;

    Equalizer eq = null;
    BassBoost bb = null;

    int min_level = 0;
    int max_level = 100;

    static final int MAX_SLIDERS = 8; // Must match the XML layout
    SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    TextView slider_labels[] = new TextView[MAX_SLIDERS];
    int num_sliders = 0;

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
    private final String URL = "https://api.soundcloud.com/tracks/113949500/stream?client_id=1e9034524a004460783bb4d4ba024ffb";

    private final Handler handler = new Handler();
    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            updateSeekProgress();
        }
    };

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        init();

        enabled = (CheckBox)findViewById(R.id.enabled);
        enabled.setOnCheckedChangeListener (this);

        thumbAndTxt = findViewById(R.id.thumbnail_and_gift_text_container);
        equalizer = findViewById(R.id.equalizer_container);

        btn_equalizer = (CheckBox)findViewById(R.id.btn_equalizer);
        btn_equalizer.setOnCheckedChangeListener (this);

        flat = (Button)findViewById(R.id.flat);
        flat.setOnClickListener(this);

        bass_boost = (SeekBar)findViewById(R.id.bass_boost);
        bass_boost.setOnSeekBarChangeListener(this);
        bass_boost_label = (TextView) findViewById (R.id.bass_boost_label);

        sliders[0] = (SeekBar)findViewById(R.id.slider_1);
        slider_labels[0] = (TextView)findViewById(R.id.slider_label_1);
        sliders[1] = (SeekBar)findViewById(R.id.slider_2);
        slider_labels[1] = (TextView)findViewById(R.id.slider_label_2);
        sliders[2] = (SeekBar)findViewById(R.id.slider_3);
        slider_labels[2] = (TextView)findViewById(R.id.slider_label_3);
        sliders[3] = (SeekBar)findViewById(R.id.slider_4);
        slider_labels[3] = (TextView)findViewById(R.id.slider_label_4);
        sliders[4] = (SeekBar)findViewById(R.id.slider_5);
        slider_labels[4] = (TextView)findViewById(R.id.slider_label_5);
        sliders[5] = (SeekBar)findViewById(R.id.slider_6);
        slider_labels[5] = (TextView)findViewById(R.id.slider_label_6);
        sliders[6] = (SeekBar)findViewById(R.id.slider_7);
        slider_labels[6] = (TextView)findViewById(R.id.slider_label_7);
        sliders[7] = (SeekBar)findViewById(R.id.slider_8);
        slider_labels[7] = (TextView)findViewById(R.id.slider_label_8);

        eq = new Equalizer (0, 0);
        if (eq != null)
        {
            eq.setEnabled (true);
            int num_bands = eq.getNumberOfBands();
            num_sliders = num_bands;
            short r[] = eq.getBandLevelRange();
            min_level = r[0];
            max_level = r[1];
            for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
            {
                int[] freq_range = eq.getBandFreqRange((short)i);
                sliders[i].setOnSeekBarChangeListener(this);
                slider_labels[i].setText (formatBandLabel (freq_range));
            }
        }
        for (int i = num_sliders ; i < MAX_SLIDERS; i++)
        {
            sliders[i].setVisibility(View.GONE);
            slider_labels[i].setVisibility(View.GONE);
        }

        bb = new BassBoost (0, 0);
        if (bb != null)
        {
        }
        else
        {
            bass_boost.setVisibility(View.GONE);
            bass_boost_label.setVisibility(View.GONE);
        }

        updateUI();
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

    /*
    To delete - just for the interface
     */
    public void toAddingText(View view){
        Intent intent = new Intent(this, AddingGiftTextActivity.class);
        startActivity(intent);
    }

    private void showEqualizer(boolean isChecked){
        if (isChecked){
            thumbAndTxt.setVisibility(View.GONE);
            equalizer.setVisibility(View.VISIBLE);
        } else if (!isChecked){
            equalizer.setVisibility(View.GONE);
            thumbAndTxt.setVisibility(View.VISIBLE);
        }
    }

    /*
    Equalizer Methods
     */
    /*=============================================================================
    onProgressChanged
=============================================================================*/
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onProgressChanged (SeekBar seekBar, int level,
                                   boolean fromTouch)
    {
        if (seekBar == bass_boost)
        {
            bb.setEnabled (level > 0 ? true : false);
            bb.setStrength ((short)level); // Already in the right range 0-1000
        }
        else if (eq != null)
        {
            int new_level = min_level + (max_level - min_level) * level / 100;

            for (int i = 0; i < num_sliders; i++)
            {
                if (sliders[i] == seekBar)
                {
                    eq.setBandLevel ((short)i, (short)new_level);
                    break;
                }
            }
        }
    }

    /*=============================================================================
        onStartTrackingTouch
    =============================================================================*/
    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
    }

    /*=============================================================================
        onStopTrackingTouch
    =============================================================================*/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
    }

    /*=============================================================================
        formatBandLabel
    =============================================================================*/
    public String formatBandLabel (int[] band)
    {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }

    /*=============================================================================
        milliHzToString
    =============================================================================*/
    public String milliHzToString (int milliHz)
    {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }

    /*=============================================================================
        updateSliders
    =============================================================================*/
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void updateSliders ()
    {
        for (int i = 0; i < num_sliders; i++)
        {
            int level;
            if (eq != null)
                level = eq.getBandLevel ((short)i);
            else
                level = 0;
            int pos = 100 * level / (max_level - min_level) + 50;
            sliders[i].setProgress (pos);
        }
    }

    /*=============================================================================
        updateBassBoost
    =============================================================================*/
    public void updateBassBoost ()
    {
        if (bb != null)
            bass_boost.setProgress (bb.getRoundedStrength());
        else
            bass_boost.setProgress (0);
    }

    /*=============================================================================
        onCheckedChange
    =============================================================================*/
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCheckedChanged (CompoundButton view, boolean isChecked)
    {
        if (view == (View) enabled)
        {
            eq.setEnabled (isChecked);
        }
        if (view == (View) btn_equalizer)
        {
            showEqualizer(isChecked);
        }
    }

    /*=============================================================================
    updateUI
=============================================================================*/
    public void updateUI ()
    {
        updateSliders();
        updateBassBoost();
        enabled.setChecked (eq.getEnabled());
    }

    /*=============================================================================
        setFlat
    =============================================================================*/
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void setFlat ()
    {
        if (eq != null)
        {
            for (int i = 0; i < num_sliders; i++)
            {
                eq.setBandLevel ((short)i, (short)0);
            }
        }

        if (bb != null)
        {
            bb.setEnabled (false);
            bb.setStrength ((short)0);
        }

        updateUI();
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
            case R.id.flat:
                setFlat();
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