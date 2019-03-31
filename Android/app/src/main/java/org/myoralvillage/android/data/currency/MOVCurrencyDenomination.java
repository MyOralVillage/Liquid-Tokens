package org.myoralvillage.android.data.currency;

public class MOVCurrencyDenomination implements Comparable<MOVCurrencyDenomination>{


    private MOVCurrency mCurrency;
    private int mValue;
    private MOVCurrencyDenominationType mType;

    /**
     * Represents a currency denomination (e.g. $20 bill, $2 coin)
     *
     * @param currency The parent currency
     * @param type The type of the currency denomination (bill, coin). This parameter is currently
     *             unused.
     * @param value The value in the form of the currency's smallest denomination.
     *              (e.g. for USD smallest is 1 cent, so 5 dollars is 500 cents)
     */
    public MOVCurrencyDenomination(MOVCurrency currency, MOVCurrencyDenominationType type, int value) {
        this.mValue = value;
        this.mType = type;
        this.mCurrency = currency;
    }

    public int getValue() {
        return mValue;
    }

    public MOVCurrency getCurrency() {
        return mCurrency;
    }

    public MOVCurrencyDenominationType getType() {
        return mType;
    }

    public String toString(){
        return(this.mValue+"");
    }

    @Override
    public int compareTo(MOVCurrencyDenomination o) {
        if(o.mValue == mValue) {
            return 0;
        } else if(o.mValue > mValue) {
            return 1;
        }
        return -1;
    }
}
