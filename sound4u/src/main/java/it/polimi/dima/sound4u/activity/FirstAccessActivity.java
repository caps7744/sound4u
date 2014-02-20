package it.polimi.dima.sound4u.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.conf.SoundCloudConst;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.LoginTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Stack;

public class FirstAccessActivity extends ActionBarActivity{

    public static final String USER_EXTRA = Const.PKG + ".extra.USER_EXTRA";

    final ApiWrapper wrapper = new ApiWrapper(
            SoundCloudConst.CLIENT_ID,
            SoundCloudConst.CLIENT_SECRET,
            SoundCloudConst.REDIRECT_URI,
            null    /* token */);

    private Stack<String> urlStack;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        WebView view = (WebView) findViewById(R.id.webview);
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                Log.w(this.getClass().getName(), url);
                if (url.startsWith("sound4u://soundcloud/callback")) {
                    new RetrivePersonalInformationTask(wrapper).execute();
                }
                else {
                    urlStack.push(url);
                    view.loadUrl(urlStack.peek());
                }
                return true;
            }
        });
        urlStack = new Stack<String>();
        urlStack.add("https://soundcloud.com/connect?" +
                "client_id=1e9034524a004460783bb4d4ba024ffb&" +
                "redirect_uri=sound4u://soundcloud/callback&" +
                "response_type=code&" +
                "display=popup&" +
                "scope=non-expiring");
        view.loadUrl(urlStack.peek());

    }

    @Override
    public void onBackPressed() {
        WebView view = (WebView) findViewById(R.id.webview);
        urlStack.pop();
        view.loadUrl(urlStack.peek());
        super.onBackPressed();
    }

    private class RetrivePersonalInformationTask extends AsyncTask {

        private ApiWrapper wrapper;

        public RetrivePersonalInformationTask(ApiWrapper wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                HttpResponse response = wrapper.get(Request.to("/me"));
                if(response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseBody = EntityUtils.toString(entity);
                        JsonObject jsonObject = JsonObject.readFrom(responseBody);
                        User me = User.create(jsonObject);
                        me.withToken(wrapper.getToken());
                        me.save(FirstAccessActivity.this);
                        Intent loggedIntent = new Intent(FirstAccessActivity.this, MyGiftsActivity.class);
                        startActivity(loggedIntent);
                        finish();
                    }
                }
            } catch (IOException e) {
                Log.w(this.getClass().getName(), e.getMessage());
            }
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                toHelp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toHelp() {
        Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);
    }

}