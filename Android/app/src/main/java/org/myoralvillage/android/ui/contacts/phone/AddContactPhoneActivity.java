package org.myoralvillage.android.ui.contacts.phone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.CountryCodePicker;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.contacts.AddContactViewModel;
import org.myoralvillage.android.ui.util.ErrorClearTextWatcher;
import org.myoralvillage.android.ui.widgets.ContactCard;

import java.util.HashMap;
import java.util.Map;

public class AddContactPhoneActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT_UID = "ContactUid";

    private TextInputEditText phoneField;
    private CountryCodePicker countryCodePicker;

    private MaterialButton searchButton;

    private ProgressBar progressBar;
    private TextView errorText;
    private MaterialButton addButton;
    private View contactResultView;
    private View contactSearchForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_phone);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        bindViews();
        observeData();
        addListeners();

        if(getIntent() != null) {
            if(getIntent().getStringExtra(EXTRA_CONTACT_UID) != null) {
                contactSearchForm.setVisibility(View.GONE);
                
                String contactUid = getIntent().getStringExtra(EXTRA_CONTACT_UID);

                searchContactFromUid(contactUid);
            }
        }
    }

    private void searchContactFromUid(String uid) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MOVUser user = dataSnapshot.getValue(MOVUser.class);
                        if(user != null) {
                            searchUsers(user.getPhone());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void bindViews() {
        phoneField = findViewById(R.id.add_contact_phone_field);
        countryCodePicker = findViewById(R.id.add_contact_phone_ccp);
        searchButton = findViewById(R.id.add_contact_phone_button_search);
        progressBar = findViewById(R.id.add_contact_phone_progress);
        errorText = findViewById(R.id.add_contact_phone_text_error);
        addButton = findViewById(R.id.add_contact_phone_button_add);
        contactResultView = findViewById(R.id.add_contact_phone_layout_contact);
        contactSearchForm = findViewById(R.id.add_contact_phone_layout_form);


        countryCodePicker.registerPhoneNumberTextView(phoneField);
        phoneField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phoneField.addTextChangedListener(new ErrorClearTextWatcher(phoneField));
        phoneField.setImeActionLabel(
                getResources().getString(R.string.add_contact_phone_search),
                EditorInfo.IME_ACTION_SEARCH);
    }

    private void observeData() {
        final AddContactViewModel viewModel = ViewModelProviders.of(this).get(AddContactViewModel.class);

        viewModel.getState().observe(this, new Observer<AddContactViewModel.AddContactState>() {
            @Override
            public void onChanged(AddContactViewModel.AddContactState addContactState) {
                Log.v("AddContactPhone", "State change: "+addContactState);
                int loadingVisibility;
                int errorVisibility;
                int resultVisibility;

                switch(addContactState) {
                    case Loading:
                        loadingVisibility = View.VISIBLE;
                        errorVisibility = View.GONE;
                        resultVisibility = View.GONE;
                        break;
                    case Found:
                        loadingVisibility = View.GONE;
                        errorVisibility = View.GONE;
                        resultVisibility = View.VISIBLE;
                        break;
                    case Error:
                        loadingVisibility = View.GONE;
                        errorVisibility = View.VISIBLE;
                        resultVisibility = View.GONE;
                        Log.v("AddContactPhone", "ErrorText: "+viewModel.getErrorText());
                        errorText.setText(viewModel.getErrorText());
                        break;
                    case Blank:
                    default:
                        loadingVisibility = View.GONE;
                        errorVisibility = View.GONE;
                        resultVisibility = View.GONE;
                        break;
                }

                errorText.setVisibility(errorVisibility);
                progressBar.setVisibility(loadingVisibility);
                contactResultView.setVisibility(resultVisibility);
                addButton.setVisibility(resultVisibility);
            }
        });

        viewModel.getContactToAdd().observe(this, new Observer<MOVUser>() {
            @Override
            public void onChanged(MOVUser movUser) {
                if(movUser != null) {
                    TextView nameText = contactResultView.findViewById(R.id.cell_contact_text_name);
                    ImageView contactImage = contactResultView.findViewById(R.id.cell_contact_image_contact);

                    nameText.setText(movUser.getName());
                    ContactCard.setUserImage(AddContactPhoneActivity.this, movUser, contactImage);
                }
            }
        });
    }

    private void addListeners() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSelectedContact();
            }
        });

        phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    return doSearch();
                }
                return false;
            }
        });
    }

    private boolean doSearch() {
        if(!countryCodePicker.isValid()) {
            phoneField.setError(getResources().getString(R.string.add_contact_phone_enter_number));
            return false;
        }

        hideKeyboard();

        String formattedNumber = countryCodePicker.getFullNumberWithPlus();
        searchUsers(transformPhoneNumber(formattedNumber));

        return true;
    }

    private void searchUsers(String number) {
        final AddContactViewModel viewModel = ViewModelProviders.of(this).get(AddContactViewModel.class);
        viewModel.setState(AddContactViewModel.AddContactState.Loading);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .orderByChild("phone")
                .equalTo(number)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChildren()) {
                            handleNotFound();

                            return;
                        }

                        DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                        MOVUser user = userSnapshot.getValue(MOVUser.class);

                        if(user == null || user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                            handleNotFound();

                            return;
                        }

                        viewModel.setContactToAdd(user);
                        viewModel.setState(AddContactViewModel.AddContactState.Found);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}

                    private void handleNotFound() {
                        viewModel.setErrorText(getResources().getString(R.string.add_contact_phone_not_found));
                        viewModel.setState(AddContactViewModel.AddContactState.Error);
                    }
                });
    }

    private void addSelectedContact() {
        AddContactViewModel viewModel = ViewModelProviders.of(this).get(AddContactViewModel.class);
        MOVUser selectedContact = viewModel.getContactToAdd().getValue();

        if(selectedContact == null) {
            throw new IllegalStateException("Cannot add null contact");
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> newContact = new HashMap<>();
        newContact.put(selectedContact.getUid(), true);


        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUser.getUid())
                .child("contacts")
                .updateChildren(newContact, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        setResult(RESULT_OK, null);

                        finish();
                    }
                });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private String transformPhoneNumber(String formattedNumber) {
        return formattedNumber.replaceAll(" *-*\\(*\\)*", "");
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

}
