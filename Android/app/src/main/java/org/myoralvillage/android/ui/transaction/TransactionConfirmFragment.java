package org.myoralvillage.android.ui.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;
import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.transaction.amountselection.TransactionAmountSelectionListAdapter;
import org.myoralvillage.android.ui.transaction.amountselection.TransactionAmountSelectionViewModel;
import org.myoralvillage.android.ui.widgets.ContactCard;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class TransactionConfirmFragment extends Fragment implements TransactionPage {

    private static final String ARG_CURRENCY_CODE = "currency_code";
    private static final String ARG_TRANSACTION_TYPE = "transaction_type";

    private MaterialCardView contactCard;
    private TextView amountText;
    private TransactionAmountSelectionListAdapter selectedListAdapter;
    private GridView amountSelectedGridView;

    private String currencyCode;
    private int transactionType;
    private MOVCurrency currency;

    public TransactionConfirmFragment() {
        // Required empty constructor
    }

    public static TransactionConfirmFragment newInstance(String isoCountryCode, int transactionType) {
        TransactionConfirmFragment fragment = new TransactionConfirmFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_CURRENCY_CODE, isoCountryCode);
        bundle.putInt(ARG_TRANSACTION_TYPE, transactionType);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            currencyCode = getArguments().getString(ARG_CURRENCY_CODE);
            transactionType = getArguments().getInt(ARG_TRANSACTION_TYPE);
        }

        try {
            currency = MOVCurrency.loadFromJson(getContext(), currencyCode);
        } catch(IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_confirm, null);

        contactCard = view.findViewById(R.id.transaction_confirm_contact);
        amountText = view.findViewById(R.id.transaction_confirm_amount);
        amountSelectedGridView = view.findViewById(R.id.transaction_confirm_amount_selection);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TransactionViewModel transactionViewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        TransactionAmountSelectionViewModel selectionViewModel = ViewModelProviders.of(getActivity()).get(TransactionAmountSelectionViewModel.class);

        selectedListAdapter = new TransactionAmountSelectionListAdapter(selectionViewModel);
        amountSelectedGridView.setAdapter(selectedListAdapter);
        selectionViewModel.getSelectedCurrency().observe(this, new Observer<List<MOVCurrencyDenomination>>() {
            @Override
            public void onChanged(List<MOVCurrencyDenomination> currencyDenominations) {
                selectedListAdapter.setSelectedDenominations(currencyDenominations);
            }
        });
        selectionViewModel.getValue().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer amount) {
                if(amount == null) {
                    amount = 0;
                }
                amountText.setText(currency.getFormattedString(amount));
            }
        });
        transactionViewModel.getSelectedContact().observe(this, new Observer<MOVUser>() {
            @Override
            public void onChanged(MOVUser movUser) {
                if(movUser != null) {
                    ContactCard.setUser(contactCard, movUser);
                }
            }
        });

    }

    @Override
    public void onPageMadeActive() {

    }

    @Override
    public int getForwardButtonText() {
        if(transactionType == TransactionActivity.TRANSACTION_TYPE_SEND) {
            return R.string.transaction_send;
        } else {
            return R.string.transaction_request;
        }
    }

    @Override
    public int getForwardButtonIcon() {
        if(transactionType == TransactionActivity.TRANSACTION_TYPE_SEND) {
            return R.drawable.baseline_arrow_upward_black_24;
        } else {
            return R.drawable.baseline_arrow_downward_black_24;
        }
    }

    @Override
    public void onNextButtonPressed() {
        getActivity().finish();
    }
}
