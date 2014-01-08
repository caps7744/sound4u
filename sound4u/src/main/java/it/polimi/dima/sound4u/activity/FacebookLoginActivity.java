package it.polimi.dima.sound4u.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.conf.SoundCloudConst;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.LoginService;

public class FacebookLoginActivity extends ActionBarActivity {

    public static final String LOGIN_ACTION = Const.PKG + ".action.LOGIN_ACTION";

    public static final String USER_EXTRA = Const.PKG + ".extra.USER_EXTRA";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                if (url.startsWith(SoundCloudConst.REDIRECT_URI.toString())) {
                    Uri result = Uri.parse(url);
                    String error = result.getQueryParameter("error");
                    String code = result.getQueryParameter("code");
                }
                return true;
            }
        });
        // webView.loadUrl(wrapper.authorizationCodeUrl(Endpoints.FACEBOOK_CONNECT.toString()), Token.SCOPE_NON_EXPIRING);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
