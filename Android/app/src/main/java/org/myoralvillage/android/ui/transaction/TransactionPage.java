package org.myoralvillage.android.ui.transaction;

import com.google.android.material.button.MaterialButton;

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
