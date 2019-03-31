package org.myoralvillage.android.ui.transaction;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVRequest;
import android.util.Log;

public class TransactionsFragment extends Fragment {

    private FirebaseRecyclerAdapter<MOVRequest, MOVIncRequestViewHolder> adapterInc;
    private FirebaseRecyclerAdapter<MOVRequest, MOVOutRequestViewHolder> adapterOut;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    public static TransactionsFragment newInstance() {
        TransactionsFragment fragment = new TransactionsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //String uid = "1";
        //Log.v("Hi", uid);
        Query toQuery = FirebaseDatabase.getInstance().getReference().child("requests").orderByChild("to").equalTo(uid);
        //Log.v("Hi", uid);

        FirebaseRecyclerOptions<MOVRequest> options = new FirebaseRecyclerOptions.Builder<MOVRequest>()
                .setQuery(toQuery, new SnapshotParser<MOVRequest>() {
                    @NonNull
                    @Override
                    public MOVRequest parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return snapshot.getValue(MOVRequest.class);
                    }
                }).build();

        adapterInc = new FirebaseRecyclerAdapter<MOVRequest, MOVIncRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MOVIncRequestViewHolder movIncRequestViewHolder, int i, @NonNull MOVRequest movRequest) {
                //Log.v("Bye", movRequest.getCurrency());
                movIncRequestViewHolder.sender.setText(movRequest.getFrom()); // View Holder Functions
                movIncRequestViewHolder.amount.setText(Integer.toString(movRequest.getAmount()));
                movIncRequestViewHolder.currency.setText(movRequest.getCurrency());
            }

            @NonNull
            @Override
            public MOVIncRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_incoming_request, parent, false);

                return new MOVIncRequestViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapterInc);
        adapterInc.startListening();

    }

    class MOVIncRequestViewHolder extends RecyclerView.ViewHolder {
        TextView sender;
        TextView amount;
        TextView currency;

        MOVIncRequestViewHolder(View itemView) {
            super(itemView);

            sender = itemView.findViewById(R.id.incoming_request_sender);
            amount = itemView.findViewById(R.id.incoming_request_amount);
            currency = itemView.findViewById(R.id.incoming_request_currency);
        }
    }

    private void setUpOutReqRecyclerView(RecyclerView recyclerView) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query fromQuery = FirebaseDatabase.getInstance().getReference().child("requests").orderByChild("from").equalTo(uid);

        FirebaseRecyclerOptions<MOVRequest> options = new FirebaseRecyclerOptions.Builder<MOVRequest>()
                .setQuery(fromQuery, new SnapshotParser<MOVRequest>() {
                    @NonNull
                    @Override
                    public MOVRequest parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return snapshot.getValue(MOVRequest.class);
                    }
                }).build();

        adapterOut = new FirebaseRecyclerAdapter<MOVRequest, MOVOutRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MOVOutRequestViewHolder movOutRequestViewHolder, int i, @NonNull MOVRequest movRequest) {
                // Use DataSnapshot to search for name?
                movOutRequestViewHolder.recipient.setText(movRequest.getTo()); // View Holder Functions
                movOutRequestViewHolder.amount.setText(Integer.toString(movRequest.getAmount()));
                movOutRequestViewHolder.currency.setText(movRequest.getCurrency());
            }

            @NonNull
            @Override
            public MOVOutRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_outgoing_request, parent, false);

                return new MOVOutRequestViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapterOut);
        adapterOut.startListening();

    }

    class MOVOutRequestViewHolder extends RecyclerView.ViewHolder {
        TextView recipient;
        TextView amount;
        TextView currency;

        MOVOutRequestViewHolder(View itemView) {
            super(itemView);

            recipient = itemView.findViewById(R.id.outgoing_request_recipient);
            amount = itemView.findViewById(R.id.outgoing_request_amount);
            currency = itemView.findViewById(R.id.outgoing_request_currency);
        }
    }

}
