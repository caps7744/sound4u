package it.polimi.dima.sound4u.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import com.appspot.sound4u_backend.sound4uendpoints.Sound4uendpoints;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import it.polimi.dima.sound4u.model.Gift;

import java.io.IOException;

/**
 * Created by canidio-andrea on 16/01/14.
 */
public class GiftSenderTask extends AsyncTask<Gift, Void, Boolean>{

    public static final String SHARED_GIFT_EXTRA = "it.polimi.dima.sound4u.extra.SHARED_GIFT_EXTRA";

    private Activity context;

    private ProgressDialog mProgressDialog;

    private Sound4uendpoints service;

    private Gift gift;

    public GiftSenderTask(Activity context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Gift... params) {
        gift = params[0];
        com.appspot.sound4u_backend.sound4uendpoints.model.Gift sendGift = new com.appspot.sound4u_backend.sound4uendpoints.model.Gift();
        sendGift.setSenderID(gift.getSender().getId());
        sendGift.setSenderUsername(gift.getSender().getUsername());
        sendGift.setReceiverID(gift.getReceiver().getId());
        sendGift.setReceiverUsername(gift.getReceiver().getUsername());
        sendGift.setSoundID(gift.getSound().getId());
        sendGift.setSoundTitle(gift.getSound().getTitle());
        sendGift.setCoverURL(gift.getSound().getCoverURL());
        sendGift.setStreamURL(gift.getSound().getStreamURL());
        sendGift.setSoundArtistID(gift.getSound().getAuthor().getId());
        sendGift.setSoundArtistUsername(gift.getSound().getAuthor().getUsername());
        if(gift.getSender().getFullName()!=null){
            sendGift.setSenderUsername(gift.getSender().getFullName());
        }
        if(gift.getReceiver().getFullName()!=null){
            sendGift.setReceiverUsername(gift.getReceiver().getFullName());
        }
        try {
            service.add(sendGift).execute();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
        Sound4uendpoints.Builder builder = new Sound4uendpoints.Builder(
                AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
        service = builder.build();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mProgressDialog.dismiss();
        Intent resultIntent = new Intent();
        if (result == true) {
            resultIntent.putExtra(SHARED_GIFT_EXTRA, gift);
            context.setResult(context.RESULT_OK, resultIntent);

        } else {
            context.setResult(context.RESULT_CANCELED, resultIntent);
        }
        context.finish();
    }
    }
