package org.myoralvillage.android.ui;

import org.myoralvillage.android.data.model.MOVUser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CurrentUserViewModel extends ViewModel {

    private MutableLiveData<MOVUser> currentUser;

    public CurrentUserViewModel() {
        currentUser = new MutableLiveData<>();
    }

    public void setCurrentUser(MOVUser user) {
        this.currentUser.setValue(user);
    }

    public LiveData<MOVUser> getCurrentUser() {
        return currentUser;
    }
}
