package org.myoralvillage.android.ui.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.TransactionTooLargeException;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
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
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.auth.LoginActivity;
import org.myoralvillage.android.ui.auth.LoginCompletionActivity;
import org.myoralvillage.android.ui.contacts.ContactsActivity;
import org.myoralvillage.android.ui.util.ErrorClearTextWatcher;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment {

    //Firebase
    private static final String TAG = "UserFragment";
    private static final int REQUEST_SELECT_PHOTO = 0;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String UID;
    private CountryCodePicker countryField;
    private TextInputEditText phoneField;
    private TextInputEditText nameField;
    private TextInputEditText currencyField;
    private CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");
    private ViewSwitcher viewSwitcher;
    private AlertDialog selectPhotoDialog;
    private MaterialCardView pictureCard;
    private ViewSwitcher pictureCardSwitcher;
    private CircleImageView pictureCardImage;
    private FloatingActionButton pictureDeleteButton;

    private Uri selectedPhotoUri;
    private Button saveButton;
    private Button logoutButton;
    private FirebaseUser user;

    private String newcurrency;
    private String currency;
    private String country;
    private String name;

    public UserFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        user = mAuth.getCurrentUser();
        UID = user.getUid();

        picker.setListener(new CurrencyPickerListener() {
            @Override
            public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                Log.e("CURRENCY", name + " " + code + " " + symbol + flagDrawableResID);
                newcurrency = code;
                currencyField.setText(code);
                picker.dismiss();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:User:Logged_In: " + user.getUid());
                    toastMessage("Signed in with: " + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:User:Logged_out: " + user.getUid());
                    toastMessage("Signed out with: " + user.getUid());
                }
            }
        };
        myRef.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        phoneField = view.findViewById(R.id.user_phonenumber);
//        phoneField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
//        phoneField.addTextChangedListener(new ErrorClearTextWatcher(phoneField));

        nameField = view.findViewById(R.id.user_name);
        currencyField = view.findViewById(R.id.user_currency);
        currencyField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCurrencyClicked(picker);
            }
        });

        countryField = (CountryCodePicker) view.findViewById(R.id.user_country);
        countryField.setHidePhoneCode(true);
        countryField.showFullName(true);

        pictureDeleteButton = view.findViewById(R.id.login_completion_fab);
        showHideFab();
        pictureDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPhotoUri != null) {
                    setSelectedPhotoUri(null);
                }
            }
        });

        pictureCard = view.findViewById(R.id.login_completion_card);
        pictureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPictureCardClicked();
            }
        });
        pictureCardSwitcher = view.findViewById(R.id.login_completion_card_switcher);
        pictureCardImage = view.findViewById(R.id.login_completion_card_image);

        logoutButton = view.findViewById(R.id.user_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        saveButton = view.findViewById(R.id.user_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        MaterialCardView contactsButton = view.findViewById(R.id.user_contacts);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ContactsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void onPictureCardClicked() {
        presentPictureOptionsDialog();
    }

    private void doUploadSelectedImage() {
        if (selectedPhotoUri != null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference child = storageRef.child("users/" + currentUser.getUid() + "/raw_profile.jpg");
            child.putFile(selectedPhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    getFragmentManager().popBackStack();
                    //redirectToMainActivity();
                }
            });
        } else {
            getFragmentManager().popBackStack();
        }

    }

    private void presentPictureOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View bodyView = getLayoutInflater().inflate(R.layout.layout_contact_photo_select, null);
        MaterialButton selectPhotoButton = bodyView.findViewById(R.id.contact_photo_select_button_select);
        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getActivity().startActivityForResult(selectPhoto, REQUEST_SELECT_PHOTO);
            }
        });
        MaterialButton takePhotoButton = bodyView.findViewById(R.id.contact_photo_select_button_take);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File tempFile = new File(getContext().getFilesDir(), "images/image.jpg");
                if (!tempFile.exists() && !tempFile.mkdir()) return;

                Uri uri = FileProvider.getUriForFile(
                        getContext(),
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

    private void showHideFab() {
        if (selectedPhotoUri == null) {
            pictureDeleteButton.hide();
        } else {
            pictureDeleteButton.show();
        }
    }

    private void onCurrencyClicked(CurrencyPicker picker) {
        picker.show(getFragmentManager(), "CURRENCY_PICKER");
    }

    private void showData(DataSnapshot dataSnapshot) {
        DataSnapshot userSnap = dataSnapshot.child("users/" + UID);
        ArrayList<String> array = new ArrayList<>();
        MOVUser user = userSnap.getValue(MOVUser.class);
        name = user.getName();
        currency = user.getCurrency();
        country = user.getCountry();
        nameField.setText(user.getName());
        currencyField.setText(user.getCurrency());
        countryField.setCountryForNameCode(user.getCountry());
        phoneField.setText(user.getPhone());
//        setSelectedPhotoUri(Uri.parse(user.getImage()));
//        for (DataSnapshot ds : userSnap.getChildren()) {
//            array.add(ds.getKey() + ":" + ds.getValue(String.class));
//        }
    }

    private void toastMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}