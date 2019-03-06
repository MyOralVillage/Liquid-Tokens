package org.myoralvillage.android.ui.transaction.amountselection;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;

import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;

import java.util.ArrayList;
import java.util.List;

public class TransactionAmountSelectionViewModel extends ViewModel {

    private MutableLiveData<List<MOVCurrencyDenomination>> selectedCurrency = new MutableLiveData<>();
    ;
    private MutableLiveData<Boolean> adding = new MutableLiveData<>();
    private MutableLiveData<Boolean> removing = new MutableLiveData<>();

    private MutableLiveData<Integer> selectedCurrencyValue = new MutableLiveData<>();

    public TransactionAmountSelectionViewModel() {
        selectedCurrency.observeForever(new Observer<List<MOVCurrencyDenomination>>() {
            @Override
            public void onChanged(@Nullable List<MOVCurrencyDenomination> movCurrencyDenominations) {
                if(movCurrencyDenominations == null) {
                    selectedCurrencyValue.setValue(0);
                } else {
                    int amount = 0;
                    for(MOVCurrencyDenomination denomination : movCurrencyDenominations) {
                        amount += denomination.getValue();
                    }

                    selectedCurrencyValue.setValue(amount);
                }
            }
        });
        selectedCurrency.setValue(new ArrayList<MOVCurrencyDenomination>());
    }

    public LiveData<List<MOVCurrencyDenomination>> getSelectedCurrency() {
        return selectedCurrency;
    }

    public void addCurrency(MOVCurrencyDenomination denomination) {
        List<MOVCurrencyDenomination> denominations = selectedCurrency.getValue();
        if(denominations == null) {
            denominations = new ArrayList<>();
        }
        denominations.add(denomination);

        selectedCurrency.setValue(denominations);
    }

    public void removeCurrency(int index) {
        List<MOVCurrencyDenomination> denominations = selectedCurrency.getValue();
        if(denominations != null) {
            denominations.remove(index);

            selectedCurrency.setValue(denominations);
        }
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
