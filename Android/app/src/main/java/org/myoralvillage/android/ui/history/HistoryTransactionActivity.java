package org.myoralvillage.android.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.util.GlideApp;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class HistoryTransactionActivity extends AppCompatActivity {

    private static final int TIME_PER_CIRCLE = 604800; //Seconds in a week
    //2592000 is a month.

    private StorageReference profile_image;
    private String to_location;
    private String from_location;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected void onStart() {
        super.onStart();
        StorageReference image_ref;
        setContentView(R.layout.activity_transaction_history);

        final TextView text_date = findViewById(R.id.transaction_detail_date);
        final TextView text_amount = findViewById(R.id.transaction_detail_amount);

        final TextView phone_number = findViewById(R.id.transaction_detail_phone_number);

        final ImageView hand = findViewById(R.id.transaction_detail_hand);
        final ImageView image = findViewById(R.id.transaction_detail_image);
        final ImageView image_date = findViewById(R.id.transaction_detail_date_image);
        final ImageView image_phone = findViewById(R.id.transaction_detail_phone);
        final ImageView image_currency = findViewById(R.id.transaction_detail_currency);

        Intent intent = getIntent();
        String from = intent.getStringExtra(HistoryFragment.HISTORY_FROM);
        String to = intent.getStringExtra(HistoryFragment.HISTORY_TO);
        int amount = intent.getIntExtra(HistoryFragment.HISTORY_AMOUNT, -1);
        String currency = intent.getStringExtra(HistoryFragment.HISTORY_CURRENCY);
        long time = intent.getLongExtra(HistoryFragment.HISTORY_TIME, 0);
        String phone_num = intent.getStringExtra(HistoryFragment.HISTORY_PHONE);
        final Boolean sender = intent.getBooleanExtra(HistoryFragment.HISTORY_SENDER,false);

        Log.d("ACTIVITY",from);
        Log.d("ACTIVITY",to);
        Log.d("ACTIVITY",""+amount);
        Log.d("ACTIVITY",currency);
        Log.d("ACTIVITY",""+time);
        Log.d("ACTIVITY",phone_num);
        Log.d("ACTIVITY",""+intent.getBooleanExtra(HistoryFragment.HISTORY_SENDER,true));

        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users");
        to_location = "/users/" + to + "/profile.jpg";
        from_location = "/users/" + from + "/profile.jpg";
        users.orderByChild("image").equalTo(to_location).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d("LOGGGGG","NOT");
                    if (sender) {
                        profile_image = FirebaseStorage.getInstance().getReference(to_location);
                    } else {
                        profile_image = FirebaseStorage.getInstance().getReference(from_location);
                    }
                }else{
                    Log.d("LOGGGGG","NULL");
                    profile_image = null;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(sender){
            hand.setImageResource(R.drawable.history_send_money);
        }else{
            hand.setImageResource(R.drawable.history_receive_money);

        }

        String string_time = convertTime(time);
        text_date.setText(string_time);
        text_amount.setText("$"+printAmount(amount));

        int id = this.getResources().getIdentifier("flag_"+currency, "drawable", this.getPackageName());
        if(id>0)
            image_currency.setImageResource(id);
        else
            image_currency.setImageResource(R.drawable.dollar);

        long current_time = System.currentTimeMillis()/1000;
        long difference = current_time-time/1000;
        int month_offset = (int)difference/(TIME_PER_CIRCLE);

        image_date.setImageResource(R.drawable.month_00 + (12 - Math.max(month_offset, 0)));
        image_phone.setImageResource(R.drawable.phone);

        if (phone_num != null)
            phone_number.setText(phone_num);
        else
            phone_number.setText("-------------");
        if(profile_image != null) {
            GlideApp.with(this)
                    .load(profile_image)
                    .dontAnimate()
                    .override(250)
                    .circleCrop()
                    .into(image);
        }
        else{
            image.setImageResource(R.drawable.blank_profile);
        }

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
