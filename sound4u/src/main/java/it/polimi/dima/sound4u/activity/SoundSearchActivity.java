package it.polimi.dima.sound4u.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.DownloadImageTask;
import it.polimi.dima.sound4u.service.SoundSearchTask;
import it.polimi.dima.sound4u.service.SoundService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SoundSearchActivity extends ListActivity {

    private static final String TAG_LOG = SoundSearchActivity.class.getName();

    public static final String SOUNDSEARCH_ACTION = Const.PKG + ".action.SOUNDSEARCH_ACTION";

    private static final String[] FROM = {"cover", "title", "artist", "sound"};

    private static final int[] TO = {
            R.id.list_item_cover,
            R.id.list_item_title,
            R.id.list_item_artist,
            R.id.list_item_send
    };

    private static final int USER_REQUEST_ID = 2;

    private ListView mListView;

    private SimpleAdapter mAdapter;

    private List<Map<String, Object>> mModel = new LinkedList<Map<String, Object>>();

    private List<Sound> mRealModel = new LinkedList<Sound>();

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_search);
        mUser = User.load(this);
        if (mUser == null) {
            finish();
        }
        mListView = getListView();
        mAdapter = new SimpleAdapter(this, mModel, R.layout.sound_list_item, FROM, TO);
        mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                switch (view.getId()) {
                    case R.id.list_item_cover:
                        String coverURL = (String) data;
                        ImageView coverImageView = (ImageView) view;
                        if(coverURL != null) {
                            new DownloadImageTask(coverImageView).execute(coverURL);
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
                    case R.id.list_item_send:
                        final Sound sound = (Sound) data;
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent userSearchIntent = new Intent(UserSearchActivity.USERSEARCH_ACTION);
                                userSearchIntent.putExtra(UserSearchActivity.SOUND_EXTRA, sound);
                                startActivityForResult(userSearchIntent, USER_REQUEST_ID);
                            }
                        });
                }
                return true;
            }
        });
        mListView.setAdapter(mAdapter);
        handleIntent(getIntent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_REQUEST_ID) {
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
            new SoundSearchTask(this, query).execute();
            final List<Sound> result = SoundService.load(query);
            mModel.clear();
            mRealModel.clear();
            mRealModel.addAll(result);
            for(Sound sound: result) {
                final Map<String, Object> item = new HashMap<String, Object>();
                item.put("cover", sound.getCover());
                item.put("title", sound.getTitle());
                item.put("artist", sound.getAuthor().getUsername());
                item.put("sound", sound);
                mModel.add(item);
            }
            mAdapter.notifyDataSetChanged();
            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v, position, id);
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
                return true;
            case R.id.action_logout:
                doLogout();
                return true;
            case R.id.action_settings:
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
        Intent playIntent = new Intent(PlayerActivity.PLAYER_ACTION);
        Sound extraSound = mRealModel.get(position);
        playIntent.putExtra(PlayerActivity.SOUND_EXTRA, extraSound);
        startActivity(playIntent);
    }
}
