package org.myoralvillage.android.ui.transaction.amountselection;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;

import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionAmountSelectionViewModel extends ViewModel {

    private MutableLiveData<Boolean> adding = new MutableLiveData<>();
    private MutableLiveData<Boolean> removing = new MutableLiveData<>();

    private MutableLiveData<Integer> selectedCurrencyValue = new MutableLiveData<>();

    public TransactionAmountSelectionViewModel() {
    }



    public void addCurrency(MOVCurrencyDenomination toAdd) {
        Integer amount = selectedCurrencyValue.getValue();
        if(amount == null) {
            amount = 0;
        }
        amount += toAdd.getValue();

        selectedCurrencyValue.setValue(amount);
    }

    public void removeCurrency(MOVCurrencyDenomination amount) {
        Integer currentAmount = selectedCurrencyValue.getValue();
        if(currentAmount == null) {
            currentAmount = 0;
        }

        selectedCurrencyValue.setValue(currentAmount - amount.getValue());
    }

    public LiveData<Integer> getValue() {
        return selectedCurrencyValue;
    }

    public LiveData<Boolean> isAdding() {
        return adding;
    }

    public void setAdding(boolean adding) {
        this.adding.setValue(adding);
    }

    public LiveData<Boolean> isRemoving() {
        return removing;
    }

    public void setRemoving(boolean removing) {
        this.removing.setValue(removing);
    }
}
