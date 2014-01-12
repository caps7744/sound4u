package it.polimi.dima.sound4u.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class AddingGiftTextActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(it.polimi.dima.sound4u.R.layout.activity_adding_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(it.polimi.dima.sound4u.R.menu.adding_gift_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case it.polimi.dima.sound4u.R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    To delete - just for the interface
     */
    public void backToPlayer(View view){
        Intent intent = new Intent(this, UserSearchActivity.class);
        startActivity(intent);
    }
}
