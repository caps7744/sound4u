package it.polimi.dima.sound4u.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.easy.facebook.android.apicall.GraphApi;
import com.easy.facebook.android.data.User;
import com.easy.facebook.android.error.EasyFacebookError;
import com.easy.facebook.android.facebook.FBLoginManager;
import com.easy.facebook.android.facebook.Facebook;
import com.easy.facebook.android.facebook.LoginListener;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.model.Gift;

public class FacebookConnect extends Activity implements LoginListener {

    public static final String FACEBOOK_POST_EXTRA = "it.polimi.dima.sound4u.extra.FACEBOOK_POST_EXTRA";

    private FBLoginManager fbLoginManager;
    private GraphApi graphApi;
    private User user;

    public final String APP_ID = "1379605065621911";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectToFacebook();
    }

    public void connectToFacebook() {
        String permissions[] = {
                "user_about_me",
                "user_activities",
                "user_birthday",
                "user_checkins",
                "user_education_history",
                "user_events",
                "user_groups",
                "user_hometown",
                "user_interests",
                "user_likes",
                "user_location",
                "user_notes",
                "user_online_presence",
                "user_photo_video_tags",
                "user_photos",
                "user_relationships",
                "user_relationship_details",
                "user_religion_politics",
                "user_status",
                "user_videos",
                "user_website",
                "user_work_history",
                "email",
                "read_friendlists",
                "read_insights",
                "read_mailbox",
                "read_requests",
                "read_stream",
                "xmpp_login",
                "ads_management",
                "create_event",
                "manage_friendlists",
                "manage_notifications",
                "offline_access",
                "publish_checkins",
                "publish_stream",
                "rsvp_event",
                "sms",
                "publish_actions",
                "manage_pages"
        };

        fbLoginManager = new FBLoginManager(this, R.layout.activity_facebook_connect, APP_ID, permissions);

        if (fbLoginManager.existsSavedFacebook()){
            fbLoginManager.loadFacebook();
        } else {
            fbLoginManager.login();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!=null){
            fbLoginManager.loginSuccess(data);
        } else {
            finish();
        }
    }

    public void loginSuccess(Facebook facebook){
        graphApi = new GraphApi(facebook);
        user = new User();

        SetStatus ss = new SetStatus();
        ss.execute();
    }

    public void logoutSuccess() {
        fbLoginManager.displayToast("Logout Success");
        finish();
    }

    public void loginFail() {
        fbLoginManager.displayToast("Login Fail");
        finish();
    }

    class SetStatus extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            fbLoginManager.displayToast("Shared on Facebook!");
            finish();
        }
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                Gift gift = getIntent().getParcelableExtra(FACEBOOK_POST_EXTRA);
                String songTitle = gift.getSound().getTitle();
                String message = "I sent "+songTitle+" to "+gift.getReceiver().getUsername()+" with Sound4u!";
                String pictureUrl = gift.getSound().getCoverBig();

                user = graphApi.getMyAccountInfo();
                graphApi.setStatus(message, pictureUrl, null, null, null, songTitle);

            } catch (EasyFacebookError e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
