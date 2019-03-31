package org.myoralvillage.android.ui.contacts;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.ClassSnapshotParser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.widgets.ContactCard;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsActivity extends AppCompatActivity implements ChildEventListener, SearchView.OnQueryTextListener, ContactsListAdapter.OnContactSelectedListener {

    public static String EXTRA_SELECTION_MODE = "SelectionMode";
    public static int SELECTION_MODE_VIEW = 0;
    public static int SELECTION_MODE_PICK = 1;

    public static int RESULT_DID_PICK = 0;
    public static String RESULT_SELECTED_CONTACT = "SelectedContact";

    private Query contactsQuery;

    private ListView listView;
    private View emptyView;
    private ContactsListViewModel contactListViewModel;
    private ContactsListAdapter contactListAdapter;

    private FloatingActionButton addContactButton;

    private MenuItem searchItem;

    private int selectionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        selectionMode = getIntent().getIntExtra(EXTRA_SELECTION_MODE, SELECTION_MODE_VIEW);

        addContactButton = findViewById(R.id.contacts_fab);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddContactButton();
            }
        });
        listView = findViewById(R.id.contacts_list);
        emptyView = findViewById(R.id.contacts_text_empty);

        contactListViewModel = ViewModelProviders.of(this).get(ContactsListViewModel.class);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        contactsQuery = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUser.getUid())
                .child("contacts");
        setUpListView(listView);

    }

    private void onClickAddContactButton() {
        Intent intent = new Intent(this, AddContactActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.v("ContactsActivity", "AddListener");
        contactListViewModel.clearContacts();
        contactsQuery.addChildEventListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.v("ContactsActivity", "RemoveListener");


        contactsQuery.removeEventListener(this);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Boolean added = dataSnapshot.getValue(Boolean.class);
        if(added == null || added) {
            Log.v("ContactsActivity", "onChildAdded: "+dataSnapshot.getKey());
            contactListViewModel.addContact(dataSnapshot.getKey());
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Boolean added = dataSnapshot.getValue(Boolean.class);
        if(added == null || !added) {
            Log.v("ContactsActivity", "onChildChanged: "+dataSnapshot.getKey());

            contactListViewModel.removeContact(dataSnapshot.getKey());
        }
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Log.v("ContactsActivity", "onChildRemoved: "+dataSnapshot.getKey());

        contactListViewModel.removeContact(dataSnapshot.getKey());
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    private void setUpListView(final ListView listView) {
        contactListAdapter = new ContactsListAdapter(this);
        contactListAdapter.setContactSelectedListener(this);
        listView.setAdapter(contactListAdapter);

        final ContactsListViewModel contactList = ViewModelProviders.of(this).get(ContactsListViewModel.class);

        contactList.getContacts().observe(this, new Observer<List<MOVUser>>() {
            @Override
            public void onChanged(List<MOVUser> movUsers) {
                if(movUsers == null || movUsers.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }

                contactListAdapter.setContacts(movUsers);
            }
        });
    }

    @Override
    public void onContactSelected(MOVUser user) {
        if(selectionMode == SELECTION_MODE_VIEW) {
            Intent intent = new Intent(this, ContactActivity.class);
            intent.putExtra(ContactActivity.EXTRA_CONTACT_UID, user.getUid());

            startActivity(intent);
        } else {
            Intent data = new Intent();
            data.putExtra(RESULT_SELECTED_CONTACT, user.getUid());
            setResult(RESULT_DID_PICK, data);

            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contacts, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.search);

        Drawable icon = (Drawable)searchItem.getIcon();
        icon.mutate().setColorFilter(Color.argb(255, 255, 255, 255), PorterDuff.Mode.SRC_IN);
        searchItem.setIcon(icon);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contactListAdapter.getFilter().filter(newText);
        return true;
    }
}
