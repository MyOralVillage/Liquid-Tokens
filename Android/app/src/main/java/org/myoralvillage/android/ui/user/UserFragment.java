package org.myoralvillage.android.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.mynameismidori.currencypicker.ExtendedCurrency;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.CurrentUserViewModel;
import org.myoralvillage.android.ui.auth.LoginActivity;
import org.myoralvillage.android.ui.contacts.ContactsActivity;
import org.myoralvillage.android.ui.widgets.ContactCard;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment {

    //Firebase
    private static final String TAG = "UserFragment";

    //private CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");

    private CircleImageView pictureCardImage;
    private TextView userNameText;
    private TextView userPhoneText;
    private TextView userCurrencyText;

    private ImageView userCurrencyImage;

    private MaterialCardView contactsButton;
    private MaterialCardView editProfileButton;
    private Button logoutButton;

    public UserFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        bindViews(view);
        addListeners();
        observeData();

        return view;
    }

    private void bindViews(View view) {
        pictureCardImage = view.findViewById(R.id.user_image);
        userNameText = view.findViewById(R.id.user_text_name);
        userPhoneText = view.findViewById(R.id.user_text_phone);
        userCurrencyText = view.findViewById(R.id.user_text_currency);
        userCurrencyImage = view.findViewById(R.id.user_icon_currency);
        contactsButton = view.findViewById(R.id.user_button_contacts);
        editProfileButton = view.findViewById(R.id.user_button_edit);
        logoutButton = view.findViewById(R.id.user_button_logout);
    }

    private void addListeners() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ContactsActivity.class);
                startActivity(intent);
            }
        });
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void observeData() {
        if(getActivity() == null) {
            throw new IllegalStateException("Tried to get ViewModel on null activity");
        }

        CurrentUserViewModel viewModel = ViewModelProviders.of(getActivity()).get(CurrentUserViewModel.class);
        viewModel.getCurrentUser().observe(this, new Observer<MOVUser>() {
            @Override
            public void onChanged(MOVUser movUser) {
                userNameText.setText(movUser.getName());
                userPhoneText.setText(movUser.getPhone());

                ContactCard.setUserImage(getContext(), movUser, pictureCardImage);

                ExtendedCurrency currency = MOVCurrency.getExtendedCurrencyByIso(movUser.getCurrency().toLowerCase());

                userCurrencyImage.setImageResource(currency.getFlag());
                userCurrencyText.setText(currency.getName());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}