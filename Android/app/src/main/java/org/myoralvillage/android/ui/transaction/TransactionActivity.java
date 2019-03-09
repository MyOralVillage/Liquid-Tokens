package org.myoralvillage.android.ui.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.badoualy.stepperindicator.StepperIndicator;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVUser;

public class TransactionActivity extends AppCompatActivity implements OnTransactionPageInteractionListener {

    public static final String EXTRA_TRANSACTION_TYPE = "transaction_type";
    public static final String EXTRA_TRANSACTION_SEND_TO = "transaction_send_to";

    private static final String ARG_TRANSACTION_TYPE = EXTRA_TRANSACTION_TYPE;
    private static final String ARG_TRANSACTION_SEND_TO = EXTRA_TRANSACTION_SEND_TO;
    private static final String ARG_CURRENT_ITEM = "current_item";

    public static final int TRANSACTION_TYPE_SEND = 0;
    public static final int TRANSACTION_TYPE_REQUEST = 1;

    private int transactionType;
    private String transactionSendTo;

    private int currentItem = 0;

    private TransactionPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    private MaterialButton backButton;
    private MaterialButton nextButton;

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            currentItem = position;

            if(position == 0) {
                setCanGoBack(false);
            } else {
                setCanGoBack(true);
            }

            Fragment currentPage = pagerAdapter.getItem(viewPager.getCurrentItem());
            if(currentPage instanceof TransactionPage) {
                TransactionPage pageMadeActiveListener = (TransactionPage) currentPage;
                pageMadeActiveListener.onPageMadeActive();

                nextButton.setText(pageMadeActiveListener.getForwardButtonText());
                nextButton.setIconResource(pageMadeActiveListener.getForwardButtonIcon());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Intent intent = getIntent();

        if(intent != null) {
            transactionType = intent.getIntExtra(EXTRA_TRANSACTION_TYPE, 0);
            transactionSendTo = intent.getStringExtra(EXTRA_TRANSACTION_SEND_TO);
        }

        if(savedInstanceState != null) {
            transactionType = savedInstanceState.getInt(ARG_TRANSACTION_TYPE);
            transactionSendTo = savedInstanceState.getString(ARG_TRANSACTION_SEND_TO);
            currentItem = savedInstanceState.getInt(ARG_CURRENT_ITEM, 0);
        }

        if(transactionSendTo != null) {
            DatabaseReference sendToRef = FirebaseDatabase.getInstance().getReference().child("users/"+transactionSendTo);
            sendToRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    MOVUser user = dataSnapshot.getValue(MOVUser.class);

                    TransactionViewModel viewModel = ViewModelProviders.of(TransactionActivity.this).get(TransactionViewModel.class);
                    viewModel.setSelectedContact(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //TODO: Handle user not found
                }
            });
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            switch(transactionType) {
                case TRANSACTION_TYPE_REQUEST:
                    actionBar.setTitle(R.string.transactions_request_money);
                    break;
                case TRANSACTION_TYPE_SEND:
                    actionBar.setTitle(R.string.transactions_send_money);
                    break;
            }
        }

        backButton = findViewById(R.id.transaction_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(backButton.isEnabled() && currentItem > 0) {
                    viewPager.setCurrentItem(currentItem - 1, true);
                }
            }
        });
        nextButton = findViewById(R.id.transaction_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment currentPage = pagerAdapter.getItem(currentItem);
                if(currentPage instanceof TransactionPage) {
                    ((TransactionPage) currentPage).onNextButtonPressed();
                }

                viewPager.setCurrentItem(currentItem + 1, true);
            }
        });

        // TODO: get currency from actual config
        pagerAdapter = new TransactionPagerAdapter(getSupportFragmentManager(),
                transactionType,
                "cad",
                transactionSendTo);
        viewPager = findViewById(R.id.transaction_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.setCurrentItem(currentItem, false);
        pageChangeListener.onPageSelected(currentItem);


        StepperIndicator stepperIndicator = findViewById(R.id.transaction_stepper);
        stepperIndicator.setViewPager(viewPager);
    }

    @Override
    public void setCanGoForward(Fragment fragment, boolean canGoForward) {
        if(!isCurrentFragment(fragment)) {
            return;
        }

        setCanGoForward(canGoForward);
    }

    private boolean isCurrentFragment(Fragment fragment) {
        int currentPosition = viewPager.getCurrentItem();

        return fragment.getClass().isInstance(pagerAdapter.getItem(currentPosition));
    }

    private void setCanGoBack(boolean canGoBack) {
        backButton.setEnabled(canGoBack);
    }

    private void setCanGoForward(boolean canGoForward) {
        nextButton.setEnabled(canGoForward);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}