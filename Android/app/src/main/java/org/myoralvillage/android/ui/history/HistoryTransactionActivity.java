package org.myoralvillage.android.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;
import org.myoralvillage.android.ui.transaction.TransactionViewModel;
import org.myoralvillage.android.ui.transaction.amountselection.TransactionAmountSelectionRecyclerAdapter;
import org.myoralvillage.android.ui.transaction.amountselection.TransactionAmountSelectionViewModel;
import org.myoralvillage.android.ui.util.GlideApp;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class HistoryTransactionActivity extends AppCompatActivity {

    private static final int TIME_PER_CIRCLE = 604800; //Seconds in a week
    MOVCurrency currency;
    private TransactionAmountSelectionRecyclerAdapter selectedRecyclerAdapter;
    private RecyclerView amountSelectedRecycler;
    //2592000 is a month

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected void onStart() {
        super.onStart();
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
        String currency_code = intent.getStringExtra(HistoryFragment.HISTORY_CURRENCY);
        long time = intent.getLongExtra(HistoryFragment.HISTORY_TIME, 0);
        String phone_num = intent.getStringExtra(HistoryFragment.HISTORY_PHONE);
        String name = intent.getStringExtra(HistoryFragment.HISTORY_FROM);
        final Boolean sender = intent.getBooleanExtra(HistoryFragment.HISTORY_SENDER,false);
        final Boolean flag = intent.getBooleanExtra(HistoryFragment.HISTORY_FLAG,false);

        Log.d("ACTIVITY",from);
        Log.d("ACTIVITY",to);
        Log.d("ACTIVITY",""+amount);
        Log.d("ACTIVITY",currency_code);
        Log.d("ACTIVITY",""+time);
        Log.d("ACTIVITY",phone_num);
        Log.d("ACTIVITY",""+intent.getBooleanExtra(HistoryFragment.HISTORY_SENDER,true));
        Log.d("ACTIVITY flag",""+intent.getBooleanExtra(HistoryFragment.HISTORY_FLAG,false));

        String user_str;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users");
        if(user.getUid().equals(to))
            user_str = from;
        else
            user_str = to;
        Query ref = users.orderByChild("uid").equalTo(user_str);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    i++;
                    set_location(datas.child("image").getValue().toString());
                }
                if (i == 0) {
                    set_location("users/profile.jpg/");
                }
                Log.d("DataSnapShot1.5", dataSnapshot + " " + i);
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
        StorageReference profile_image = null;

        if(!intent.getStringExtra(HistoryFragment.HISTORY_PORTRAIT).equals("no_image"))
            profile_image =  FirebaseStorage.getInstance().getReference(intent.getStringExtra(HistoryFragment.HISTORY_PORTRAIT));

        String string_time = convertTime(time);
        String string_amount = "$"+printAmount(amount);
        text_date.setText(string_time);
        text_amount.setText(string_amount);

        int id = this.getResources().getIdentifier("flag_"+currency_code, "drawable", this.getPackageName());
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

        Log.d("LOGGGGGcc",""+profile_image);

        GlideApp.with(this)
                .load(profile_image)
                .dontAnimate()
                .override(250)
                .circleCrop()
                .into(image);

        try {
            currency = MOVCurrency.loadFromJson(getApplicationContext(), currency_code);

            amountSelectedRecycler = findViewById(R.id.transaction_detail_amount_display);

            selectedRecyclerAdapter = new TransactionAmountSelectionRecyclerAdapter(currency, null);
            amountSelectedRecycler.setAdapter(selectedRecyclerAdapter);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            amountSelectedRecycler.setLayoutManager(layoutManager);
            selectedRecyclerAdapter.setAmount(amount);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void set_location(String location) {
        StorageReference profile_image = FirebaseStorage.getInstance().getReference(location);
        final ImageView image = findViewById(R.id.transaction_detail_image);
        GlideApp.with(this)
            .load(profile_image)
            .dontAnimate()
            .override(250)
            .circleCrop()
            .into(image);
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
        int amLowHigh = amLow/10;
        int amLower = amLow-amLowHigh*10;
        if (amLow % 10 == 0 && amLow > 9 && amLower != 0){
            strAmLow = ""+amLow+"0";
        }
        else if(amLow < 10){
            strAmLow = "0"+amLow;
        }
        else
            strAmLow =  ""+amLow;
        return ""+amHigh+"."+strAmLow;
    }

    public static int[] greedyMoney(int amount, int[] denoms){
        int[] to_ret = new int[denoms.length];
        for(int i = 0; i < denoms.length; i++){
            if(denoms[i] <= amount){
                amount -= denoms[i];
                to_ret[i]++;
                i--;
            }
        }

        return to_ret;
    }

}
