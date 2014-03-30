package com.oyster.DBandContentProviderEx;

import android.app.Fragment;

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