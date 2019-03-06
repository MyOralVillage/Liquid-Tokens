package org.myoralvillage.android.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;

import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.history.HistoryFragment;
import org.myoralvillage.android.ui.request.RequestFragment;
import org.myoralvillage.android.ui.scan.ScanFragment;
import org.myoralvillage.android.ui.send.SendFragment;
import org.myoralvillage.android.ui.transaction.TransactionsFragment;

public class MainActivity extends AppCompatActivity {

    private final Fragment mFragmentTransactions = TransactionsFragment.newInstance();
    private final Fragment mFragmentScan = ScanFragment.newInstance();
    private final Fragment mFragmentHistory = HistoryFragment.newInstance();

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
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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