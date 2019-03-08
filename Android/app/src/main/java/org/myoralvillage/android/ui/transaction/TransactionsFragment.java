package org.myoralvillage.android.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.myoralvillage.android.R;


public class TransactionsFragment extends Fragment {


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
        return view;
    }
}
