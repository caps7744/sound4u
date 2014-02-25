package it.polimi.dima.sound4u.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import de.greenrobot.event.EventBus;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.activity.PlayerActivity;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.DurationInformation;
import it.polimi.dima.sound4u.utilities.Utilities;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    private static final int NOTIFY_ID = 123456789 ;

    public static String streamUrl = null;

    public static enum State {
        Retriving,      // After prepareAsync
        Prepared,       // After onPrepared
        Playing,        // After start() and before onCompleted()
        Paused,         // After pause() and before new start()
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
                    EventBus.getDefault().post(information);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(MusicService.class.getName(), e.getMessage());
                }
            }
        }
    };

    public static final String MUSICPLAYER_SOUND_ACTION = Const.PKG + ".action.MUSICPLAYER_SOUND_ACTION";
    public static final String MUSICPLAYER_STREAM_URL_EXTRA = Const.PKG + ".extra.MUSICPLAYER_STREAM_URL_EXTRA";

    public static MusicService myMusicService;

    public static MusicService getMyMusicService() {
        if(myMusicService==null){
            myMusicService = new MusicService();
        }
            return myMusicService;
    }

    private MediaPlayer mMediaPlayer;
    private State mState;
    private NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new Notification();
        notification.icon = R.drawable.btn_play_grey;
        notification.tickerText = "Notification Test";
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        RemoteViews layout = new RemoteViews(getPackageName(), R.layout.notification);
        notification.contentView = layout;

        startForeground(startId, notification);

        streamUrl = intent.getStringExtra(MUSICPLAYER_STREAM_URL_EXTRA);
        initMediaPlayer();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancelAll();
        EventBus.getDefault().unregister(this);
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
        EventBus.getDefault().post(mState);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        EventBus.getDefault().post(new SeekBarPercentage(percent));
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mState = State.Prepared;
        EventBus.getDefault().post(mState);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopForeground(true);
        mState = State.Prepared;
        EventBus.getDefault().post(mState);
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
        EventBus.getDefault().post(mState);
    }

    private void pause() {
        mMediaPlayer.pause();
        mState = State.Paused;
        EventBus.getDefault().post(mState);
    }

    private void play() {
        mMediaPlayer.start();
        new Thread(mUpdateTimeTask).start();
        mState = State.Playing;
        EventBus.getDefault().post(mState);
    }

    public void onEvent(PlayerActivity.SeekBarTouchProgress event) {
        int currentPosition = Utilities.progressToTimer(event.getProgress(), mMediaPlayer.getDuration());
        mMediaPlayer.seekTo(currentPosition);
    }

}
