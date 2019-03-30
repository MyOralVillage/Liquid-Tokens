package org.myoralvillage.android.ui.transaction.amountselection;

import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;

class DenominationDragPayload {
    private final MOVCurrencyDenomination denomination;
    private final boolean selectedAmount;

    public DenominationDragPayload(MOVCurrencyDenomination denomination, boolean selectedAmount) {
        this.denomination = denomination;
        this.selectedAmount = selectedAmount;
    }

    public MOVCurrencyDenomination getDenomination() {
        return denomination;
    }

    public boolean isSelectedAmount() {
        return selectedAmount;
    }
}
