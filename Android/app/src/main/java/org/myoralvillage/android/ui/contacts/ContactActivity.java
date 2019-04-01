package org.myoralvillage.android.ui.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.transaction.TransactionActivity;
import org.myoralvillage.android.ui.widgets.ContactCard;

import java.util.HashMap;
import java.util.Map;

public class ContactActivity extends AppCompatActivity implements ValueEventListener {

    public static final String EXTRA_CONTACT_UID = "ContactUid";

    private String contactUid;

    private ViewSwitcher viewSwitcher;
    private CircleImageView contactImage;
    private TextView contactName;
    private TextView contactPhone;

    private DatabaseReference userReference;

    private MaterialCardView sendButton;
    private MaterialCardView requestButton;

    private MaterialButton removeContactButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        contactUid = getIntent().getStringExtra(EXTRA_CONTACT_UID);
        if(contactUid == null) {
            throw new IllegalStateException("EXTRA_CONTACT_UID must be set.");
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setActionBarTitle("");

        bindViews();
        addListeners();
        observeData();
    }

    private void bindViews() {
        viewSwitcher = findViewById(R.id.contact_switcher);
        contactImage = findViewById(R.id.contact_image);
        contactName = findViewById(R.id.contact_text_name);
        contactPhone = findViewById(R.id.contact_text_phone);
        sendButton = findViewById(R.id.contact_button_send);
        requestButton = findViewById(R.id.contact_button_request);
        removeContactButton = findViewById(R.id.contact_button_remove);
    }

    private void addListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, TransactionActivity.class);
                intent.putExtra(TransactionActivity.EXTRA_TRANSACTION_SEND_TO, contactUid);
                intent.putExtra(TransactionActivity.EXTRA_TRANSACTION_TYPE, TransactionActivity.TRANSACTION_TYPE_SEND);

                startActivity(intent);
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, TransactionActivity.class);
                intent.putExtra(TransactionActivity.EXTRA_TRANSACTION_SEND_TO, contactUid);
                intent.putExtra(TransactionActivity.EXTRA_TRANSACTION_TYPE, TransactionActivity.TRANSACTION_TYPE_REQUEST);

                startActivity(intent);
            }
        });

        removeContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromContacts();
            }
        });
    }

    private void removeFromContacts() {
        String uid = FirebaseAuth.getInstance().getUid();

        Map<String, Object> contacts = new HashMap<>();
        contacts.put(contactUid, false);
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uid)
                .child("contacts")
                .updateChildren(contacts)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }

    private void observeData() {
        userReference = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(contactUid);


        ViewModelProviders.of(this)
                .get(ContactViewModel.class)
                .getContact()
                .observe(this, new Observer<MOVUser>() {
                    @Override
                    public void onChanged(MOVUser movUser) {
                        ContactCard.setUserImage(ContactActivity.this, movUser, contactImage);
                        contactName.setText(movUser.getName());
                        if(movUser.getPhone() != null) {
                            contactPhone.setText(PhoneNumberUtils.formatNumber(movUser.getPhone()));
                        }
                        setActionBarTitle(movUser.getName());
                    }
                });
    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        userReference.addValueEventListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        userReference.removeEventListener(this);
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
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        MOVUser contact = dataSnapshot.getValue(MOVUser.class);

        ContactViewModel contactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);

        if(contact != null && contactViewModel.getContact().getValue() == null) {
            viewSwitcher.showNext();
        }
        contactViewModel.setContact(contact);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
