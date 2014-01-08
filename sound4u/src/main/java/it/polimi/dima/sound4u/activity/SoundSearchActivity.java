package it.polimi.dima.sound4u.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import it.polimi.dima.sound4u.R;

public class SoundSearchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_search);

        ListView listView = (ListView)findViewById(R.id.list_sound_search_results);

        // Example String Results
        String [] array = new String[]{"Angra - The Course of Nature", "Aquaria - Shambala", "Calico Jack - House of Jewelry", "Judas Priest - Breaking the Law", "Motorhead - Overkill", "Nid - Let it Be", "A - B", "C - D", "E - F", "G - H", "I - J", "K - L"};

        //remember to optimize http://www.simplesoft.it/android/guida-agli-adapter-e-le-listview-in-android.html
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.list_row_sound, R.id.list_element_sound, array);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
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
}
