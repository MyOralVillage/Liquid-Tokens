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

    private MutableLiveData<Map<MOVCurrencyDenomination, Integer>> selectedCurrency = new MutableLiveData<>();
    ;
    private MutableLiveData<Boolean> adding = new MutableLiveData<>();
    private MutableLiveData<Boolean> removing = new MutableLiveData<>();

    private MutableLiveData<Integer> selectedCurrencyValue = new MutableLiveData<>();

    private MOVCurrency currency;

    public TransactionAmountSelectionViewModel() {
        selectedCurrency.observeForever(new Observer<Map<MOVCurrencyDenomination, Integer>>() {
            @Override
            public void onChanged(@Nullable Map<MOVCurrencyDenomination, Integer> movCurrencyDenominations) {
                if(movCurrencyDenominations == null) {
                    selectedCurrencyValue.setValue(0);
                } else {
                    int amount = 0;
                    for(Map.Entry<MOVCurrencyDenomination, Integer> entry : movCurrencyDenominations.entrySet()) {
                        amount += entry.getKey().getValue() * entry.getValue();
                    }

                    selectedCurrencyValue.setValue(amount);
                }
            }
        });
        selectedCurrency.setValue(new HashMap<MOVCurrencyDenomination, Integer>());
    }

    public LiveData<Map<MOVCurrencyDenomination, Integer>> getSelectedCurrency() {
        return selectedCurrency;
    }

    public void addCurrency(MOVCurrencyDenomination toAdd) {
        Integer amount = selectedCurrencyValue.getValue();
        if(amount == null) {
            amount = 0;
        }
        amount += toAdd.getValue();

        updateForValue(amount);
    }

    private void updateForValue(int amount) {
        List<MOVCurrencyDenomination> sortedDenominations = new ArrayList<>(currency.getDenominations());
        Collections.sort(sortedDenominations);

        Map<MOVCurrencyDenomination, Integer> map = selectedCurrency.getValue();
        map.clear();


        int index = 0;
        while(amount > 0) {
            MOVCurrencyDenomination denomination = sortedDenominations.get(index);
            int denominationAmount = denomination.getValue();

            while(amount - denominationAmount >= 0) {
                addDenomination(map, denomination);

                amount -= denominationAmount;
            }

            index += 1;
        }

        selectedCurrency.setValue(map);
    }

    private void addDenomination(Map<MOVCurrencyDenomination, Integer> denominationAmounts, MOVCurrencyDenomination denomination) {
        Integer amount = denominationAmounts.get(denomination);
        if(amount == null) {
            amount = 0;
        }

        amount += 1;

        denominationAmounts.put(denomination, amount);
    }

    public void removeCurrency(MOVCurrencyDenomination amount) {
        Integer currentAmount = selectedCurrencyValue.getValue();
        if(currentAmount == null) {
            currentAmount = 0;
        }

        updateForValue(currentAmount - amount.getValue());
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

    public void setCurrency(MOVCurrency currency) {
        this.currency = currency;
    }
}
