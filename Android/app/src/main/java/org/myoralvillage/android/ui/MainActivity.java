package org.myoralvillage.android.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.myoralvillage.android.R;
import org.myoralvillage.android.ui.history.HistoryFragment;
import org.myoralvillage.android.ui.request.RequestFragment;
import org.myoralvillage.android.ui.scan.ScanFragment;
import org.myoralvillage.android.ui.send.SendFragment;

public class MainActivity extends AppCompatActivity {

    private final Fragment mFragmentSend = SendFragment.newInstance();
    private final Fragment mFragmentRequest = RequestFragment.newInstance();
    private final Fragment mFragmentScan = ScanFragment.newInstance();
    private final Fragment mFragmentHistory = HistoryFragment.newInstance();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_send:
                    switchFragment(mFragmentSend);
                    return true;
                case R.id.navigation_request:
                    switchFragment(mFragmentRequest);
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
        navigation.setSelectedItemId(R.id.navigation_send);
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
    }

}
