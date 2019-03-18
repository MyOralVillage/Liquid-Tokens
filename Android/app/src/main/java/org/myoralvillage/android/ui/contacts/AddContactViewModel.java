package org.myoralvillage.android.ui.contacts;

import org.myoralvillage.android.data.model.MOVUser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddContactViewModel extends ViewModel {

    public enum AddContactState {
        Loading, Error, Found, Blank
    }

    private MutableLiveData<MOVUser> contactToAdd = new MutableLiveData<>();

    private MutableLiveData<AddContactState> contactState = new MutableLiveData<>();

    private String errorText;

    public AddContactViewModel() {
        contactState.setValue(AddContactState.Blank);
    }

    public LiveData<MOVUser> getContactToAdd() {
        return contactToAdd;
    }

    public void setContactToAdd(MOVUser contactToAdd) {
        this.contactToAdd.setValue(contactToAdd);
    }

    public LiveData<AddContactState> getState() {
        return this.contactState;
    }

    public void setState(AddContactState state) {
        this.contactState.setValue(state);
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
}
