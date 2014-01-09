package it.polimi.dima.sound4u.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.SoundService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SoundSearchActivity extends ListActivity {

    public static final String SOUNDSEARCH_ACTION = Const.PKG + ".action.SOUNDSEARCH_ACTION";

    private static final String[] FROM = {"cover", "title", "artist", "id"};

    private static final int[] TO = {
            R.id.list_item_cover,
            R.id.list_item_title,
            R.id.list_item_artist,
            R.id.list_item_send
    };

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
                        // TODO Manage the real cover as in the tutorial saved
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
                        final long id = (Long) data;
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent userSearchIntent = new Intent(UserSearchActivity.USERSEARCH_ACTION);
                                userSearchIntent.putExtra(UserSearchActivity.SOUNDID_EXTRA, id);
                                startActivity(userSearchIntent);
                            }
                        });
                }
                return true;
            }
        });
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        playGift(position);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final List<Sound> result = SoundService.load();
        mModel.clear();
        mRealModel.clear();
        mRealModel.addAll(result);
        for(Sound sound: result) {
            final Map<String, Object> item = new HashMap<String, Object>();
            item.put("cover", sound.getCover());
            item.put("title", sound.getTitle());
            item.put("artist", sound.getAuthor().getUsername());
            item.put("id", sound.getId());
            mModel.add(item);
        }
        mAdapter.notifyDataSetChanged();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sound_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void playGift(int position) {
        Intent playIntent = new Intent(PlayerActivity.PLAYER_ACTION);
        Sound extraSound = mRealModel.get(position);
        playIntent.putExtra(PlayerActivity.SOUND_EXTRA, extraSound);
        startActivity(playIntent);
    }
}
