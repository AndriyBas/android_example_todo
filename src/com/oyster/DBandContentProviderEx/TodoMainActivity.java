package com.oyster.DBandContentProviderEx;

import android.app.Fragment;
import android.app.FragmentManager;

public class TodoMainActivity extends SingleFragmentActivity
        implements ToDoDetailFragment.OnSuicideListener {

    private ToDoMainFragment mToDoMainFragment;

    @Override
    public Fragment createFragment() {
        if (mToDoMainFragment == null) {
            mToDoMainFragment = new ToDoMainFragment();
        }
        return mToDoMainFragment;
    }

    @Override
    public void onFragmentSuicide() {

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragmentContainer, createFragment())
                        // because
                .commit();
    }
}
