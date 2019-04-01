package org.myoralvillage.android.ui.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrencyCache;
import org.myoralvillage.android.data.model.MOVRequest;
import org.myoralvillage.android.ui.CurrentUserViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OutgoingRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final OnTransactionListener mOnTransactionListener;

    private final List<MOVRequest> requests;

    private final MOVCurrencyCache currencyCache;
    private final CurrentUserViewModel userViewModel;

    public OutgoingRequestAdapter(CurrentUserViewModel userViewModel, List<MOVRequest> requests, OnTransactionListener onTransactionListener) {
        this.userViewModel = userViewModel;
        this.requests = requests;
        this.mOnTransactionListener = onTransactionListener;

        this.currencyCache = new MOVCurrencyCache();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_outgoing_request, parent, false);

        return new OutgoingRequestViewHolder(view, currencyCache, mOnTransactionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OutgoingRequestViewHolder outgoingRequestViewHolder = (OutgoingRequestViewHolder) holder;
        int requestPosition = position;

        MOVRequest request = requests.get(requestPosition);
        outgoingRequestViewHolder.setRequest(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
