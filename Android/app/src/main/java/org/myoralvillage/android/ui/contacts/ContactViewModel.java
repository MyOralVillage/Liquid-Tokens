package org.myoralvillage.android.ui.contacts;

import org.myoralvillage.android.data.model.MOVUser;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ContactViewModel extends ViewModel {

    private final MutableLiveData<MOVUser> contact = new MutableLiveData<>();

    public MutableLiveData<MOVUser> getContact() {
        return contact;
    }

    public void setContact(MOVUser contact) {
        this.contact.setValue(contact);
    }
}
