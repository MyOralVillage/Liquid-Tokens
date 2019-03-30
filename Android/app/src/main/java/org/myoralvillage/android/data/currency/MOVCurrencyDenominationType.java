package org.myoralvillage.android.data.currency;

public enum MOVCurrencyDenominationType {
    Bill("bill"), Coin("coin");

    private final String type;

    MOVCurrencyDenominationType(String type) {
        this.type = type;
    }

    public static MOVCurrencyDenominationType GetTypeFromString(String type) {
        for(MOVCurrencyDenominationType denominationType : values()) {
            if(denominationType.type.equals(type)) {
                return denominationType;
            }
        }

        throw new IllegalArgumentException("Denomination type not found");
    }
}

