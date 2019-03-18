package org.myoralvillage.android.ui.contacts;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.myoralvillage.android.data.model.MOVUser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ContactsListViewModel extends ViewModel {

    private MutableLiveData<List<MOVUser>> contacts = new MutableLiveData<>();

    public ContactsListViewModel() {
        contacts.setValue(new ArrayList<MOVUser>());

    }

    public void addContact(@NonNull String uid) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MOVUser user = dataSnapshot.getValue(MOVUser.class);

                        if(user != null) {
                            addContact(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void removeContact(@NonNull String uid) {
        List<MOVUser> contacts = this.contacts.getValue();
        for(int i = 0; i < contacts.size(); i++) {
            MOVUser contact = contacts.get(i);

            if(contact.getUid().equals(uid)) {
                contacts.remove(i);
                this.contacts.setValue(contacts);
                return;
            }
        }
    }

    public void clearContacts() {
        contacts.setValue(new ArrayList<MOVUser>());
    }

    private void addContact(MOVUser user) {
        List<MOVUser> existingUsers = contacts.getValue();

        for(MOVUser existingUser : existingUsers) {
            if(existingUser.getUid().equals(user.getUid())) {
                throw new IllegalStateException("User appeared twice");
            }
        }

        existingUsers.add(user);

        contacts.setValue(existingUsers);
    }

    public LiveData<List<MOVUser>> getContacts() {
        return contacts;
    }
}
