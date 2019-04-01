package org.myoralvillage.android.ui.transaction;

import org.myoralvillage.android.data.model.MOVUser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TransactionViewModel extends ViewModel {

    private final MutableLiveData<MOVUser> selectedContact = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedAmount = new MutableLiveData<>();

    public TransactionViewModel() {
        selectedAmount.setValue(0);
        selectedContact.setValue(null);
    }
    public void setSelectedContact(MOVUser user) {
        selectedContact.setValue(user);
    }

    public LiveData<MOVUser> getSelectedContact() {
        return selectedContact;
    }

    public LiveData<Integer> getSelectedAmount() {
        return selectedAmount;
    }

    public void setSelectedAmount(Integer amount) {
        selectedAmount.setValue(amount);
    }
}
