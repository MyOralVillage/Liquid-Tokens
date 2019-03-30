package org.myoralvillage.android.ui.transaction;

import org.myoralvillage.android.ui.transaction.amountselection.TransactionAmountSelectionFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TransactionPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments;

    /**
     * Pager adapter for a transaction flow
     *
     * @param manager The fragment manager
     * @param transactionType The type of the transaction.
     *  Either  {@link TransactionActivity#TRANSACTION_TYPE_REQUEST}
     *          {@link TransactionActivity#TRANSACTION_TYPE_SEND}
     * @param isoCurrencyCode The ISO currency code of the transaction
     * @param transactionSendTo The user ID of the transaction recipient
     */
    public TransactionPagerAdapter(FragmentManager manager,
                                   int transactionType,
                                   String isoCurrencyCode,
                                   @Nullable String transactionSendTo) {
        super(manager);

        fragments = new ArrayList<>();
        if(transactionSendTo == null) {
            fragments.add(new TransactionSelectContactFragment());
        }
        fragments.add(TransactionAmountSelectionFragment.newInstance(isoCurrencyCode));
        fragments.add(TransactionConfirmFragment.newInstance(isoCurrencyCode, transactionType));
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
