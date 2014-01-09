package it.polimi.dima.sound4u.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.conf.Const;
import it.polimi.dima.sound4u.model.Gift;
import it.polimi.dima.sound4u.model.Sound;
import it.polimi.dima.sound4u.model.User;
import it.polimi.dima.sound4u.service.GiftService;

import java.util.LinkedList;
import java.util.List;

public class MyGiftsActivity extends ActionBarActivity {

    public static final String USER_EXTRA = Const.PKG + "action.USER_EXTRA";

    private ListView mListView;

    private ListAdapter mAdapter;

    private List<Gift> mModel = new LinkedList<Gift>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_gifts);
        mListView = (ListView) findViewById(android.R.id.list);
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mModel.size();
            }

            @Override
            public Object getItem(int position) {
                return mModel.get(position);
            }

            @Override
            public long getItemId(int position) {
                Gift gift = (Gift) getItem(position);
                return gift.getId();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.gift_list_item, null);
                }
                final TextView senderTextView = (TextView) convertView.findViewById(R.id.list_item_sender);
                final TextView receiverTextView = (TextView) convertView.findViewById(R.id.list_item_receiver);
                // TODO Management of the cover
                final TextView titleTextView = (TextView) convertView.findViewById(R.id.list_item_title);
                final TextView artistTextView = (TextView) convertView.findViewById(R.id.list_item_artist);
                final Gift item = (Gift) getItem(position);
                senderTextView.setText(item.getSender().getUsername());
                receiverTextView.setText(item.getReceiver().getUsername());
                titleTextView.setText(item.getSound().getTitle());
                artistTextView.setText(item.getSound().getAuthor());
                return convertView;
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent playIntent = new Intent(PlayerActivity.PLAYER_ACTION);
                Sound extraSound = mModel.get(position).getSound();
                playIntent.putExtra(PlayerActivity.SOUND_EXTRA, extraSound);
                startActivity(playIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final List<Gift> result = GiftService.load();
        mModel.clear();
        mModel.addAll(result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_gifts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_search_sound:
                doSearchSound();
                return true;
            case R.id.action_logout:
                doLogout();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doSearchSound() {
        Intent searchIntent = new Intent(SoundSearchActivity.SOUNDSEARCH_ACTION);
        startActivity(searchIntent);
    }

    private void doLogout() {
        // TODO
    }

    public void playGift() {
        // TODO
    }
}
