package it.polimi.dima.sound4u.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.common.eventbus.EventBus;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.model.DurationInformation;
import it.polimi.dima.sound4u.utilities.Utilities;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.Gift;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.service.DownloadImageTask;
import it.polimi.dima.sound4u.service.GiftSenderTask;

import java.io.IOException;

public class PlayerActivity extends Activity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    public static final String PLAYER_ACTION = Const.PKG + ".action.PLAYER_ACTION";

    public static final String SOUND_EXTRA = Const.PKG + ".extra.SOUND_EXTRA";

    public static final String SHARING_GIFT_EXTRA = "it.polimi.dima.sound4u.extra.SHARING_GIFT_EXTRA";

    private static final int USER_SEARCH_ID = 1;

    public static enum Command {
        Play,
        Pause,
        Stop,
        Exit
    }

    public class SeekBarTouchPosition {
        private int position;

        public SeekBarTouchPosition(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    private EventBus eventBus;

    private DurationInformation information;

    CheckBox btn_equalizer = null;
    View thumbAndTxt = null;
    View equalizer = null;
    Button btn_send = null;

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
            btn_pause,
            btn_stop;
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
        eventBus = new EventBus();
        eventBus.register(this);

        information = new DurationInformation(0,0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        song_title = (TextView) findViewById(R.id.song_title);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);

        thumbAndTxt = findViewById(R.id.thumbnail_and_gift_text_container);
        equalizer = findViewById(R.id.equalizer_container);

        btn_equalizer = (CheckBox)findViewById(R.id.btn_equalizer);
        btn_equalizer.setOnCheckedChangeListener(this);

        btn_send = (Button)findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);

        initializeEqualizerVariables();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentSound = getIntent().getParcelableExtra(SOUND_EXTRA);
        song_title.setText(currentSound.getTitle());
        streamURL = currentSound.getURLStream().concat("?client_id=").concat(getString(R.string.client_id));

        coverURL = currentSound.getCoverBig();

        initializeMediaPlayerVariables();

        try {
            if (coverURL != null) {
                new DownloadImageTask(thumbnail).execute(coverURL);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAudio();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                toHelp();
                return true;
            case R.id.to_sound_search_from_player:
                toSoundSearch();
                return true;
            case R.id.action_to_gifts:
                toMyGifts();
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

    private void toUserSearch() {
        Intent intent = new Intent(this, UserSearchActivity.class);
        intent.putExtra(UserSearchActivity.SOUND_EXTRA, currentSound);
        startActivityForResult(intent, USER_SEARCH_ID);
    }
    private void toHelp() {
        Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);
    }





    /*
    Streaming player methods___________________________________________________________________________________________________________
     */





    public void onEvent(String string){
        if(string.equals("ok")){
            btn_play.setEnabled(true);
        }
    }

    private void initializeMediaPlayerVariables() {

        songCurrentDurationLabel = (TextView) findViewById(R.id.current_duration_label);
        songTotalDurationLabel = (TextView) findViewById(R.id.total_duration_label);
        btn_play = (ImageButton)findViewById(R.id.btn_play);
        btn_pause = (ImageButton)findViewById(R.id.btn_pause);
        btn_stop = (ImageButton)findViewById(R.id.btn_stop);
        seekBar = (SeekBar)findViewById(R.id.songProgressBar);

        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);

        btn_pause.setVisibility(View.GONE);
        btn_stop.setEnabled(false);
        btn_play.setEnabled(false);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.reset();
        try {
            try {
                mediaPlayer.setDataSource(streamURL);
            } catch (IllegalStateException e){
                mediaPlayer.reset();
                mediaPlayer.setDataSource(streamURL);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_play:
                    lengthOfAudio = mediaPlayer.getDuration();
                    playAudio();
                break;
            case R.id.btn_pause:
                lengthOfAudio = mediaPlayer.getDuration();
                pauseAudio();
                break;
            case R.id.btn_stop:
                lengthOfAudio = mediaPlayer.getDuration();
                stopAudio();
                break;
            case R.id.flat:
                setFlat();
                break;
            case R.id.btn_send:
                toUserSearch();
                break;
            default:
                break;
        }

        if(view.getId()!=R.id.btn_stop){
            updateSeekProgress();
        }
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+Utilities.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText("" + Utilities.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition() / lengthOfAudio) * 100));

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };

    private void updateSeekProgress() {
            handler.postDelayed(mUpdateTimeTask, 100);
    }

    private void pauseAudio() {
        btn_play.setVisibility(View.VISIBLE);
        btn_pause.setVisibility(View.GONE);
        Command command = Command.Pause;
        eventBus.post(command);
    }

    private void stopAudio(){
        btn_play.setVisibility(View.VISIBLE);
        btn_pause.setVisibility(View.GONE);
        Command command = Command.Stop;
        eventBus.post(command);
    }

    private void playAudio() {
        btn_play.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);
        Command command = Command.Play;
        eventBus.post(command);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(this.seekBar == seekBar){
            int currentPosition = Utilities.progressToTimer(seekBar.getProgress(), information.getTotalMillisDuration());
            SeekBarTouchPosition seekPosition = new SeekBarTouchPosition(currentPosition);
            eventBus.post(seekPosition);

        }
    }





    /*
    Equalizer Methods_____________________________________________________________________________________________________________________
     */

    private void initializeEqualizerVariables() {
        flat = (Button)findViewById(R.id.flat);
        flat.setOnClickListener(this);

        enabled = (CheckBox)findViewById(R.id.enabled);
        enabled.setOnCheckedChangeListener(this);

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

        eq = new Equalizer(0, 0);
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
        bb = new BassBoost(0, 0);
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
    public void onStartTrackingTouch(SeekBar seekBar) { }

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
        enabled.setChecked(eq.getEnabled());
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
}
