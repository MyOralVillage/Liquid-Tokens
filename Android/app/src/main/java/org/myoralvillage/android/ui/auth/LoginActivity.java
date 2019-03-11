package org.myoralvillage.android.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.rilixtech.CountryCodePicker;


import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.util.ErrorClearTextWatcher;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class LoginActivity extends Activity {

    private static final String LOG_TAG = "LoginActivity";

    private TextInputEditText phoneField;
    private TextInputEditText codeField;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private ViewSwitcher viewSwitcher;
    private CountryCodePicker ccp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d(LOG_TAG, "onVerificationCompleted: " + phoneAuthCredential);

            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.w(LOG_TAG, "onVerificationFailed", e);

            phoneField.setError(getResources().getString(R.string.login_error_invalid_number));
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
            LoginActivity.this.verificationId = verificationId;
            LoginActivity.this.resendToken = token;

            viewSwitcher.showNext();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        phoneField = findViewById(R.id.login_input_phone);
        phoneField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phoneField.addTextChangedListener(new ErrorClearTextWatcher(phoneField));

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerPhoneNumberTextView(phoneField);
        ccp.enablePhoneAutoFormatter(true);
        ccp.hideNameCode(true);
        ccp.setHidePhoneCode(true);

        codeField = findViewById(R.id.login_input_code);
        codeField.addTextChangedListener(new ErrorClearTextWatcher(codeField));

        MaterialButton logInButton = findViewById(R.id.login_button_login);

        MaterialButton verifyButton = findViewById(R.id.login_button_code);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVerifyButtonClicked();
            }
        });
        MaterialButton tryAgainButton = findViewById(R.id.login_button_try_again);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTryAgainButtonClicked();
            }
        });

        viewSwitcher = findViewById(R.id.login_view_switcher);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogInButtonClicked();
            }
        });
    }

    private void onVerifyButtonClicked() {
        if (codeField.getText() == null) {
            codeField.setError(getResources().getString(R.string.login_error_blank_number));
            return;
        } else if (codeField.getText().toString().length() < 6) {
            codeField.setError(getResources().getString(R.string.login_error_length));
        }

        String code = codeField.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        Task<AuthResult> task = signInWithPhoneAuthCredential(credential);
        task.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        codeField.setError(getResources().getString(R.string.login_error_invalid_code));
                    }
                }
            }
        });
    }

    private void onTryAgainButtonClicked() {
        viewSwitcher.showPrevious();
    }

    private void onLogInButtonClicked() {
        if (!ccp.isValid()) {
            phoneField.setError(getResources().getString(R.string.login_error_blank_number));
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                ccp.getFullNumberWithPlus(),
                60, TimeUnit.SECONDS,
                this,
                verificationCallbacks
        );
    }

    private Task<AuthResult> signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Task<AuthResult> task = FirebaseAuth.getInstance().signInWithCredential(credential);
        task.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    onLoginSuccess();
                }
            }
        });

        return task;
    }

    private void onLoginSuccess() {
        startActivity(new Intent(this, LoginCompletionActivity.class));

        finish();
    }


}

