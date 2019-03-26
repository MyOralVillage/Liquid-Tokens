package org.myoralvillage.android.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.util.GlideApp;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class HistoryTransactionActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_history_transaction);

        final TextView text_date = (TextView) findViewById(R.id.history_transaction_date);
        final TextView text_amount = (TextView) findViewById(R.id.history_transaction_amount);
        final ImageView image_from = (ImageView) findViewById(R.id.history_transaction_from);
        final ImageView image_to = (ImageView) findViewById(R.id.history_transaction_to);


        Intent intent = getIntent();
        String from = intent.getStringExtra(HistoryFragment.HISTORY_FROM);
        String to = intent.getStringExtra(HistoryFragment.HISTORY_TO);
        int amount = intent.getIntExtra(HistoryFragment.HISTORY_AMOUNT,-1);
        String currency = intent.getStringExtra(HistoryFragment.HISTORY_CURRENCY);
        long time = intent.getLongExtra(HistoryFragment.HISTORY_TIME,0);

        if(amount < 0){
            finish();
        }
        String string_time = convertTime(time);
        text_date.setText(string_time);
        text_amount.setText(""+printAmount(amount)+" "+currency);

        StorageReference to_reference = FirebaseStorage.getInstance().getReference("/users/"+to+"/profile.jpg");
        StorageReference from_reference = FirebaseStorage.getInstance().getReference("/users/"+from+"/profile.jpg");

        GlideApp.with(this)
                .load(from_reference)
                .dontAnimate()
                .into(image_from);

        GlideApp.with(this)
                .load(to_reference)
                .dontAnimate()
                .into(image_to);

    }

    /* From https://stackoverflow.com/questions/6782185/convert-timestamp-long-to-normal-date-format
        Given a long representing the seconds since the Epoch (00:00:00 Thursday, 1 January 1970)
        Format it into a yyyy/MM/dd HH:mm:ss format for easy of printing to screen.
     */
    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return format.format(date);
    }

    /*
        printAmount turns the int Amount representing that many cents into printable dollar form.

     */
    private String printAmount(int am) {
        int amHigh = am/100;
        int amLow = am%100;
        String strAmLow;
        //for the case when amount ends in 0 add a 0 at the end
        // unless amount <100 which in 0.100
        if (amLow % 10 == 0 && am > 99){
            strAmLow = ""+amLow+"0";
        }
        else
            strAmLow =  ""+amLow;
        return ""+amHigh+"."+strAmLow;
    }

}
