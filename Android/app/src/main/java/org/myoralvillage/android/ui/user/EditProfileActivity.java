package org.myoralvillage.android.ui.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;
import com.mynameismidori.currencypicker.ExtendedCurrency;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.auth.LoginCompletionActivity;
import org.myoralvillage.android.ui.widgets.ContactCard;
import org.myoralvillage.android.ui.widgets.PhotoSelectionDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;

import static org.myoralvillage.android.ui.widgets.PhotoSelectionDialog.REQUEST_SELECT_PHOTO;
import static org.myoralvillage.android.ui.widgets.PhotoSelectionDialog.REQUEST_TAKE_PHOTO;

public class EditProfileActivity extends AppCompatActivity {

    private CurrencyPicker currencyPicker;

    private CircleImageView userProfileImage;
    private TextInputEditText userNameEditText;
    private ImageView userCurrencyImage;
    private Button userCurrencyButton;
    private Button userSaveProfileButton;

    private ExtendedCurrency selectedCurrency;

    private AlertDialog userProfileImageDialog;

    private Uri selectedPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        currencyPicker = CurrencyPicker.newInstance("Select Currency");
        currencyPicker.setCurrenciesList(getAvailableCurrencies());

        bindViews();
        addListeners();
        observeData();
    }

    private void bindViews() {
        userProfileImage = findViewById(R.id.profile_image);
        userNameEditText = findViewById(R.id.profile_edittext_name);
        userCurrencyImage = findViewById(R.id.profile_image_currency);
        userCurrencyButton = findViewById(R.id.profile_button_currency);
        userSaveProfileButton = findViewById(R.id.profile_button_save);
    }

    private void addListeners() {
        userCurrencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrencyPicker();
            }
        });

        userSaveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        currencyPicker.setListener(new CurrencyPickerListener() {
            @Override
            public void onSelectCurrency(String name, String code, String symbol, int i) {
                ExtendedCurrency selectedCurrency = MOVCurrency.getExtendedCurrencyByIso(code);
                if(selectedCurrency != null) {
                    setSelectedCurrency(selectedCurrency);
                }

                currencyPicker.dismiss();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileImageDialog = PhotoSelectionDialog.presentPictureOptionsDialog(EditProfileActivity.this);
            }
        });
    }

    private void observeData() {
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MOVUser user = dataSnapshot.getValue(MOVUser.class);

                if(user != null) {
                    showData(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showData(MOVUser user) {
        userNameEditText.setText(user.getName());
        ContactCard.setUserImage(this, user, userProfileImage, false);

        ExtendedCurrency currency = MOVCurrency.getExtendedCurrencyByIso(user.getCurrency().toUpperCase());
        if(currency != null) {
            setSelectedCurrency(currency);
        }
    }

    private void saveData() {
        String uid = FirebaseAuth.getInstance().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("name", userNameEditText.getText().toString());
        data.put("currency", selectedCurrency.getCode().toLowerCase());

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uid)
                .updateChildren(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                doUploadSelectedImage();
            }
        });
    }

    private void setSelectedCurrency(ExtendedCurrency currency) {
        this.selectedCurrency = currency;

        userCurrencyImage.setImageResource(currency.getFlag());
        userCurrencyButton.setText(currency.getName());
    }


    private void showCurrencyPicker() {
        currencyPicker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                File imageFile = new File(getFilesDir(), "images/image.jpg");

                Uri uri = FileProvider.getUriForFile(
                        EditProfileActivity.this,
                        "org.myoralvillage.android.provider",
                        imageFile);

                setSelectedPhotoUri(uri);
            }
        } else if (requestCode == REQUEST_SELECT_PHOTO) {
            if (data != null) {
                Uri imageUri = data.getData();

                setSelectedPhotoUri(imageUri);
            }
        }

        userProfileImageDialog.dismiss();
    }

    private void setSelectedPhotoUri(Uri uri) {
        selectedPhotoUri = uri;
        if(uri != null) {
            userProfileImage.setImageURI(uri);
        }
    }

    private void doUploadSelectedImage() {
        if (selectedPhotoUri != null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference child = storageRef.child("users/" + currentUser.getUid() + "/raw_"+selectedPhotoUri.getLastPathSegment()+".jpg");
            child.putFile(selectedPhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    finish();
                }
            });
        } else {
            finish();
        }

    }

    private List<ExtendedCurrency> getAvailableCurrencies() {
        List<ExtendedCurrency> currencies = new ArrayList<>();
        try {
            List<String> availableCurrencyCodes = MOVCurrency.getAvailableCurrencies(this);
            for(String currencyCode : availableCurrencyCodes) {
                currencyCode = currencyCode.toUpperCase();

                ExtendedCurrency currency = MOVCurrency.getExtendedCurrencyByIso(currencyCode.trim());

                if(currency != null) {
                    currencies.add(currency);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return currencies;
    }
}
