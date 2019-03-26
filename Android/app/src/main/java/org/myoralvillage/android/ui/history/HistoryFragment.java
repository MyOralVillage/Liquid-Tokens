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
import org.myoralvillage.android.ui.util.GlideApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnTransactionListener {

    private static final String LOG_TAG = "HistoryFragment";
    public static final String HISTORY_FROM = "org.myoralvillage.android.ui.history.from";
    public static final String HISTORY_TO = "org.myoralvillage.android.ui.history.to";
    public static final String HISTORY_TIME = "org.myoralvillage.android.ui.history.time";
    public static final String HISTORY_AMOUNT = "org.myoralvillage.android.ui.history.amount";
    public static final String HISTORY_CURRENCY = "org.myoralvillage.android.ui.history.currency";

    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    private DataSnapshot toSnapshot;
    private DataSnapshot fromSnapshot;

    private Query fromQuery;
    private Query toQuery;

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

        adapter = new HistoryAdapter(transactions, defaultHeightPx,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        toQuery.addValueEventListener(toListener);
        fromQuery.addValueEventListener(fromListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        toQuery.removeEventListener(toListener);
        fromQuery.removeEventListener(fromListener);
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
        Log.d("History Fragment","Clicked");
        Intent intent = new Intent(HistoryFragment.this.getActivity(),HistoryTransactionActivity.class);
        intent.putExtra(HISTORY_FROM,transactions.get(position).getFrom());
        intent.putExtra(HISTORY_TO,transactions.get(position).getTo());
        intent.putExtra(HISTORY_TIME,transactions.get(position).getTime());
        intent.putExtra(HISTORY_AMOUNT,transactions.get(position).getAmount());
        intent.putExtra(HISTORY_CURRENCY,transactions.get(position).getCurrency());
        startActivity(intent);
    }
}