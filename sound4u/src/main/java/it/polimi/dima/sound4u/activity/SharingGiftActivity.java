package it.polimi.dima.sound4u.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import it.polimi.dima.sound4u.R;
import it.polimi.dima.sound4u.model.Gift;

public class SharingGiftActivity extends Activity implements View.OnClickListener {

    private Button btn_share_facebook, btn_share_google_plus, btn_done_sharing;

    private Gift gift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_gift);

        btn_share_facebook = (Button)findViewById(R.id.btn_share_facebook);
        btn_share_google_plus = (Button)findViewById(R.id.btn_share_google_plus);
        btn_done_sharing = (Button)findViewById(R.id.btn_done_sharing);

        btn_share_facebook.setOnClickListener(this);
        btn_share_google_plus.setOnClickListener(this);
        btn_done_sharing.setOnClickListener(this);

        gift = getIntent().getParcelableExtra(PlayerActivity.SHARING_GIFT_EXTRA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_share_facebook:
                shareOnFacebook(gift);
                break;
            case R.id.btn_share_google_plus:
                break;
            case R.id.btn_done_sharing:
                finish();
                break;
        }
    }

    private void shareOnFacebook(Gift gift) {
        Intent intent = new Intent(this, FacebookConnect.class);
        intent.putExtra(FacebookConnect.FACEBOOK_POST_EXTRA, gift);
        startActivity(intent);
    }
}
