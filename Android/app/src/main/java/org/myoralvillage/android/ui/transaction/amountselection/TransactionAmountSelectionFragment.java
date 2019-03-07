package org.myoralvillage.android.ui.transaction.amountselection;

import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import org.myoralvillage.android.ui.transaction.OnTransactionPageInteractionListener;
import org.myoralvillage.android.ui.transaction.TransactionPage;
import org.myoralvillage.android.ui.transaction.TransactionViewModel;

import java.io.IOException;
import java.util.List;


public class TransactionAmountSelectionFragment extends Fragment implements TransactionPage {

    private static final String ARG_CURRENCY_CODE = "currency_code";

    private static final String PARAM_CURRENCY_CODE = "currency_code";

    private MOVCurrency currency;

    private String currencyCode;

    private OnTransactionPageInteractionListener interactionListener;

    public TransactionAmountSelectionFragment() {
        // Required empty public constructor
    }

    public static TransactionAmountSelectionFragment newInstance(String isoCurrencyCode) {
        TransactionAmountSelectionFragment fragment = new TransactionAmountSelectionFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_CURRENCY_CODE, isoCurrencyCode);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            currencyCode = getArguments().getString(ARG_CURRENCY_CODE);
        } else if(savedInstanceState != null) {
            currencyCode = savedInstanceState.getString(PARAM_CURRENCY_CODE);
        }

        Context context = getContext();

        if(context != null) {
            try {
                currency = MOVCurrency.loadFromJson(context, currencyCode);
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
        View view = inflater.inflate(R.layout.fragment_transaction_amount_selection, container, false);

        final TransactionAmountSelectionViewModel model = ViewModelProviders.of(getActivity()).get(TransactionAmountSelectionViewModel.class);

        final TransactionAmountSelectionListAdapter adapter = new TransactionAmountSelectionListAdapter(model);
        GridView gridView = view.findViewById(R.id.amount_selection_grid);
        gridView.setOnDragListener(new DenominationTargetDragListener(model, true));
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
                    header.setText(R.string.transaction_amount_selection_header);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TransactionAmountSelectionViewModel selectionViewModel = ViewModelProviders.of(getActivity()).get(TransactionAmountSelectionViewModel.class);
        final TransactionViewModel transactionViewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        selectionViewModel.getValue().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                transactionViewModel.setSelectedAmount(integer);
                if(integer == null || integer == 0) {
                    interactionListener.setCanGoForward(TransactionAmountSelectionFragment.this, false);
                } else {
                    interactionListener.setCanGoForward(TransactionAmountSelectionFragment.this, true);
                }
            }
        });

    }

    private void populateCurrencySelection(LayoutInflater inflater, LinearLayout currencySelection, TransactionAmountSelectionViewModel viewModel) {
        currencySelection.setOnDragListener(new DenominationTargetDragListener(viewModel, false));
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

            final DenominationImageView billImage = billLayout.findViewById(R.id.bill_image);
            billImage.setCurrencyDenomination(denomination);
            billImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            billImage);
                    DenominationDragPayload payload = new DenominationDragPayload(denomination, false);
                    billImage.startDrag(data, shadowBuilder, payload, 0);

                    return true;
                }
            });


            currencySelection.addView(billLayout);
        }
    }

    @Override
    public void onPageMadeActive() {
        TransactionViewModel viewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        Integer currentAmount = viewModel.getSelectedAmount().getValue();

        interactionListener.setCanGoForward(this, currentAmount != null && currentAmount > 0);

    }

    @Override
    public int getForwardButtonText() {
        return R.string.transaction_next;
    }

    @Override
    public int getForwardButtonIcon() {
        return R.drawable.baseline_arrow_forward_black_24;
    }

    @Override
    public void onNextButtonPressed() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof OnTransactionPageInteractionListener) {
            interactionListener = (OnTransactionPageInteractionListener) context;
        } else {
            throw new IllegalStateException("Fragment must be attached to OnTransactionPageInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        interactionListener = null;
    }
}
