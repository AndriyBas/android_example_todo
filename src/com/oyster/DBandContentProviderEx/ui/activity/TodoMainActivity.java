package com.oyster.DBandContentProviderEx.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import com.oyster.DBandContentProviderEx.R;
import com.oyster.DBandContentProviderEx.services.ToDoParseUploadService;
import com.oyster.DBandContentProviderEx.ui.fragment.ToDoDetailFragment;
import com.oyster.DBandContentProviderEx.ui.fragment.ToDoMainFragment;
import com.oyster.DBandContentProviderEx.utils.NavigationDrawerBaseActivity;

public class TodoMainActivity extends NavigationDrawerBaseActivity
        implements ToDoDetailFragment.OnSuicideListener {

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

    @Override
    public void onFragmentSuicide() {
//        FragmentManager fm = getFragmentManager();
//        while (fm.popBackStackImmediate()) ;
//
//        fm.beginTransaction()
//                .replace(R.id.fragmentContainer, createFragment())
//                .commit();
    }

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

    public void synchronizeWithServer() {
        // synchronize with server
        Intent i = new Intent(
                ToDoParseUploadService.ACTION_FETCH_NEW_ITEMS, // action
                null,                                          // uri
                this,                                 // context
                ToDoParseUploadService.class);                 // Service
        startService(i);
    }
}
