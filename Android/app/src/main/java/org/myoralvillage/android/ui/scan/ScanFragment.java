package org.myoralvillage.android.ui.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.transaction.TransactionActivity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static android.app.Activity.RESULT_OK;


public class ScanFragment extends Fragment {

    private static final int REQUEST_SCAN_QR = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Fragment createFragment;

    public ScanFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance() {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "");
        args.putString(ARG_PARAM2, "");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan,
                container, false);

        //listener for create_qr_button
        Button create_button = view.findViewById(R.id.create_qr_button);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
                createFragment = CreateQR.newInstance();

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction().addToBackStack(null);

                ft.replace(R.id.container, createFragment).commit();
            }
        });

        //listener for scan_qr_button
        Button scan_button = view.findViewById(R.id.scan_qr_button);
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), CameraScanActivity.class);
                startActivityForResult(intent, REQUEST_SCAN_QR);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_SCAN_QR && resultCode == RESULT_OK && data != null) {
            String contactUid = data.getStringExtra(CameraScanActivity.EXTRA_CONTACT_UID);

            Intent intent = new Intent(getContext(), TransactionActivity.class);
            intent.putExtra(TransactionActivity.EXTRA_TRANSACTION_SEND_TO, contactUid);
            intent.putExtra(TransactionActivity.EXTRA_TRANSACTION_TYPE, TransactionActivity.TRANSACTION_TYPE_SEND);

            startActivity(intent);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //call CreateQR fragment instance to insure QRcode dissapears
        if (createFragment != null){
            createFragment.onDetach();
        }

    }
}
