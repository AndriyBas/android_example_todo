package com.oyster.DBandContentProviderEx;

import android.app.Fragment;
import android.app.FragmentManager;

public class TodoMainActivity extends SingleFragmentActivity
        implements ToDoDetailFragment.OnSuicideListener {

    public static final String TAG_DETAIL_FRAGMENT = "detail_fragment";

    @Override
    public Fragment createFragment() {
        return new ToDoMainFragment();
    }

    @Override
    public void onFragmentSuicide() {

        FragmentManager fm = getFragmentManager();
        while (fm.popBackStackImmediate()) ;

        fm.beginTransaction()
                .replace(R.id.fragmentContainer, createFragment())
                        // because
                .commit();
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(TAG_DETAIL_FRAGMENT);

        if (fragment != null) {

            ToDoDetailFragment toDoDetailFragment = (ToDoDetailFragment) fragment;
            if (toDoDetailFragment.isToDoChanged()) {
                toDoDetailFragment.showSaveConfirmationDialog();
                return;
            }
        }


        super.onBackPressed();
    }
}
