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
import it.polimi.dima.sound4u.service.GiftService;
import it.polimi.dima.sound4u.service.UserService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserSearchActivity extends ListActivity {

    private static final String TAG_LOG = SoundSearchActivity.class.getName();

    public static final String USERSEARCH_ACTION = Const.PKG + ".action.USERSEARCH_ACTION";

    public static final String SOUND_EXTRA = Const.PKG + ".extra.SOUND_EXTRA";

    private static final String[] FROM = {"avatar", "username"};

    private static final int[] TO = {
            R.id.list_item_avatar,
            R.id.list_item_username
    };

    private ListView mListView;

    private SimpleAdapter mAdapter;

    private List<Map<String, Object>> mModel = new LinkedList<Map<String, Object>>();

    private List<User> mRealModel = new LinkedList<User>();

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
        mReceivedSound = getIntent().getParcelableExtra(SOUND_EXTRA);
        mListView = getListView();
        mAdapter = new SimpleAdapter(this, mModel, R.layout.user_list_item, FROM, TO);
        mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                switch (view.getId()) {
                    case R.id.list_item_avatar:
                        String avatarURL = (String) data;
                        ImageView coverImageView = (ImageView) view;
                        if(avatarURL != null) {
                            new DownloadImageTask(coverImageView).execute(avatarURL);
                        }
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
        mListView.setAdapter(mAdapter);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            final List<User> results = UserService.load(query);
            mModel.clear();
            mRealModel.clear();
            mRealModel.addAll(results);
            for(User user: results) {
                if(user.getId() != mMe.getId()) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("avatar", user.getAvatar());
                    item.put("username", user.getUsername());
                    mModel.add(item);
                }
            }
            mAdapter.notifyDataSetChanged();
            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent resultIntent = new Intent();
        User receiver = mRealModel.get(position);
        if (GiftService.sendGift(mMe, receiver, mReceivedSound)) {
            setResult(RESULT_OK, resultIntent);
        } else {
            setResult(RESULT_CANCELED, resultIntent);
        }
        finish();
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

}