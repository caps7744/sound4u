package it.polimi.dima.sound4u.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.appspot.sound4u_backend.sound4uendpoints.Sound4uendpoints;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.model.Gift;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MyGiftsActivity extends ListActivity {

    private static final String[] FROM = {"sender", "receiver", "cover", "title", "artist"};

    private static final int[] TO = {
            R.id.list_item_sender,
            R.id.list_item_receiver,
            R.id.list_item_cover,
            R.id.list_item_title,
            R.id.list_item_artist
    };

    private static final String MODEL_KEY = "it.polimi.dima.sound4u.key.MODEL_KEY";

    private List<Map<String, Object>> mModel = new LinkedList<Map<String, Object>>();

    private ArrayList<Gift> mRealModel = new ArrayList<Gift>();

    private ListView mListView;

    private SimpleAdapter mAdapter;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gifts);
        mUser = User.load(this);
        if (mUser == null) {
            finish();
        }
        mAdapter = new MyGiftsAdapter();
        mListView = getListView();
        mListView.setAdapter(mAdapter);
        if (savedInstanceState == null || savedInstanceState.getParcelableArrayList(MODEL_KEY) == null){
            new MyGiftsTasks().execute(mUser.getId());
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        playGift(position);
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
            case R.id.action_logout:
                doLogout();
                return true;
            case R.id.action_help:
                toHelp();
                return true;
            case R.id.to_sound_search:
                toSoundSearch();
                return true;
            case R.id.refresh:{
                new MyGiftsTasks().execute(mUser.getId());
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doLogout() {
        mUser.logout(this);
        final Intent firstAccessIntent = new Intent(this, FirstAccessActivity.class);
        startActivity(firstAccessIntent);
        finish();
    }

    public void toSoundSearch(){
        Intent intent = new Intent(this, SoundSearchActivity.class);
        startActivity(intent);
    }

    public void playGift(int position) {
        Sound extraSound = mRealModel.get(position).getSound();
        Intent playIntent = new Intent(PlayerActivity.PLAYER_ACTION);
        playIntent.putExtra(PlayerActivity.SOUND_EXTRA, extraSound);
        startActivity(playIntent);

    }

    private class MyGiftsAdapter extends SimpleAdapter {

        public MyGiftsAdapter() {
            super(MyGiftsActivity.this, mModel, R.layout.gift_list_item, FROM, TO);
            this.setViewBinder(new SimpleAdapter.ViewBinder() {

                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    switch (view.getId()) {
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
                    }
                    return true;
                }
            });
        }
    }

    private Sound searchSound(long id) {
        for(Gift item: mRealModel) {
            if (item.getSound().getId() == id) {
                return item.getSound();
            }
        }
        return null;
    }

    private class MyGiftsTasks extends AsyncTask<Long, Void, List<Gift>> {

        private Sound4uendpoints service;

        private ProgressDialog mProgressDialog;

        public MyGiftsTasks() { }

        @Override
        protected List<Gift> doInBackground(Long... params) {
            List<Gift> myGifts = new LinkedList<Gift>();
            try {
                List<com.appspot.sound4u_backend.sound4uendpoints.model.Gift> collection = service.list(params[0]).execute().getItems();
                if(collection == null) {
                    return myGifts;
                }
                for (com.appspot.sound4u_backend.sound4uendpoints.model.Gift item: collection) {
                    User sender = User.create(item.getSenderID(), item.getSenderUsername());
                    User receiver = User.create(item.getReceiverID(), item.getReceiverUsername());
                    User author = User.create(item.getSoundArtistID(), item.getSoundArtistUsername());
                    Sound sound = Sound.create(item.getSoundID(), item.getSoundTitle())
                            .withCoverURL(item.getCoverURL())
                            .withAuthor(author)
                            .withStreamURL(item.getStreamURL());
                    if (item.getCoverURL() != null) {
                        Bitmap cover = null;
                        try {
                            InputStream in = new URL(item.getCoverURL()).openStream();
                            cover = BitmapFactory.decodeStream(in);
                        } catch (IOException e) {
                            Log.w(MyGiftsTasks.class.getName(), e.getMessage());
                        }
                        sound = sound.withCover(cover);
                    }
                    Gift giftItem = Gift.create(
                            item.getId(),
                            sender,
                            receiver,
                            sound
                    );
                    myGifts.add(giftItem);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return myGifts;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(MyGiftsActivity.this, "", "Loading. Please wait...", true);
            Sound4uendpoints.Builder builder = new Sound4uendpoints.Builder(
                    AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
            service = builder.build();

            TextView no_gifts = (TextView) findViewById(R.id.no_gifts);
            no_gifts.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(List<Gift> gifts) {
            super.onPostExecute(gifts);
            mModel.clear();
            mRealModel.clear();
            if (!gifts.isEmpty()) {
                mRealModel.addAll(gifts);
                for(Gift gift: gifts) {
                    final Map<String, Object> item = new HashMap<String, Object>();
                    item.put("sender", gift.getSender().getUsername());
                    item.put("receiver", gift.getReceiver().getUsername());
                    item.put("cover", gift.getSound().getCover());
                    item.put("title", gift.getSound().getTitle());
                    item.put("artist", gift.getSound().getAuthor().getUsername());
                    mModel.add(item);
                }
            } else if (gifts.isEmpty()){
                TextView no_gifts = (TextView) findViewById(R.id.no_gifts);
                no_gifts.setVisibility(View.VISIBLE);
            }
            mAdapter.notifyDataSetChanged();
            mListView.setAdapter(mAdapter);
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MODEL_KEY, mRealModel);
        // outState.putString(MODEL_KEY, Gift.listToJson(mRealModel));
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        ArrayList<Gift> model = state.getParcelableArrayList(MODEL_KEY);
        if (model != null) {
            mUser = User.load(this);
            if (mUser == null) {
                finish();
            }
            mAdapter = new MyGiftsAdapter();
            mListView = getListView();
            mListView.setAdapter(mAdapter);
            mRealModel = model;
            if (!mRealModel.isEmpty()) {
                for(Gift gift: mRealModel) {
                    final Map<String, Object> item = new HashMap<String, Object>();
                    item.put("sender", gift.getSender().getUsername());
                    item.put("receiver", gift.getReceiver().getUsername());
                    item.put("cover", gift.getSound().getCover());
                    item.put("title", gift.getSound().getTitle());
                    item.put("artist", gift.getSound().getAuthor().getUsername());
                    mModel.add(item);
                }
            } else if (mRealModel.isEmpty()){
                TextView no_gifts = (TextView) findViewById(R.id.no_gifts);
                no_gifts.setVisibility(View.VISIBLE);
            }
            mAdapter.notifyDataSetChanged();
            mListView.setAdapter(mAdapter);
        }
    }

    private void toHelp() {
        Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);
    }
}
