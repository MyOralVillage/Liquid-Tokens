package org.myoralvillage.android.ui.amountselection;

import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;

import java.io.IOException;
import java.util.List;


public class AmountSelectionFragment extends Fragment {

    private static final String PARAM_CURRENCY_CODE = "currency_code";

    private MOVCurrency currency;

    private String currencyCode;

    public AmountSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            currencyCode = savedInstanceState.getString(PARAM_CURRENCY_CODE);
        }

        Context context = getContext();

        if(context != null) {
            // TODO: get currency from actual config
            try {
                currency = MOVCurrency.loadFromJson(context, "cad");
            } catch (IOException e) {
                e.printStackTrace();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(PARAM_CURRENCY_CODE, currencyCode);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amount_selection, container, false);

        final AmountSelectionViewModel model = ViewModelProviders.of(this).get(AmountSelectionViewModel.class);

        final AmountSelectedListAdapter adapter = new AmountSelectedListAdapter(model);
        GridView gridView = view.findViewById(R.id.amount_selection_grid);
        gridView.setOnDragListener(new BillTargetDragListener(model, true));
        gridView.setAdapter(adapter);
        model.getSelectedCurrency().observe(this, new Observer<List<MOVCurrencyDenomination>>() {
            @Override
            public void onChanged(List<MOVCurrencyDenomination> currencyDenominations) {
                adapter.setSelectedDenominations(currencyDenominations);
            }
        });

        final LinearLayout currencyBills = view.findViewById(R.id.currency_bills);
        populateCurrencySelection(inflater, currencyBills, model);

        final View currencyAddFrame = view.findViewById(R.id.add_currency_frame);
        final Observer<Boolean> addObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean dragging) {
                if(dragging) {
                    currencyAddFrame.animate().setDuration(150).alpha(0.5f);
                } else if(model.getValue().getValue() != null && model.getValue().getValue() != 0){

                    currencyAddFrame.animate().setDuration(150).alpha(0);
                }
            }
        };
        model.isAdding().observe(this, addObserver);

        final TextView header = view.findViewById(R.id.selection_header);
        model.getValue().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 0) {
                    header.setText(R.string.amount_selection_title);
                    addObserver.onChanged(true);
                } else {
                    header.setText(currency.getFormattedString(integer));
                }
            }
        });

        final View currencyRemoveFrame = view.findViewById(R.id.remove_currency_frame);
        model.isRemoving().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean dragging) {
                if(dragging) {
                    currencyRemoveFrame.animate().setDuration(150).alpha(0.8f);
                } else {
                    currencyRemoveFrame.animate().setDuration(150).alpha(0);
                }
            }
        });

        return view;
    }

    private void populateCurrencySelection(LayoutInflater inflater, LinearLayout currencySelection, AmountSelectionViewModel viewModel) {
        currencySelection.setOnDragListener(new BillTargetDragListener(viewModel, false));
        Resources r = getContext().getResources();
        int marginVertical = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                r.getDisplayMetrics()
        );
        int marginHorizontal = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8,
                r.getDisplayMetrics()
        );
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        params.setMargins(marginHorizontal, marginVertical, marginHorizontal, marginVertical);
        for(final MOVCurrencyDenomination denomination : currency.getDenominations()) {
            LinearLayout billLayout = (LinearLayout) inflater.inflate(R.layout.layout_bill, null);
            billLayout.setLayoutParams(params);

            final BillImageView billImage = billLayout.findViewById(R.id.bill_image);
            billImage.setCurrencyDenomination(denomination);
            billImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            billImage);
                    BillDragPayload payload = new BillDragPayload(denomination, false);
                    billImage.startDrag(data, shadowBuilder, payload, 0);

                    return true;
                }
            });


            currencySelection.addView(billLayout);
        }
    }

    public int getAmount() {
        AmountSelectionViewModel model = ViewModelProviders.of(this).get(AmountSelectionViewModel.class);
        Integer amount = model.getValue().getValue();

        if(amount == null) {
            return 0;
        } else {
            return amount;
        }
    }
}
