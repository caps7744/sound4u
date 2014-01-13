package it.polimi.dima.sound4u.activity;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.SearchView;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.Gift;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.DownloadImageTask;
import it.polimi.dima.sound4u.service.GiftService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MyGiftsActivity extends ListActivity {

    private static final String[] FROM = {"sender", "receiver", "cover", "title", "artist"};

    private static final int[] TO = {
            R.id.list_item_sender,
            R.id.list_item_receiver,
            R.id.list_item_cover,
            R.id.list_item_title,
            R.id.list_item_artist
    };

    private static final int SEARCH_SOUND_ID = 1;

    private ListView mListView;

    private SimpleAdapter mAdapter;

    private List<Map<String, Object>> mModel = new LinkedList<Map<String, Object>>();

    private List<Gift> mRealModel = new LinkedList<Gift>();

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_gifts);
        mUser = User.load(this);
        if (mUser == null) {
            finish();
        }
        mListView = getListView();
        mAdapter = new SimpleAdapter(this, mModel, R.layout.gift_list_item, FROM, TO);
        mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                switch(view.getId()) {
                    case R.id.list_item_sender:
                        String senderUsername = (String) data;
                        TextView senderTextView = (TextView) view;
                        senderTextView.setText(senderUsername);
                        break;
                    case R.id.list_item_receiver:
                        String receiverUsername = (String) data;
                        TextView receiverTextView = (TextView) view;
                        receiverTextView.setText(receiverUsername);
                        break;
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
        final List<Gift> result = GiftService.load(mUser);
        mModel.clear();
        mRealModel.clear();
        mRealModel.addAll(result);
        for(Gift gift: result) {
            final Map<String, Object> item = new HashMap<String, Object>();
            item.put("sender", gift.getSender().getUsername());
            item.put("receiver", gift.getReceiver().getUsername());
            item.put("cover", gift.getSound().getCover());
            item.put("title", gift.getSound().getTitle());
            item.put("artist", gift.getSound().getAuthor().getUsername());
            mModel.add(item);
        }
        mAdapter.notifyDataSetChanged();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_gifts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent searchIntent = new Intent(SoundSearchActivity.SOUNDSEARCH_ACTION);
                startActivityForResult(searchIntent, SEARCH_SOUND_ID);
                return true;
            case R.id.action_logout:
                doLogout();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_SOUND_ID && resultCode == RESULT_OK) {
            Toast.makeText(this, "Gift sent!", Toast.LENGTH_SHORT).show();
        }
    }

    private void doLogout() {
        mUser.logout(this);
        final Intent firstAccessIntent = new Intent(this, FirstAccessActivity.class);
        startActivity(firstAccessIntent);
        finish();
    }

    public void playGift(int position) {
        Intent playIntent = new Intent(PlayerActivity.PLAYER_ACTION);
        Sound extraSound = mRealModel.get(position).getSound();
        playIntent.putExtra(PlayerActivity.SOUND_EXTRA, extraSound);
        startActivity(playIntent);
    }
}
