package org.myoralvillage.android.ui.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.MainActivity;
import org.myoralvillage.android.ui.util.ErrorClearTextWatcher;

import java.util.HashMap;
import java.util.Map;

public class LoginCompletionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "LoginCompletionActivity";

    private TextInputEditText firstNameText;
    private TextInputEditText lastNameText;

    private MaterialButton submitButton;

    private ViewSwitcher viewSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_completion);

        viewSwitcher = findViewById(R.id.login_completion_switcher);

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

        checkIfLoggedIn();
    }

    private void checkIfLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
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

        Map<String, Object> values = new HashMap<>();
        values.put("name", firstNameString+" "+lastNameString);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        userRef.updateChildren(values).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    firstNameText.setError(task.getException().toString());
                } else {
                    redirectToMainActivity();
                }
            }
        });
    }
}
