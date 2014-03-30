package com.oyster.DBandContentProviderEx;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import com.parse.ParseAnalytics;

public class TodoMainActivity extends NavigationDrawerBaseActivity {

    public static final String TAG_DETAIL_FRAGMENT = "detail_fragment";

    public int getFragmentContainerId() {
        return R.id.navigation_drawer_fragment_container;
    }

    //    @Override
    public Fragment createFragment() {
        return new ToDoMainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // track how many times the app was opened, could be monitored on parse.com
        ParseAnalytics.trackAppOpened(getIntent());


        if (getFragmentContainerId() == R.id.navigation_drawer_fragment_container) {
            FragmentManager fm = getFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.navigation_drawer_fragment_container);
            if (fragment == null) {
                fm.beginTransaction()
                        .add(getFragmentContainerId(), createFragment())
                        .commit();
            }
        }
    }


/*
    public void onFragmentSuicide() {
        FragmentManager fm = getFragmentManager();
        while (fm.popBackStackImmediate()) ;

        fm.beginTransaction()
                .replace(R.id.fragmentContainer, createFragment())
                        // because
                .commit();
    }
*/

    @Override
    public void onBackPressed() {

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(TAG_DETAIL_FRAGMENT);

        if (fragment != null && fragment instanceof ToDoDetailFragment) {

            ToDoDetailFragment toDoDetailFragment = (ToDoDetailFragment) fragment;
            if (toDoDetailFragment.isToDoChanged()) {
                toDoDetailFragment.showSaveConfirmationDialog();
                return;
            }
        }
        super.onBackPressed();
    }
}
