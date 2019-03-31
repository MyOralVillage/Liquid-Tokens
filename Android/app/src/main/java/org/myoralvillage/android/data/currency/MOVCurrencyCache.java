package org.myoralvillage.android.data.currency;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class MOVCurrencyCache {

    private final Map<String, MOVCurrency> currencies = new HashMap<>();

    public MOVCurrency getCurrency(@NonNull Context context, @NonNull String isoCurrencyCode) {
        MOVCurrency existing = currencies.get(isoCurrencyCode);

        if(existing == null) {
            try {
                existing = MOVCurrency.loadFromJson(context, isoCurrencyCode);
                currencies.put(isoCurrencyCode, existing);
            } catch (Exception e) {
                throw new IllegalStateException("Error loading currency: "+isoCurrencyCode);
            }
        }

        return existing;
    }
}
