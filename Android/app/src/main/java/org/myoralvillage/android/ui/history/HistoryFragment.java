package org.myoralvillage.android.ui.history;

import android.content.Context;
import android.os.Bundle;
import android.os.TransactionTooLargeException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.data.transaction.MOVTransaction;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class HistoryFragment extends Fragment {

    //Firebase
    private static final String TAG = "HistoryFragment";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ListView mListView;
    private String UID;

    public HistoryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        UID = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG,"onAuthStateChanged:History:Logged_In: "+user.getUid());
                    toastMessage("Signed in with: "+user.getUid());
                }
                else{
                    Log.d(TAG,"onAuthStateChanged:History:Logged_out: "+user.getUid());
                    toastMessage("Signed out with: "+user.getUid());
                }
            }
        };
        myRef.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mListView = view.findViewById(R.id.history_listview);

        return view;
    }

    /*
        showData accesses Firebase and reads through each transaction. When it finds a transaction
        that was sent by UID or received by UID it adds it to an arrayList that is displayed later.

     */
    private void showData(DataSnapshot dataSnapshot) {
        DataSnapshot transSnap = dataSnapshot.child("transactions");
        ArrayList<String> array = new ArrayList<>();

        //for each transaction
        for(DataSnapshot ds: transSnap.getChildren()){
            Log.d(TAG,(""+ds.getValue(MOVTransaction.class)));
            MOVTransaction trans = ds.getValue(MOVTransaction.class);

            if(trans.getTo().equals(UID)|| trans.getFrom().equals(UID)){
                if(trans.getTo_name() == null){
                    trans.setTo_name(dataSnapshot.child("users").child(trans.getTo())
                            .getValue(MOVUser.class).getName());
                }
                if(trans.getFrom_name() == null){
                    trans.setFrom_name(dataSnapshot.child("users").child(trans.getFrom())
                            .getValue(MOVUser.class).getName());
                }
                array.add(""+trans);
            }
        }
        ListAdapter myAdapt = new ArrayAdapter<String>(getContext(),android.R.layout
                            .simple_list_item_1,array);

        mListView.setAdapter(myAdapt);
    }

    /*
        Given a String s,
        Make a toast message with that string.
     */
    private void toastMessage(String s) {
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
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