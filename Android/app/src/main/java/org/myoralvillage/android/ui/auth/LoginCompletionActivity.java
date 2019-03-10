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
import android.provider.MediaStore;
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

import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.MainActivity;
import org.myoralvillage.android.ui.util.ErrorClearTextWatcher;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginCompletionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "LoginCompletionActivity";

    private static final int REQUEST_SELECT_PHOTO = 0;
    private static final int REQUEST_TAKE_PHOTO = 1;

    private static final String ARG_SELECTED_PHOTO_URI = "selectedPhotoUri";

    private TextInputEditText firstNameText;
    private TextInputEditText lastNameText;

    private MaterialButton submitButton;

    private MaterialCardView pictureCard;
    private ViewSwitcher pictureCardSwitcher;
    private CircleImageView pictureCardImage;
    private FloatingActionButton pictureDeleteButton;

    private ViewSwitcher viewSwitcher;
    private AlertDialog selectPhotoDialog;

    private Uri selectedPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_completion);

        if(savedInstanceState != null) {
            selectedPhotoUri = savedInstanceState.getParcelable(ARG_SELECTED_PHOTO_URI);
        }

        viewSwitcher = findViewById(R.id.login_completion_switcher);
        pictureDeleteButton = findViewById(R.id.login_completion_fab);
        pictureDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPhotoUri != null) {
                    setSelectedPhotoUri(null);
                }
            }
        });
        firstNameText = findViewById(R.id.login_completion_field_first_name);
        firstNameText.addTextChangedListener(new ErrorClearTextWatcher(firstNameText));

        lastNameText = findViewById(R.id.login_completion_field_last_name);
        lastNameText.addTextChangedListener(new ErrorClearTextWatcher(lastNameText));

        submitButton = findViewById(R.id.login_completion_button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitButtonClicked();
            }
        });

        pictureCard = findViewById(R.id.login_completion_card);
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

        if(selectedPhotoUri != null) {
            outState.putParcelable(ARG_SELECTED_PHOTO_URI, selectedPhotoUri);
        }
    }

    private void showHideFab() {
        if(selectedPhotoUri == null) {
            pictureDeleteButton.hide();
        } else {
            pictureDeleteButton.show();
        }
    }

    private void onPictureCardClicked() {
        presentPictureOptionsDialog();
    }

    private void checkIfLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.hasChild("name")) {
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
        Editable lastNameEditable = lastNameText.getText();

        String firstNameString = null;
        String lastNameString = null;

        String error = getResources().getString(R.string.login_completion_error_empty_name);
        if(firstNameEditable == null ||
                (firstNameString = firstNameEditable.toString()).length() <= 1) {
            firstNameText.setError(error);

            return;
        }

        if(lastNameEditable == null ||
                (lastNameString = lastNameEditable.toString()).length() <= 1) {
            lastNameText.setError(error);

            return;
        }

        viewSwitcher.showPrevious();

        Map<String, Object> values = new HashMap<>();
        values.put("name", firstNameString+" "+lastNameString);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        userRef.updateChildren(values).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    firstNameText.setError(task.getException().toString());

                    viewSwitcher.showNext();
                } else {
                    doUploadSelectedImage();
                }
            }
        });
    }

    private void doUploadSelectedImage() {
        if(selectedPhotoUri != null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference child = storageRef.child("users/"+currentUser.getUid() +"/raw_profile.jpg");
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

    private void presentPictureOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View bodyView = getLayoutInflater().inflate(R.layout.layout_contact_photo_select, null);
        MaterialButton selectPhotoButton = bodyView.findViewById(R.id.contact_photo_select_button_select);
        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(selectPhoto, REQUEST_SELECT_PHOTO);
            }
        });
        MaterialButton takePhotoButton = bodyView.findViewById(R.id.contact_photo_select_button_take);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File tempFile = new File(getFilesDir(), "images/image.jpg");
                if(!tempFile.exists() && !tempFile.mkdir()) return;

                Uri uri = FileProvider.getUriForFile(
                        LoginCompletionActivity.this,
                        "org.myoralvillage.android.provider",
                        tempFile);

                Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                startActivityForResult(takePhoto, REQUEST_TAKE_PHOTO);

            }
        });


        builder.setView(bodyView);
        builder.setTitle(getResources().getString(R.string.login_completion_dialog_add_photo));

        selectPhotoDialog = builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                File imageFile = new File(getFilesDir(), "images/image.jpg");

                Uri uri = FileProvider.getUriForFile(
                        LoginCompletionActivity.this,
                        "org.myoralvillage.android.provider",
                        imageFile);

                setSelectedPhotoUri(uri);
            }
        } else if(requestCode == REQUEST_SELECT_PHOTO) {
            if(data != null) {
                Uri imageUri = data.getData();

                setSelectedPhotoUri(imageUri);
            }
        }

        selectPhotoDialog.dismiss();
    }

    private void setSelectedPhotoUri(Uri uri) {
        selectedPhotoUri = uri;
        pictureCardImage.setImageURI(uri);

        if(uri == null) {
            pictureCardSwitcher.setDisplayedChild(0);
        } else {
            pictureCardSwitcher.setDisplayedChild(1);
        }

        showHideFab();
    }
}
