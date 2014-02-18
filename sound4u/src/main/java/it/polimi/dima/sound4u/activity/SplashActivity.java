package it.polimi.dima.sound4u.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.User;

import java.lang.ref.WeakReference;

public class SplashActivity extends Activity {

    private static final long MIN_WAIT_INTERVAL = 1500L;

    private static final long MAX_WAIT_INTERVAL = 3000L;

    private static final int GO_AHEAD_WHAT = 1;

    private static final String IS_DONE_KEY = Const.PKG + ".key.IS_DONE_KEY";

    private static final String START_TIME_KEY = Const.PKG + ".key.START_TIME_KEY";

    private long mStartTime = -1;

    private boolean mIsDone;

    private UiHandler mHandler;

    private static class UiHandler extends Handler {

        private WeakReference<SplashActivity> mActivityRef;

        public UiHandler(final SplashActivity srcActivity) {
            this.mActivityRef = new WeakReference<SplashActivity>(srcActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            final SplashActivity srcActivity = this.mActivityRef.get();
            if(srcActivity == null) {
                return;
            }
            switch (msg.what) {
                case GO_AHEAD_WHAT:
                    long elapsedTime = SystemClock.uptimeMillis() - srcActivity.mStartTime;
                    if(elapsedTime >= MIN_WAIT_INTERVAL && !srcActivity.mIsDone) {
                        srcActivity.mIsDone = true;
                        srcActivity.goAhead();
                    }
                    break;
            }
        }
    }

    private void goAhead() {
        final User user = User.load(this);
        Class<? extends Activity> destinationActivity = null;
        if (user == null) {
            destinationActivity = FirstAccessActivity.class;
        } else {
            destinationActivity = MyGiftsActivity.class;
        }
        final Intent intent = new Intent(this, destinationActivity);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(savedInstanceState != null) {
            this.mStartTime = savedInstanceState.getLong(START_TIME_KEY);
        }
        mHandler = new UiHandler(this);
        final ImageView logoImageView = (ImageView) findViewById(R.id.splash_imageview);
        logoImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long elapsedTime = SystemClock.uptimeMillis() - mStartTime;
                if(elapsedTime >= MIN_WAIT_INTERVAL && !mIsDone) {
                    mIsDone = true;
                    goAhead();
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mStartTime == -1L) {
            mStartTime = SystemClock.uptimeMillis();
        }
        final Message goAheadMessage = mHandler.obtainMessage(GO_AHEAD_WHAT);
        mHandler.sendMessageAtTime(goAheadMessage, mStartTime + MAX_WAIT_INTERVAL);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_DONE_KEY, mIsDone);
        outState.putLong(START_TIME_KEY, mStartTime);
    }
}
