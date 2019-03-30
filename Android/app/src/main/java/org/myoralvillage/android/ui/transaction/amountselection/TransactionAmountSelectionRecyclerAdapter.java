package org.myoralvillage.android.ui.transaction.amountselection;

import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionAmountSelectionRecyclerAdapter extends RecyclerView.Adapter<TransactionAmountSelectionViewHolder>{

    private static final int VIEW_TYPE_HORIZONTAL = 0;
    public static final int VIEW_TYPE_VERTICAL = 1;

    private final Map<MOVCurrencyDenomination, Integer> denominationAmountsMap;
    private final List<MOVCurrencyDenomination> selectedDenominations;

    private final TransactionAmountSelectionCurrencyRemover currencyRemover;
    private final MOVCurrency currency;

    /**
     *
     * @param currencyRemover Handle dragging a denomination out of the frame.
     *                        If null, dragging is disabled.
     */
    public TransactionAmountSelectionRecyclerAdapter(@NonNull MOVCurrency currency, @Nullable TransactionAmountSelectionCurrencyRemover currencyRemover) {
        this.currencyRemover = currencyRemover;
        this.currency = currency;

        selectedDenominations = new ArrayList<>();
        denominationAmountsMap = new HashMap<>();
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
        if(currencyRemover != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            holder.getDragPayloadImage());
                    DenominationDragPayload payload = new DenominationDragPayload(denomination, true);
                    holder.getDragPayloadImage().startDrag(data, shadowBuilder, payload, 0);

                    currencyRemover.removeCurrency(denomination);

                    return true;
                }
            });
        }
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

    public void setAmount(int amount) {
        List<MOVCurrencyDenomination> sortedDenominations = new ArrayList<>(currency.getDenominations());
        Collections.sort(sortedDenominations);

        denominationAmountsMap.clear();


        int index = 0;
        while(amount > 0) {
            MOVCurrencyDenomination denomination = sortedDenominations.get(index);
            int denominationAmount = denomination.getValue();

            while(amount - denominationAmount >= 0) {
                addDenomination(denominationAmountsMap, denomination);

                amount -= denominationAmount;
            }

            index += 1;
        }

        updateSelectedDenominationsFromMap();

        notifyDataSetChanged();
    }

    private void updateSelectedDenominationsFromMap() {
        selectedDenominations.clear();

        for(Map.Entry<MOVCurrencyDenomination, Integer> denomination : denominationAmountsMap.entrySet()) {
            if(denomination.getValue() > 0) {
                selectedDenominations.add(denomination.getKey());
            }
        }

        Collections.sort(selectedDenominations);

        notifyDataSetChanged();
    }

    private void addDenomination(Map<MOVCurrencyDenomination, Integer> denominationAmounts, MOVCurrencyDenomination denomination) {
        Integer amount = denominationAmounts.get(denomination);
        if(amount == null) {
            amount = 0;
        }

        amount += 1;

        denominationAmounts.put(denomination, amount);
    }
}
