package org.myoralvillage.android.ui.transaction.amountselection;

import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;

interface TransactionAmountSelectionCurrencyRemover {
    void removeCurrency(MOVCurrencyDenomination denomination);
}
