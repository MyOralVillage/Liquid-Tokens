package org.myoralvillage.android.ui.transaction;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public interface TransactionPage {

    void onPageMadeActive();

    @StringRes
    int getForwardButtonText();

    @DrawableRes
    int getForwardButtonIcon();

    void onNextButtonPressed();
}
