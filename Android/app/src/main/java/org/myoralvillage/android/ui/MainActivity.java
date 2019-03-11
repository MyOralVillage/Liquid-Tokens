package org.myoralvillage.android.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.MenuItem;

import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.history.HistoryFragment;
import org.myoralvillage.android.ui.request.RequestFragment;
import org.myoralvillage.android.ui.scan.ScanFragment;
import org.myoralvillage.android.ui.send.SendFragment;
import org.myoralvillage.android.ui.transaction.TransactionsFragment;
import org.myoralvillage.android.ui.user.UserFragment;

public class MainActivity extends AppCompatActivity {

    private final Fragment mFragmentTransactions = TransactionsFragment.newInstance();
    private final Fragment mFragmentScan = ScanFragment.newInstance();
    private final Fragment mFragmentHistory = HistoryFragment.newInstance();
    private final Fragment mFragmentUser = UserFragment.newInstance();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_transactions:
                    switchFragment(mFragmentTransactions);
                    return true;
                case R.id.navigation_scan:
                    switchFragment(mFragmentScan);
                    return true;
                case R.id.navigation_history:
                    switchFragment(mFragmentHistory);
                    return true;
                case R.id.navigation_user:
                    switchFragment(mFragmentUser);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            Log.d("T2", "getInstanceId failed", task.getException());
                            return;
                        }

                        String token = task.getResult().getToken();

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                                child("users").child(userId).child("messagingToken");
                        if(!ref.toString().equals(token)) {
                            ref.setValue(token);
                        }
                    }

                });

        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_transactions);
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
    }

}