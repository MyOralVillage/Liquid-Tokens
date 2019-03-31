package org.myoralvillage.android.ui.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.rilixtech.CountryCodePicker;

import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.MainActivity;
import org.myoralvillage.android.ui.util.ErrorClearTextWatcher;
import org.myoralvillage.android.ui.widgets.PhotoSelectionDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LoginCompletionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "LoginCompletionActivity";

    private static final int REQUEST_SELECT_PHOTO = 0;
    private static final int REQUEST_TAKE_PHOTO = 1;

    private static final String ARG_SELECTED_PHOTO_URI = "selectedPhotoUri";

    private TextInputEditText firstNameText;
    //private TextInputEditText lastNameText;
    private TextInputEditText currencyText;

    private ViewSwitcher pictureCardSwitcher;
    private CircleImageView pictureCardImage;
    private FloatingActionButton pictureDeleteButton;

    private ViewSwitcher viewSwitcher;
    private AlertDialog selectPhotoDialog;

    private Uri selectedPhotoUri;

    private CountryCodePicker ccp;
    private String currency;
    private final CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_completion);

        if (savedInstanceState != null) {
            selectedPhotoUri = savedInstanceState.getParcelable(ARG_SELECTED_PHOTO_URI);
        }

        viewSwitcher = findViewById(R.id.login_completion_switcher);
        pictureDeleteButton = findViewById(R.id.login_completion_fab);
        pictureDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPhotoUri != null) {
                    setSelectedPhotoUri(null);
                }
            }
        });

        picker.setListener(new CurrencyPickerListener() {
            @Override
            public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                Log.e("CURRENCY", name + " " + code + " " + symbol + flagDrawableResID);
                currency = code;
                currencyText.setText(code);
                picker.dismiss();
            }
        });

        firstNameText = findViewById(R.id.login_completion_field_first_name);
        firstNameText.addTextChangedListener(new ErrorClearTextWatcher(firstNameText));

        currencyText = findViewById(R.id.login_completion_currency);
        currencyText.addTextChangedListener(new ErrorClearTextWatcher(currencyText));
        currencyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCurrencyClicked(picker);
            }
        });

        ccp = findViewById(R.id.ccp);
        ccp.setHidePhoneCode(true);
        ccp.showFullName(true);

        MaterialButton submitButton = findViewById(R.id.login_completion_button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitButtonClicked();
            }
        });

        MaterialCardView pictureCard = findViewById(R.id.login_completion_card);
        pictureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPictureCardClicked();
            }
        });
        pictureCardSwitcher = findViewById(R.id.login_completion_card_switcher);
        pictureCardImage = findViewById(R.id.login_completion_card_image);

        checkIfLoggedIn();
        setSelectedPhotoUri(selectedPhotoUri);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (selectedPhotoUri != null) {
            outState.putParcelable(ARG_SELECTED_PHOTO_URI, selectedPhotoUri);
        }
    }

    private void showHideFab() {
        if (selectedPhotoUri == null) {
            pictureDeleteButton.hide();
        } else {
            pictureDeleteButton.show();
        }
    }

    private void onCurrencyClicked(CurrencyPicker picker) {
        picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
    }

    private void onPictureCardClicked() {
        selectPhotoDialog = PhotoSelectionDialog.presentPictureOptionsDialog(this);
    }

    private void checkIfLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("name")) {
                    redirectToMainActivity();
                } else {
                    showRegistrationForm();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(LOG_TAG, databaseError.toString());
            }
        });
    }

    private void showRegistrationForm() {
        viewSwitcher.showNext();
    }

    private void redirectToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));

        finish();
    }

    private void onSubmitButtonClicked() {
        Editable firstNameEditable = firstNameText.getText();
//        Editable lastNameEditable = lastNameText.getText();

        String firstNameString;
//        String lastNameString = null;

        String error = getResources().getString(R.string.login_completion_error_empty_name);
        if (firstNameEditable == null ||
                (firstNameString = firstNameEditable.toString()).length() <= 1) {
            firstNameText.setError(error);

            return;
        }

//        if (lastNameEditable == null ||
//                (lastNameString = lastNameEditable.toString()).length() <= 1) {
//            lastNameText.setError(error);
//
//            return;
//        }

        viewSwitcher.showPrevious();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> values = new HashMap<>();
        values.put("name", firstNameString);
        values.put("country", ccp.getSelectedCountryNameCode());
        values.put("currency", currency);
        values.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        values.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        userRef.updateChildren(values).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    firstNameText.setError(task.getException().toString());

                    viewSwitcher.showNext();
                } else {
                    doUploadSelectedImage();
                }
            }
        });
    }

    private void doUploadSelectedImage() {
        if (selectedPhotoUri != null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference child = storageRef.child("users/" + currentUser.getUid() + "/raw_profile.jpg");
            child.putFile(selectedPhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    redirectToMainActivity();
                }
            });
        } else {
            redirectToMainActivity();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                File imageFile = new File(getFilesDir(), "images/image.jpg");

                Uri uri = FileProvider.getUriForFile(
                        LoginCompletionActivity.this,
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

        selectPhotoDialog.dismiss();
    }

    private void setSelectedPhotoUri(Uri uri) {
        selectedPhotoUri = uri;
        pictureCardImage.setImageURI(uri);

        if (uri == null) {
            pictureCardSwitcher.setDisplayedChild(0);
        } else {
            pictureCardSwitcher.setDisplayedChild(1);
        }

        showHideFab();
    }
}
