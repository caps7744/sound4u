package it.polimi.dima.sound4u.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import it.polimi.dima.sound4u.activity.FirstAccessActivity;
import it.polimi.dima.sound4u.activity.MyGiftsActivity;
import it.polimi.dima.sound4u.conf.SoundCloudConst;
import it.polimi.dima.sound4u.model.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * Created by canidio-andrea on 14/01/14.
 */
public class LoginTask extends AsyncTask<Void, Void, User> {

    private String username;

    private String password;

    private Activity context;

    private ApiWrapper service;

    public LoginTask(Activity context, String username, String password) {
        this.username = username;
        this.password = password;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        service = new ApiWrapper(
                SoundCloudConst.CLIENT_ID,
                SoundCloudConst.CLIENT_SECRET,
                null,
                null
        );
    }

    @Override
    protected User doInBackground(Void... params) {
        User user = null;
        try {
            service.login(username, password);
            HttpResponse response = service.get(Request.to("/me"));
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    user = User.create(responseBody).withPassword(password);
                }
            }
        } catch (Exception e) { }
        return user;
    }

    @Override
    protected void onPostExecute(User user) {
        if(user != null) {
            user.save(context);
            Toast.makeText(context, "Logged in as " + user.getId(), Toast.LENGTH_SHORT).show();
            Intent giftsIntent = new Intent(context, MyGiftsActivity.class);
            context.startActivity(giftsIntent);
            context.finish();
        } else {
            Toast.makeText(context, "Wrong credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
