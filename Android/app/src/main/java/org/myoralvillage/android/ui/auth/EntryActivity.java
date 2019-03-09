package org.myoralvillage.android.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import org.myoralvillage.android.ui.MainActivity;
import org.myoralvillage.android.ui.auth.LoginActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

        finish();
    }
}
