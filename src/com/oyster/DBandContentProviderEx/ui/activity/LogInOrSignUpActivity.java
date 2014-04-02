package com.oyster.DBandContentProviderEx.ui.activity;

import android.app.Fragment;
import com.oyster.DBandContentProviderEx.ui.fragment.LogInOrSignUpFragment;
import com.oyster.DBandContentProviderEx.utils.SingleFragmentActivity;

/**
 * @author bamboo
 * @since 3/30/14 12:58 PM
 */
public class LogInOrSignUpActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new LogInOrSignUpFragment();
    }

}