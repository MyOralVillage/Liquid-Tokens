package org.myoralvillage.android.ui.util;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.annotations.NotNull;

public class ErrorClearTextWatcher implements TextWatcher {

    private TextInputEditText editText;

    public ErrorClearTextWatcher(@NotNull TextInputEditText editText) {
        this.editText = editText;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        editText.setError(null);
    }
}