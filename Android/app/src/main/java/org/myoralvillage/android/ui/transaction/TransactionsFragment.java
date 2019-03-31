package org.myoralvillage.android.ui.transaction;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVRequest;
import org.myoralvillage.android.ui.CurrentUserViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionsFragment extends Fragment implements OnTransactionListener{

    private RecyclerView.Adapter<RecyclerView.ViewHolder> incAdapter;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> outAdapter;

    private DataSnapshot toSnapshot;
    private DataSnapshot fromSnapshot;
    private DataSnapshot users;

    private Query usersQuery;
    private Query fromQuery;
    private Query toQuery;

    private List<MOVRequest> incRequests;
    private List<MOVRequest> outRequests;

    private final ValueEventListener toListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            toSnapshot = dataSnapshot;
            onUpdateSnapshot();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private final ValueEventListener fromListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            fromSnapshot = dataSnapshot;
            onUpdateSnapshot();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private final ValueEventListener usersListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            users = dataSnapshot;
            onUpdateSnapshot();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public TransactionsFragment() {
        // Required empty public constructor
    }

    public static TransactionsFragment newInstance() {

        return new TransactionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        usersQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("uid");
        toQuery = FirebaseDatabase.getInstance().getReference().child("requests").orderByChild("to").equalTo(user.getUid());
        fromQuery = FirebaseDatabase.getInstance().getReference().child("requests").orderByChild("from").equalTo(user.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        FrameLayout sendView = view.findViewById(R.id.transactions_send_money);
        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TransactionActivity.class);
                intent.putExtra(TransactionActivity.EXTRA_TRANSACTION_TYPE, TransactionActivity.TRANSACTION_TYPE_SEND);

                startActivity(intent);
            }
        });
        FrameLayout requestView = view.findViewById(R.id.transactions_request_money);
        requestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TransactionActivity.class);
                intent.putExtra(TransactionActivity.EXTRA_TRANSACTION_TYPE, TransactionActivity.TRANSACTION_TYPE_REQUEST);

                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        RecyclerView incReqRecycler = view.findViewById(R.id.list_of_incoming_money_requests);
        incReqRecycler.setLayoutManager(layoutManager1);
        setUpIncReqRecyclerView(incReqRecycler);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        RecyclerView outReqRecycler = view.findViewById(R.id.list_of_outgoing_money_requests);
        outReqRecycler.setLayoutManager(layoutManager2);
        setUpOutReqRecyclerView(outReqRecycler);

        return view;
    }

    private void setUpIncReqRecyclerView(RecyclerView recyclerView) {
        incRequests = new ArrayList<>();

        if(getActivity() == null) {
            throw new IllegalStateException("Something went error. activity null");
        }
        CurrentUserViewModel userViewModel = ViewModelProviders.of(getActivity()).get(CurrentUserViewModel.class);

        incAdapter = new IncomingRequestAdapter(userViewModel, incRequests, this);
        recyclerView.setAdapter(incAdapter);
    }

    private void setUpOutReqRecyclerView(RecyclerView recyclerView) {
        outRequests = new ArrayList<>();

        if(getActivity() == null) {
            throw new IllegalStateException("Something went error. activity null");
        }
        CurrentUserViewModel userViewModel = ViewModelProviders.of(getActivity()).get(CurrentUserViewModel.class);

        outAdapter = new OutgoingRequestAdapter(userViewModel, outRequests, this);
        recyclerView.setAdapter(outAdapter);
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
        incRequests.clear();
        outRequests.clear();

        if(toSnapshot != null) {
            addChildrenValues(incRequests, toSnapshot);
        }

        if(fromSnapshot != null) {
            addChildrenValues(outRequests, fromSnapshot);
        }

        incAdapter.notifyDataSetChanged();
        outAdapter.notifyDataSetChanged();
    }

    private void addChildrenValues(List<MOVRequest> children, DataSnapshot dataSnapshot) {
        for(DataSnapshot child : dataSnapshot.getChildren()) {
            MOVRequest request = child.getValue(MOVRequest.class);
            children.add(request);
        }

        // Need to make MOVRequest comparable to sort by date
    }

    @Override
    public void onTransactionClick(int position) {

    }
}
