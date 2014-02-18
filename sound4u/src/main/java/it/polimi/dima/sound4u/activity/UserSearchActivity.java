package it.polimi.dima.sound4u.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.conf.SoundCloudConst;
import it.polimi.dima.sound4u.model.Gift;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.DownloadImageTask;
import it.polimi.dima.sound4u.service.GiftSenderTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserSearchActivity extends ListActivity {

    private static final String TAG_LOG = SoundSearchActivity.class.getName();

    public static final String USERSEARCH_ACTION = Const.PKG + ".action.USERSEARCH_ACTION";

    public static final int USER_REQUEST_ID = 2;

    public static final String SOUND_EXTRA = Const.PKG + ".extra.SOUND_EXTRA";

    private static final String MODEL_KEY = "it.polimi.dima.sound4u.key.MODEL_KEY";

    private static final String[] FROM = {"avatar", "username", "full_name"};

    private static final int[] TO = {
            R.id.list_item_avatar,
            R.id.list_item_username,
            R.id.list_item_full_name
    };

    private static final String SOUND_KEY = "it.polimi.dima.sound4u.key.SOUND_KEY";

    private ListView mListView;

    private SimpleAdapter mAdapter;

    private List<Map<String, Object>> mModel;

    private List<User> mRealModel;

    private Sound mReceivedSound;

    private User mMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        mMe = User.load(this);
        if (mMe == null) {
            finish();
        }
        if (savedInstanceState == null || savedInstanceState.getString(SOUND_KEY) == null) {
            mReceivedSound = getIntent().getParcelableExtra(SOUND_EXTRA);
        }
        mListView = getListView();
        mModel = new LinkedList<Map<String, Object>>();
        mRealModel = new LinkedList<User>();
        mAdapter = new UserAdapter();
        mListView.setAdapter(mAdapter);
        handleIntent(getIntent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_REQUEST_ID && resultCode == RESULT_OK) {
            Intent resultIntent = new Intent();
            setResult(resultCode, resultIntent);
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            TextView no_result_msg = (TextView) findViewById(R.id.no_users_found);
            no_result_msg.setVisibility(View.GONE);

            new UserSearchTask().execute(query);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        User receiver = mRealModel.get(position);
        Gift gift = Gift.create(1L, mMe, receiver, mReceivedSound);
        new GiftSenderTask(this).execute(gift);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_search, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        return true;
    }

    private void doLogout() {
        mMe.logout(this);
        final Intent firstAccessIntent = new Intent(this, FirstAccessActivity.class);
        startActivity(firstAccessIntent);
        finish();
    }

    private class UserAdapter extends SimpleAdapter {

        public UserAdapter() {
            super(UserSearchActivity.this, mModel, R.layout.user_list_item, FROM, TO);
            this.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    switch (view.getId()) {
                        case R.id.list_item_avatar:
                            String avatarURL = (String) data;
                            ImageView coverImageView = (ImageView) view;
                            if (avatarURL != null) {
                                new DownloadImageTask(coverImageView).execute(avatarURL);
                            }
                            break;
                        case R.id.list_item_full_name:
                            String full_name = (String) data;
                            TextView fullNameTextView = (TextView) view;
                            fullNameTextView.setText(full_name);


                            Log.w(UserSearchActivity.class.getName(), "fullname:"+full_name);


                            break;
                        case R.id.list_item_username:
                            String username = (String) data;
                            TextView usernameTextView = (TextView) view;
                            usernameTextView.setText(username);
                            break;
                    }
                    return true;
                }
            });
        }
    }

    public class UserSearchTask extends AsyncTask<String, Void, List<User>> {

        private ProgressDialog mProgressDialog;

        private ApiWrapper wrapper;

        public UserSearchTask() {
        }

        @Override
        protected List<User> doInBackground(String... params) {
            List<User> userList = new LinkedList<User>();
            try {
                User user = User.load(UserSearchActivity.this);
                wrapper.login(user.getUsername(), user.getPassword());
                HttpResponse response = wrapper.get(Request.to("/users").with("[q]", params[0]));
                if(response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseBody = EntityUtils.toString(entity);
                        JsonArray jsonArray = JsonArray.readFrom(responseBody);
                        for (JsonValue item: jsonArray.values()) {
                            User userItem = User.create((JsonObject) item);
                            userList.add(userItem);
                        }
                        if (jsonArray.isEmpty()){
                            TextView no_result_msg = (TextView) findViewById(R.id.no_users_found);
                            no_result_msg.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (response.getStatusLine().getStatusCode() == 403) {
                    user.logout(UserSearchActivity.this);
                    Intent accessIntent = new Intent(UserSearchActivity.this, FirstAccessActivity.class);
                    UserSearchActivity.this.startActivity(accessIntent);
                    UserSearchActivity.this.finish();
                }
            } catch (Exception e) {
                Log.w(TAG_LOG, e.getMessage());
            }
            return userList;
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
            mProgressDialog = ProgressDialog.show(UserSearchActivity.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected void onPostExecute(List<User> list) {
            mModel.clear();
            mRealModel.clear();
            for(User user: list) {
                final Map<String, Object> item = new HashMap<String, Object>();
                item.put("avatar", user.getAvatar());
                item.put("username", user.getUsername());
                if (user.getFullName() != null) {
                    item.put("full_name", user.getFullName());
                } else {
                    item.put("full_name", "");
                }
                item.put("user", user);
                mModel.add(item);
            }
            UserSearchActivity.this.getListView().setAdapter(new UserAdapter());
            UserSearchActivity.this.mRealModel.addAll(list);
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            case R.id.action_logout:
                doLogout();
                return true;
            case R.id.action_help:
                toHelp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MODEL_KEY, User.listToJson(mRealModel));
        outState.putString(SOUND_KEY, mReceivedSound.toJsonObject().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        JsonObject jsonObject = JsonObject.readFrom(state.getString(SOUND_KEY));
        mReceivedSound = Sound.create(jsonObject);
        String model = state.getString(MODEL_KEY);
        if (model != null) {
            mRealModel = User.jsonToList(model);
            for(User user: mRealModel) {
                final Map<String, Object> item = new HashMap<String, Object>();
                item.put("avatar", user.getAvatar());
                item.put("username", user.getUsername());
                if (user.getFullName() != null) {
                    item.put("full_name", user.getFullName());
                } else {
                    item.put("full_name", "");
                }
                item.put("user", user);
                mModel.add(item);
            }
            this.getListView().setAdapter(new UserAdapter());
        }
    }

    private void toHelp() {
        Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);
    }
}


