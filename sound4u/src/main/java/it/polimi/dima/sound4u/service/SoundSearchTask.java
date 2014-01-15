package it.polimi.dima.sound4u.service;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import it.polimi.dima.sound4u.activity.FirstAccessActivity;
import it.polimi.dima.sound4u.conf.SoundCloudConst;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.model.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * Created by canidio-andrea on 15/01/14.
 */
public class SoundSearchTask extends AsyncTask {

    private static final String TAG_LOG = SoundSearchTask.class.getName();

    private Activity context;

    private ApiWrapper wrapper;

    private String query;

    public SoundSearchTask(Activity context, String query) {
        this.context = context;
        this.query = query;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            User user = User.load(context);
            wrapper.login(user.getUsername(), user.getPassword());
            HttpResponse response = wrapper.get(Request.to("/tracks")
                    .with("track[title]", "(.*)" + query + "(.*)"));
            if(response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    JsonArray jsonArray = JsonArray.readFrom(responseBody);
                    for (JsonValue item: jsonArray.values()) {
                        Sound soundItem = Sound.create((JsonObject)item);
                        Log.w(TAG_LOG, soundItem.getId() + " " + soundItem.getTitle());
                    }
                }
            } else if (response.getStatusLine().getStatusCode() == 403) {
                user.logout(context);
                Intent accessIntent = new Intent(context, FirstAccessActivity.class);
                context.startActivity(accessIntent);
                context.finish();
            }
        } catch (Exception e) {
            Log.w(TAG_LOG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        wrapper = new ApiWrapper(
                SoundCloudConst.CLIENT_ID,
                SoundCloudConst.CLIENT_SECRET,
                null,
                null
        );
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
