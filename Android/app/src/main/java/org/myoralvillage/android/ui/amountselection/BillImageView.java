package org.myoralvillage.android.ui.amountselection;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;

import java.util.Locale;

public class BillImageView extends AppCompatImageView {


    public BillImageView(Context context) {
        super(context);
    }

    public BillImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setCurrencyDenomination(MOVCurrencyDenomination denomination) {
        if(denomination != null) {
            String drawableName = String.format(Locale.getDefault(), "%s_%d", denomination.getCurrency().getCode().toLowerCase(), denomination.getValue());
            setImageResource(getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName()));
        } else {
            setImageResource(R.drawable.bill_add);
        }
    }

}
