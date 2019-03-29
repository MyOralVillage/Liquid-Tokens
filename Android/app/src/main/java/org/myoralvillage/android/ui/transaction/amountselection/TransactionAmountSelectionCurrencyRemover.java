package org.myoralvillage.android.ui.transaction.amountselection;

import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;

public interface TransactionAmountSelectionCurrencyRemover {
    void removeCurrency(MOVCurrencyDenomination denomination);
}
