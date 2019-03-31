package org.myoralvillage.android.ui.history;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.currency.MOVCurrencyCache;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.CurrentUserViewModel;
import org.myoralvillage.android.ui.transaction.amountselection.TransactionAmountSelectionRecyclerAdapter;
import org.myoralvillage.android.ui.widgets.ContactCard;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryHeaderViewHolder extends RecyclerView.ViewHolder implements Observer<MOVUser>{

    private CurrentUserViewModel viewModel;
    private RecyclerView balanceRecycler;
    private TextView balanceText;
    private ImageView userImage;

    private TransactionAmountSelectionRecyclerAdapter balanceAdapter;
    private MOVCurrencyCache currencyCache;

    public HistoryHeaderViewHolder(@NonNull View itemView, MOVCurrencyCache currencyCache) {
        super(itemView);
        this.currencyCache = currencyCache;

        LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);

        balanceRecycler = itemView.findViewById(R.id.transaction_history_balance_recycler);
        balanceText = itemView.findViewById(R.id.transaction_history_balance);
        userImage = itemView.findViewById(R.id.transaction_history_image_user);

        balanceRecycler.setLayoutManager(layoutManager);
    }


    public void setViewModel(CurrentUserViewModel viewModel) {
        if(this.viewModel != null && this.viewModel != viewModel) {
            this.viewModel.getCurrentUser().removeObserver(this);
        }
        this.viewModel = viewModel;
        this.viewModel.getCurrentUser().observe((FragmentActivity) itemView.getContext(), this);
    }

    @Override
    public void onChanged(MOVUser movUser) {
        MOVCurrency currency = currencyCache.getCurrency(itemView.getContext(), movUser.getCurrency().toLowerCase());
        balanceAdapter = new TransactionAmountSelectionRecyclerAdapter(currency, null);
        balanceAdapter.setAmount(movUser.getBalance());
        balanceText.setText(currency.getFormattedString(movUser.getBalance()));
        balanceRecycler.setAdapter(balanceAdapter);

        ContactCard.setUserImage(itemView.getContext(), movUser, userImage);
    }
}
