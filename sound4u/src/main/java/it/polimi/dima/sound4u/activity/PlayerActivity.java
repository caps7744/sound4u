package it.polimi.dima.sound4u.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.Utilities.Utilities;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.Gift;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.service.DownloadImageTask;
import it.polimi.dima.sound4u.service.GiftSenderTask;

public class PlayerActivity extends Activity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener {

    public static final String PLAYER_ACTION = Const.PKG + ".action.PLAYER_ACTION";

    public static final String SOUND_EXTRA = Const.PKG + ".extra.SOUND_EXTRA";

    public static final String SHARING_GIFT_EXTRA = "it.polimi.dima.sound4u.extra.SHARING_GIFT_EXTRA";

    private static final int USER_SEARCH_ID = 1;

    private Utilities utils;

    CheckBox btn_equalizer = null;
    View thumbAndTxt = null;
    View equalizer = null;
    Button btn_send = null;
    Button btn_to_gifts = null;

    Sound currentSound = null;
    TextView song_title = null;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    String coverURL = null;
    ImageView thumbnail = null;
    String streamURL = null;

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
    private ImageButton btn_play,
            btn_pause;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private int lengthOfAudio;

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

        song_title = (TextView) findViewById(R.id.song_title);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);

        enabled = (CheckBox)findViewById(R.id.enabled);
        enabled.setOnCheckedChangeListener (this);

        thumbAndTxt = findViewById(R.id.thumbnail_and_gift_text_container);
        equalizer = findViewById(R.id.equalizer_container);

        btn_equalizer = (CheckBox)findViewById(R.id.btn_equalizer);
        btn_equalizer.setOnCheckedChangeListener (this);

        btn_send = (Button)findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);

        btn_to_gifts = (Button)findViewById(R.id.to_my_gifts);
        btn_to_gifts.setOnClickListener(this);

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
    protected void onStart() {
        super.onStart();

        currentSound = getIntent().getParcelableExtra(SOUND_EXTRA);
        song_title.setText(currentSound.getTitle());                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          streamURL = currentSound.getURLStream().concat("?client_id=").concat(getString(R.string.client_id));

        coverURL = currentSound.getCoverBig();

        try {
            if (coverURL != null) {
                new DownloadImageTask(thumbnail).execute(coverURL);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
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
            case R.id.action_help:
                toHelp();
                return true;
            case R.id.to_sound_search_from_player:
                toSoundSearch();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == USER_SEARCH_ID && resultCode == RESULT_OK) {
            Toast.makeText(this, "Gift sent!", Toast.LENGTH_SHORT).show();

            Gift gift = resultIntent.getParcelableExtra(GiftSenderTask.SHARED_GIFT_EXTRA);

            Intent intent = new Intent(this, SharingGiftActivity.class);
            intent.putExtra(SHARING_GIFT_EXTRA, gift);
            startActivity(intent);

        } else if (requestCode == USER_SEARCH_ID && resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Unable to send the gift.", Toast.LENGTH_SHORT).show();
        }
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

    public void toSoundSearch(){
        Intent intent = new Intent(this, SoundSearchActivity.class);
        startActivity(intent);
    }

    public void toMyGifts(){
        Intent intent = new Intent(this, MyGiftsActivity.class);
        startActivity(intent);
    }

    /*
    To delete - just for the interface
     */
    private void toUserSearch() {
        Intent intent = new Intent(this, UserSearchActivity.class);
        intent.putExtra(UserSearchActivity.SOUND_EXTRA, currentSound);
        startActivityForResult(intent, USER_SEARCH_ID);
    }

    /*
    Equalizer Methods
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onProgressChanged (SeekBar seekBar, int level,
                                   boolean fromTouch)
    {
        if (seekBar == bass_boost)
        {
            bb.setEnabled (level > 0);
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

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        handler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        handler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateSeekProgress();
    }

    public String formatBandLabel (int[] band)
    {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }

    public String milliHzToString (int milliHz)
    {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }

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

    public void updateBassBoost ()
    {
        if (bb != null)
            bass_boost.setProgress (bb.getRoundedStrength());
        else
            bass_boost.setProgress (0);
    }

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

    public void updateUI ()
    {
        updateSliders();
        updateBassBoost();
        enabled.setChecked (eq.getEnabled());
    }

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
        utils = new Utilities();

        songCurrentDurationLabel = (TextView) findViewById(R.id.current_duration_label);
        songTotalDurationLabel = (TextView) findViewById(R.id.total_duration_label);
        btn_play = (ImageButton)findViewById(R.id.btn_play);
        btn_play.setOnClickListener(this);
        btn_pause = (ImageButton)findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(this);
        btn_pause.setVisibility(View.GONE);

        seekBar = (SeekBar)findViewById(R.id.songProgressBar);
        seekBar.setOnSeekBarChangeListener(this);

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
        btn_play.setVisibility(View.VISIBLE);
        btn_pause.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {

        try {
            mediaPlayer.setDataSource(streamURL);
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
            case R.id.flat:
                setFlat();
                break;
            case R.id.btn_send:
                toUserSearch();
                break;
            case R.id.to_my_gifts:
                toMyGifts();
                break;
            default:
                break;
        }
        updateSeekProgress();
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition() / lengthOfAudio) * 100));

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };

    private void updateSeekProgress() {
            handler.postDelayed(mUpdateTimeTask, 1000);
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        btn_play.setVisibility(View.VISIBLE);
        btn_pause.setVisibility(View.GONE);
    }

    private void playAudio() {
        mediaPlayer.start();
        btn_play.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);

        updateSeekProgress();
    }

    private void toHelp() {
        Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);
    }
}
