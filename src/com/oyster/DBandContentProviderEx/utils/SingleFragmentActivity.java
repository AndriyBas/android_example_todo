package com.oyster.DBandContentProviderEx.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * @author bamboo
 * @since 3/24/14 9:54 PM
 */
public abstract class SingleFragmentActivity extends Activity {


    public abstract Fragment createFragment();

    public int getLayoutId() {
//        return R.id.fragmentContainer;
        return android.R.id.content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        FrameLayout frameLayout = new FrameLayout(this);
//        frameLayout.setId(getLayoutId());
//        setContentView(frameLayout);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(getLayoutId());

        if (fragment == null) {
            fragment = createFragment();

            fragmentManager.beginTransaction()
                    .add(getLayoutId(), fragment)
                    .commit();
        }
    }
}
