package it.polimi.dima.sound4u.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;
import com.google.common.eventbus.EventBus;
import it.polimi.dima.sound4u.activity.PlayerActivity;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.DurationInformation;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    public static enum State {
        Retriving,      // After prepareAsync
        Prepared,       // After onPrepared
        Playing,        // After start() and before onCompleted()
        Paused,         // After pause() and before new start()
        Completed       // After onCompleted()
    };

    public class SeekBarPercentage {
        private int percentage;

        public SeekBarPercentage(int percentage) {
            this.percentage = percentage;
        }

        public int getPercentage() {
            return percentage;
        }
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            while (mMediaPlayer.isPlaying()) {
                try {
                    int totalDuration = mMediaPlayer.getDuration();
                    int currentDuration = mMediaPlayer.getCurrentPosition();
                    DurationInformation information = new DurationInformation(totalDuration, currentDuration);
                    eventBus.post(information);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(MusicService.class.getName(), e.getMessage());
                }
            }
        }
    };

    public static final String MUSICPLAYER_SOUND_ACTION = Const.PKG + ".action.MUSICPLAYER_SOUND_ACTION";
    public static final String MUSICPLAYER_STREAM_URL_EXTRA = Const.PKG + ".extra.MUSICPLAYER_STREAM_URL_EXTRA";

    private MediaPlayer mMediaPlayer;
    private String streamUrl;
    private EventBus eventBus;
    private State mState;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(MUSICPLAYER_SOUND_ACTION)){
            streamUrl = intent.getStringExtra(MUSICPLAYER_STREAM_URL_EXTRA);
            initMediaPlayer();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        eventBus = new EventBus();
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        try {
            mMediaPlayer.setDataSource(streamUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mState = State.Retriving;
        eventBus.post(mState);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        eventBus.post(new SeekBarPercentage(percent));
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mState = State.Prepared;
        eventBus.post(mState);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mState = State.Completed;
        eventBus.post(mState);
    }

    public void onEvent(PlayerActivity.Command event) {
        switch (event) {
            case Play:
                play();
                break;
            case Pause:
                pause();
                break;
            case Stop:
                stop();
                break;
            case Exit:
                exit();
                break;
            default:
                return;
        }
    }

    private void exit() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    private void stop() {
        mMediaPlayer.pause();
        mMediaPlayer.seekTo(0);
        mState = State.Prepared;
        eventBus.post(mState);
    }

    private void pause() {
        mMediaPlayer.pause();
        mState = State.Paused;
        eventBus.post(mState);
    }

    private void play() {
        mMediaPlayer.start();
        new Thread(mUpdateTimeTask).start();
        mState = State.Playing;
        eventBus.post(mState);
    }
}
