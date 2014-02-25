package it.polimi.dima.sound4u.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import com.google.common.eventbus.EventBus;
import it.polimi.dima.sound4u.conf.Const;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    public MusicService() {
    }

    public static final String MUSICPLAYER_SOUND_ACTION = Const.PKG + ".action.MUSICPLAYER_SOUND_ACTION";

    public static final String MUSICPLAYER_STREAM_URL_EXTRA = Const.PKG + ".extra.MUSICPLAYER_STREAM_URL_EXTRA";

    private static MusicService msInstance = null;
    private MediaPlayer mp;
    private int lengthOfAudio;
    private String streamUrl;
    private EventBus eventBus;

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
        msInstance = this;
        eventBus = new EventBus();
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    private void initMediaPlayer() {
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnBufferingUpdateListener(this);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        try {
            mp.setDataSource(streamUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.prepareAsync();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        eventBus.post("ok");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    public static MusicService getMsInstance(){
        return msInstance;
    }
}
