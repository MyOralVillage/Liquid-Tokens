package org.myoralvillage.android.data.currency;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class MOVCurrency {

    public static MOVCurrency loadFromJson(Context context, String IsoCountryCode) throws IOException, JSONException {
        InputStream stream = context.getResources().getAssets().open("currency/"+IsoCountryCode+".json");
        byte[] buffer = new byte[stream.available()];
        int bytesRead = stream.read(buffer);
        stream.close();

        String jsonString = new String(buffer, "UTF-8");
        JSONObject currencyJson = new JSONObject(jsonString);
        String code = currencyJson.getString("code");

        JSONArray denominationsJson = currencyJson.getJSONArray("denominations");
        List<MOVCurrencyDenomination> denominations = new ArrayList<>();

        MOVCurrency currency = new MOVCurrency(code, denominations);
        for(int i = 0; i < denominationsJson.length(); i++) {
            JSONObject denominationObject = denominationsJson.getJSONObject(i);

            int amount = denominationObject.getInt("value");
            String typeString = denominationObject.getString("type");

            MOVCurrencyDenominationType type = MOVCurrencyDenominationType.GetTypeFromString(typeString);

            denominations.add(new MOVCurrencyDenomination(currency, type, amount));
        }

        return currency;
    }

    private List<MOVCurrencyDenomination> mDenominations;

    private String mCode;

    private MOVCurrency(String code, List<MOVCurrencyDenomination> denominations) {
        this.mDenominations = denominations;
        this.mCode = code;
    }

    public List<MOVCurrencyDenomination> getDenominations() {
        return mDenominations;
    }

    public String getCode() {
        return mCode;
    }

    public String getSymbol() {
        return Currency.getInstance(mCode).getSymbol();
    }

    public double convertToFraction(int amount) {
        return (double) amount / Math.pow(10, Currency.getInstance(mCode).getDefaultFractionDigits());
    }

    public String getFormattedString(int amount) {
        int nFractionDigits = Currency.getInstance(mCode).getDefaultFractionDigits();
        return String.format("%s%."+nFractionDigits+"f", getSymbol(), convertToFraction(amount));
    }
}
