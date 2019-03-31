package org.myoralvillage.android.ui.contacts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.contacts.phone.AddContactPhoneActivity;
import org.myoralvillage.android.ui.scan.CameraScanActivity;

public class AddContactActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_CONTACT = 0;
    private static final int REQUEST_SCAN_QR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MaterialCardView phoneButton = findViewById(R.id.add_contact_button_phone);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhoneButtonClicked();
            }
        });
        MaterialCardView qrButton = findViewById(R.id.add_contact_button_qr);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onQrButtonClicked();
            }
        });
    }

    private void onPhoneButtonClicked() {
        Intent intent = new Intent(this, AddContactPhoneActivity.class);
        startActivityForResult(intent, REQUEST_ADD_CONTACT);
    }

    private void onQrButtonClicked() {
        Intent intent = new Intent(this, CameraScanActivity.class);

        startActivityForResult(intent, REQUEST_SCAN_QR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_ADD_CONTACT) {
            if(resultCode == RESULT_OK) {
                finish();
            }
        } else if(requestCode == REQUEST_SCAN_QR) {
            if(resultCode == RESULT_OK && data != null) {
                String contactUid = data.getStringExtra(CameraScanActivity.EXTRA_CONTACT_UID);

                Intent intent = new Intent(this, AddContactPhoneActivity.class);
                intent.putExtra(AddContactPhoneActivity.EXTRA_CONTACT_UID, contactUid);

                startActivityForResult(intent, REQUEST_ADD_CONTACT);
            }
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
}
