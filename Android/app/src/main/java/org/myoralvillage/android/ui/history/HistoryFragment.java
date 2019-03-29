package org.myoralvillage.android.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.data.transaction.MOVTransaction;
import org.myoralvillage.android.ui.CurrentUserViewModel;
import org.myoralvillage.android.ui.util.GlideApp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnTransactionListener {

    private static final String LOG_TAG = "HistoryFragment";
    static final String HISTORY_FROM = "org.myoralvillage.android.ui.history.from";
    static final String HISTORY_TO = "org.myoralvillage.android.ui.history.to";
    static final String HISTORY_TIME = "org.myoralvillage.android.ui.history.time";
    static final String HISTORY_AMOUNT = "org.myoralvillage.android.ui.history.amount";
    static final String HISTORY_CURRENCY = "org.myoralvillage.android.ui.history.currency";
    static final String HISTORY_PHONE = "org.myoralvillage.android.ui.history.phone";
    static final String HISTORY_SENDER = "org.myoralvillage.android.ui.history.sender"; //boolean, if the user is the sender
    static final String HISTORY_PORTRAIT = "org.myoralvillage.android.ui.history.portrait";
    static  final String HISTORY_FLAG = "org.myoralvillage.android.ui.history.flag";
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    private DataSnapshot toSnapshot;
    private DataSnapshot fromSnapshot;
    private DataSnapshot users;

    private Query usersQuery;
    private Query fromQuery;
    private Query toQuery;

    private String location = "users/profile.jpg/";

    private List<MOVTransaction> transactions;

    private ValueEventListener toListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            toSnapshot = dataSnapshot;
            onUpdateSnapshot();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener fromListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            fromSnapshot = dataSnapshot;
            onUpdateSnapshot();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener usersListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            users = dataSnapshot;
            onUpdateSnapshot();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private boolean flag = false;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        usersQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("uid");
        toQuery = FirebaseDatabase.getInstance().getReference().child("transactions").orderByChild("to").equalTo(user.getUid());
        fromQuery = FirebaseDatabase.getInstance().getReference().child("transactions").orderByChild("from").equalTo(user.getUid());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        RecyclerView historyRecycler = view.findViewById(R.id.history_recycler);
        historyRecycler.setLayoutManager(layoutManager);
        setUpRecyclerView(historyRecycler);

        return view;
    }

    private void setUpRecyclerView(RecyclerView recyclerView) {
        transactions = new ArrayList<>();
        int defaultHeightPx = getResources().getDimensionPixelSize(R.dimen.history_cell_height);
        if(getActivity() == null) {
            throw new IllegalStateException("Something went error. activity null");
        }
        CurrentUserViewModel userViewModel = ViewModelProviders.of(getActivity()).get(CurrentUserViewModel.class);

        adapter = new HistoryAdapter(userViewModel, transactions, defaultHeightPx, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        toQuery.addValueEventListener(toListener);
        fromQuery.addValueEventListener(fromListener);
        usersQuery.addValueEventListener(usersListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        toQuery.removeEventListener(toListener);
        fromQuery.removeEventListener(fromListener);
        usersQuery.removeEventListener(usersListener);
    }

    private void onUpdateSnapshot() {
        transactions.clear();

        if(toSnapshot != null) {
            addChildrenValues(transactions, toSnapshot);
        }

        if(fromSnapshot != null) {
            addChildrenValues(transactions, fromSnapshot);
        }
        adapter.notifyDataSetChanged();
    }

    private void addChildrenValues(List<MOVTransaction> children, DataSnapshot dataSnapshot) {
        for(DataSnapshot child : dataSnapshot.getChildren()) {
            MOVTransaction transaction = child.getValue(MOVTransaction.class);
            children.add(transaction);
        }

        Collections.sort(children);
    }

    @Override
    public void onTransactionClick(int position) {
        position--;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Boolean sender = user.getUid().equals(transactions.get(position).getFrom());
        String phone_number = "";
        if(sender){
            phone_number = users.child(transactions.get(position).getTo()).getValue(MOVUser.class).getPhone();
        }
        else{
            phone_number = users.child(transactions.get(position).getFrom()).getValue(MOVUser.class).getPhone();
        }

        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users");
        String to = transactions.get(position).getTo();
        String from = transactions.get(position).getFrom();
        String user_str;
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
                    Log.d("DataSnapShot1", dataSnapshot + " " + location);
                }
                if(i == 0){
                    set_location("users/profile.jpg/");
                }
                Log.d("DataSnapShot1.5", dataSnapshot + " " + i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                set_location("users/profile.jpg/");
            }
            });
        Log.d("DataSnapShot2",  this.location);
        Intent intent = new Intent(HistoryFragment.this.getActivity(),HistoryTransactionActivity.class);
        intent.putExtra(HISTORY_FROM,transactions.get(position).getFrom());
        intent.putExtra(HISTORY_TO,transactions.get(position).getTo());
        intent.putExtra(HISTORY_TIME,transactions.get(position).getTime());
        intent.putExtra(HISTORY_AMOUNT,transactions.get(position).getAmount());
        intent.putExtra(HISTORY_CURRENCY,transactions.get(position).getCurrency());
        intent.putExtra(HISTORY_PHONE,phone_number);
        intent.putExtra(HISTORY_SENDER,sender);
        intent.putExtra(HISTORY_PORTRAIT,this.location);
        intent.putExtra(HISTORY_FLAG,this.flag);
        startActivity(intent);
    }

    private void set_location(String s){
        this.location = s;
        this.flag = true;
    }
}