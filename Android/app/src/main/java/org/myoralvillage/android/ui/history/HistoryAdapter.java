package org.myoralvillage.android.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.currency.MOVCurrencyCache;
import org.myoralvillage.android.data.transaction.MOVTransaction;
import org.myoralvillage.android.ui.CurrentUserViewModel;
import org.myoralvillage.android.ui.transaction.amountselection.TransactionAmountSelectionViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_TO = 0;
    public static final int VIEW_TYPE_FROM = 1;

    private static final int VIEW_TYPE_HEADER = 2;
    private static final int VIEW_TYPE_FOOTER = 3;

    private int defaultCellHeight;

    private List<MOVTransaction> transactions;

    private MOVCurrencyCache currencyCache;
    private CurrentUserViewModel userViewModel;

    public HistoryAdapter(CurrentUserViewModel userViewModel, List<MOVTransaction> transactions, int defaultCellHeight) {
        this.userViewModel = userViewModel;
        this.transactions = transactions;
        this.defaultCellHeight = defaultCellHeight;

        currencyCache = new MOVCurrencyCache();


    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_history_header, parent, false);


            return new HistoryHeaderViewHolder(view, currencyCache);
        }else if(viewType == VIEW_TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_history_footer, parent, false);

            return new RecyclerView.ViewHolder(view){};
        }else if(viewType == VIEW_TYPE_TO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_history_to, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_history_from, parent, false);
        }

        return new HistoryViewHolder(view, currencyCache);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == VIEW_TYPE_HEADER) {
            HistoryHeaderViewHolder headerViewHolder = (HistoryHeaderViewHolder) holder;
            headerViewHolder.setViewModel(userViewModel);
        }else if(holder.getItemViewType() == VIEW_TYPE_FROM || holder.getItemViewType() == VIEW_TYPE_TO) {
            int transactionPosition = position - 1;

            MOVTransaction transaction = transactions.get(transactionPosition);
            HistoryViewHolder historyViewHolder = (HistoryViewHolder) holder;
            historyViewHolder.setTransaction(transaction);

            if(transactionPosition > 0) {
                MOVTransaction nextTransaction = transactions.get(transactionPosition - 1);

                long timeDiff = Math.abs(nextTransaction.getTime() - transaction.getTime());
                timeDiff /= 1000;   // Convert to seconds
                timeDiff /= 60;     // Convert to minutes

                int minutesInTwoMonths = 24 * 60 * 60;

                double factor = (double) timeDiff / (double) minutesInTwoMonths;

                // Cell height ranges from 1 to n (defaultCellHeight * n - defaultCellHeight)
                // times its height depending on how big the transaction date difference is.
                int height = (int) (defaultCellHeight + (defaultCellHeight * 7 - defaultCellHeight) * factor);

                historyViewHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return VIEW_TYPE_HEADER;
        } else if(position == transactions.size() + 1) {
            return VIEW_TYPE_FOOTER;
        }

        MOVTransaction transaction = transactions.get(position - 1);
        String uid = FirebaseAuth.getInstance().getUid();

        if(uid != null && uid.equals(transaction.getTo())) {
            return VIEW_TYPE_TO;
        } else {
            return VIEW_TYPE_FROM;
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size() + 2;
    }
}
