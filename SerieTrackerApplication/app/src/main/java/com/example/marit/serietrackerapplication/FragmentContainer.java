package com.example.marit.serietrackerapplication;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class FragmentContainer extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_series:
                    SeriesOverviewFragment fragment = new SeriesOverviewFragment();
                    fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                    return true;
                case R.id.navigation_users:
                    // TO-DO dit fixen
                    UsersOverviewFragment fragmentusers = new UsersOverviewFragment();
                    fragmentTransaction.replace(R.id.fragment_container, fragmentusers).addToBackStack(null).commit();
                    return true;
                case R.id.navigation_profile:
                    LoggedInUserProfileFragment loggedinfragment = new LoggedInUserProfileFragment();
                    fragmentTransaction.replace(R.id.fragment_container, loggedinfragment).addToBackStack(null).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        // Initialize the bottom navigation
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Initialize the fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        SeriesOverviewFragment fragment = new SeriesOverviewFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, "SeriesOverview");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }


}
