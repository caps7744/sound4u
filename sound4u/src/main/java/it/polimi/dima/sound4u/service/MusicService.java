package it.polimi.dima.sound4u.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import de.greenrobot.event.EventBus;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.activity.PlayerActivity;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.DurationInformation;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.utilities.Utilities;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    private static final int NOTIFY_ID = 123456789 ;

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
                    int percent = (int)(((float)currentDuration/totalDuration)*100);
                    if (notificationCompat != null) {
                        notificationCompat.setProgress(100, percent, false);
                        notification = notificationCompat.build();
                        notificationManager.notify(NOTIFY_ID, notification);
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(MusicService.class.getName(), e.getMessage());
                }
            }
        }
    };

    private MediaPlayer mMediaPlayer;
    private State mState;
    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationCompat.Builder notificationCompat;
    private Sound mySound;

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
        Sound intentSound = intent.getParcelableExtra(PlayerActivity.SOUND_TO_MUSIC_SERVICE_EXTRA);
        if (mySound == null || intentSound.getId() != mySound.getId()) {
            mySound = intentSound;
            initMediaPlayer();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initMediaPlayer() {
        mState = State.Retriving;
        EventBus.getDefault().post(mState);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        try {
            String urlStream = mySound.getURLStream().concat("?client_id=").concat(getString(R.string.client_id));
            mMediaPlayer.setDataSource(urlStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
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
            case Stop: {
                pause();
                stop();
                break;
            }
            case Exit:
                exit();
                break;
            case State:
                sendState();
                break;
            default:
                return;
        }
    }

    private void sendState() {
        EventBus.getDefault().post(mState);
    }

    private void exit() {
        if (mState != State.Retriving) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
    }

    private void stop() {
        mMediaPlayer.seekTo(0);
        mState = State.Prepared;
        EventBus.getDefault().post(mState);
        notificationManager.cancel(NOTIFY_ID);
        stopForeground(true);
    }

    private void pause() {
        mMediaPlayer.pause();
        mState = State.Paused;
        EventBus.getDefault().post(mState);
    }

    private void play() {
        mMediaPlayer.start();
        new Thread(mUpdateTimeTask).start();
        if (mState != State.Paused) {
            //Prepare intent for calling back to player by clicking on the notification
            Intent backToPlayerIntent = new Intent(this, PlayerActivity.class);
            backToPlayerIntent.putExtra(PlayerActivity.SOUND_EXTRA, mySound);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, backToPlayerIntent, 0);

            notificationCompat = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.notification_player_message))
                    .setContentText(mySound.getTitle())
                    .setSmallIcon(R.drawable.app_launcher)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setProgress(100, 0, false);
            notification = notificationCompat.build();
            startForeground(NOTIFY_ID, notification);
        }
        mState = State.Playing;
        EventBus.getDefault().post(mState);
    }

    public void onEvent(PlayerActivity.SeekBarTouchProgress event) {
        int currentPosition = Utilities.progressToTimer(event.getProgress(), mMediaPlayer.getDuration());
        mMediaPlayer.seekTo(currentPosition);
    }

}
