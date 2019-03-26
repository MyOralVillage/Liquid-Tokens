package org.myoralvillage.android.ui.transaction.amountselection;

import android.content.ClipData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.transaction.TransactionViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionAmountSelectionRecyclerAdapter extends RecyclerView.Adapter<TransactionAmountSelectionViewHolder>{

    public static final int VIEW_TYPE_HORIZONTAL = 0;
    public static final int VIEW_TYPE_VERTICAL = 1;

    private Map<MOVCurrencyDenomination, Integer> denominationAmountsMap;
    private List<MOVCurrencyDenomination> selectedDenominations;

    private TransactionAmountSelectionViewModel viewModel;

    public TransactionAmountSelectionRecyclerAdapter(TransactionAmountSelectionViewModel viewModel, MOVCurrency currency) {
        this.viewModel = viewModel;

        selectedDenominations = new ArrayList<>();
    }

    @NonNull
    @Override
    public TransactionAmountSelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        @LayoutRes int layoutRes;

        if(viewType == VIEW_TYPE_HORIZONTAL) {
            layoutRes = R.layout.cell_denomination_tally_horizontal;
        } else {
            layoutRes = R.layout.cell_denomination_tally_vertical;
        }
        View view = inflater.inflate(layoutRes, parent, false);

        return new TransactionAmountSelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TransactionAmountSelectionViewHolder holder, final int position) {
        final MOVCurrencyDenomination denomination = selectedDenominations.get(position);
        Integer selectedAmount = denominationAmountsMap.get(denomination);

        if(selectedAmount == null) {
            throw new IllegalStateException("Amount selection mismatch (Have denomination, amount is null)");
        }

        holder.setSelectedDenomination(denomination, selectedAmount, getItemViewType(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        holder.getDragPayloadImage());
                DenominationDragPayload payload = new DenominationDragPayload(denomination, true);
                holder.getDragPayloadImage().startDrag(data, shadowBuilder, payload, 0);

                viewModel.removeCurrency(denomination);

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedDenominations.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return VIEW_TYPE_VERTICAL;
        }
        return VIEW_TYPE_HORIZONTAL;
    }

    public void setSelectedAmount(Map<MOVCurrencyDenomination, Integer> denominationAmounts) {
        this.denominationAmountsMap = denominationAmounts;
        selectedDenominations.clear();

        for(Map.Entry<MOVCurrencyDenomination, Integer> denomination : denominationAmounts.entrySet()) {
            if(denomination.getValue() > 0) {
                selectedDenominations.add(denomination.getKey());
            }
        }

        Collections.sort(selectedDenominations);

        notifyDataSetChanged();
    }
}
