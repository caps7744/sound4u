package it.polimi.dima.sound4u.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.model.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class SoundSearchActivity extends ListActivity {

    private static final String TAG_LOG = SoundSearchActivity.class.getName();

    public static final String SOUNDSEARCH_ACTION = Const.PKG + ".action.SOUNDSEARCH_ACTION";

    public static final int USER_REQUEST_ID = 2;
    public static final String MODEL_KEY = "it.polimi.dima.sound4u.key.MODEL_KEY";
    private static final int USER_SEARCH_ID = 1;

    private final String[] FROM = {"cover", "title", "artist", "sound"};

    private final int[] TO = {
            R.id.list_item_cover,
            R.id.list_item_title,
            R.id.list_item_artist,
            R.id.list_item_send
    };

    private ListView mListView;

    private SimpleAdapter mAdapter;

    private List<Map<String, Object>> mModel;

    private ArrayList<Sound> mRealModel;

    private User mUser;

    private AlertDialog mAlertDialog;

    private DialogInterface.OnClickListener mDialogOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_search);
        mUser = User.load(this);
        if (mUser == null) {
            finish();
        }
        mListView = getListView();
        mModel = new LinkedList<Map<String, Object>>();
        mRealModel = new ArrayList<Sound>();
        List<Map<String,Object>> mModel = new LinkedList<Map<String, Object>>();
        mAdapter = new SoundAdapter();
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

            TextView no_result_msg = (TextView) findViewById(R.id.no_sound_results);
            no_result_msg.setVisibility(View.GONE);

            new SoundSearchTask().execute(query);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        playGift(position);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sound_search, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                Intent i = new Intent(this, SoundSearchActivity.class);
                startActivity(i);
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

    private void doLogout() {
        mUser.logout(this);
        final Intent firstAccessIntent = new Intent(this, FirstAccessActivity.class);
        startActivity(firstAccessIntent);
        finish();
    }

    public void playGift(int position) {
        Sound extraSound = mRealModel.get(position);
        Intent playIntent = new Intent(PlayerActivity.PLAYER_ACTION);
        playIntent.putExtra(PlayerActivity.SOUND_EXTRA, extraSound);
        startActivity(playIntent);
    }

    private class SoundAdapter extends SimpleAdapter {

        public SoundAdapter() {
            super(SoundSearchActivity.this, mModel, R.layout.sound_list_item, FROM, TO);
            this.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    switch (view.getId()) {
                        case R.id.list_item_cover:
                            Bitmap cover = (Bitmap) data;
                            ImageView coverImageView = (ImageView) view;
                            if (cover != null) {
                                coverImageView.setImageBitmap(cover);
                            }
                            break;
                        case R.id.list_item_title:
                            String title = (String) data;
                            TextView titleTextView = (TextView) view;
                            titleTextView.setText(title);
                            break;
                        case R.id.list_item_artist:
                            String artist = (String) data;
                            TextView artistTextView = (TextView) view;
                            artistTextView.setText(artist);
                            break;
                        /*case R.id.list_item_send:
                            final Sound sound = (Sound) data;
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    searchUser(sound);
                                }
                            });*/
                    }
                    return true;
                }
            });
        }

        private void searchUser(final Sound sound) {
            Intent userSearchIntent = new Intent(UserSearchActivity.USERSEARCH_ACTION);
            userSearchIntent.putExtra(UserSearchActivity.SOUND_EXTRA, sound);
            SoundSearchActivity.this.startActivityForResult(userSearchIntent, USER_REQUEST_ID);
        }
    }

    private Sound searchSound(long id) {
        for(Sound item: mRealModel) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public class SoundSearchTask extends AsyncTask<String, Void, List<Sound>> {

        private ProgressDialog mProgressDialog;

        private ApiWrapper wrapper;

        private User me;

        public SoundSearchTask() {
        }

        @Override
        protected List<Sound> doInBackground(String... params) {
            List<Sound> soundList = new LinkedList<Sound>();
            try {
                HttpResponse response = wrapper.get(Request.to("/tracks").with("[q]", params[0]));
                if(response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseBody = EntityUtils.toString(entity);
                        JsonArray jsonArray = JsonArray.readFrom(responseBody);
                        for (JsonValue item: jsonArray.values()) {
                            try {
                                Sound soundItem = Sound.create((JsonObject) item);
                                if (soundItem.getCoverURL() != null) {
                                    Bitmap cover = null;
                                    try {
                                        InputStream in = new URL(soundItem.getCoverURL()).openStream();
                                        cover = BitmapFactory.decodeStream(in);
                                    } catch (IOException e) {
                                        Log.w(SoundSearchTask.class.getName(), e.getMessage());
                                    }
                                    soundItem = soundItem.withCover(cover);
                                }
                                soundList.add(soundItem);
                            } catch (IllegalArgumentException e) {
                                // Nothing to do
                            }
                        }
                        if (jsonArray.isEmpty()){
                            TextView no_result_msg = (TextView) findViewById(R.id.no_sound_results);
                            no_result_msg.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (response.getStatusLine().getStatusCode() == 403) {
                    me.logout(SoundSearchActivity.this);
                    Intent accessIntent = new Intent(SoundSearchActivity.this, FirstAccessActivity.class);
                    SoundSearchActivity.this.startActivity(accessIntent);
                    SoundSearchActivity.this.finish();
                }
            } catch (Exception e) {
                Log.w(TAG_LOG, e.getMessage());
            }
            return soundList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            me = User.load(SoundSearchActivity.this);
            wrapper = new ApiWrapper(
                    SoundCloudConst.CLIENT_ID,
                    SoundCloudConst.CLIENT_SECRET,
                    null,
                    me.getToken()
            );
            mProgressDialog = ProgressDialog.show(SoundSearchActivity.this, "", "Loading. Please wait...", true);
        }

        @Override
        protected void onPostExecute(List<Sound> list) {
            mModel.clear();
            mRealModel.clear();
            for(Sound sound: list) {
                final Map<String, Object> item = new HashMap<String, Object>();
                item.put("cover", sound.getCover());
                item.put("title", sound.getTitle());
                if (sound.getAuthor() != null) {
                    item.put("artist", sound.getAuthor().getUsername());
                } else {
                    item.put("artist", "");
                }
                item.put("sound", sound);
                mModel.add(item);
            }
            mRealModel.addAll(list);
            mAdapter.notifyDataSetChanged();
            mListView.setAdapter(mAdapter);
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MODEL_KEY, mRealModel);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        ArrayList<Sound> model = state.getParcelableArrayList(MODEL_KEY);
        mUser = User.load(this);
        if (mUser == null) {
            finish();
        }
        mListView = getListView();
        mModel = new LinkedList<Map<String, Object>>();
        mAdapter = new SoundAdapter();

        if(model!=null){
            mRealModel = model;

            for(Sound sound: mRealModel) {
                final Map<String, Object> item = new HashMap<String, Object>();
                item.put("cover", sound.getCover());
                item.put("title", sound.getTitle());
                if (sound.getAuthor() != null) {
                   item.put("artist", sound.getAuthor().getUsername());
                } else {
                    item.put("artist", "");
                }
                item.put("sound", sound);
                mModel.add(item);
                mAdapter.notifyDataSetChanged();
                mListView.setAdapter(mAdapter);
            }
        }
    }

    private void toHelp() {
        Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);
    }
}
