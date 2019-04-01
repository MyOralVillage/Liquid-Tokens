package org.myoralvillage.android.ui.scan;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.util.GlideApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class DisplayQRActivity extends AppCompatActivity {
    ImageView qrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qr);
        setQrImage();
        setListeners();

    }

    private void setListeners() {
        Button return_button = findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setQrImage() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference child = storageRef.child("users/" + currentUser.getUid() + "/qr.png");

        qrImage = (ImageView) findViewById(R.id.qr_create_image);


        if (child == null) {
            Log.v("LOC", "qr code holder is null");

        } else {
            GlideApp.with(this)
                    .load(child)
                    .into(qrImage);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
