package com.oyster.DBandContentProviderEx.ui.activity;

import android.app.Fragment;
import com.oyster.DBandContentProviderEx.ui.fragment.SignUpFragment;
import com.oyster.DBandContentProviderEx.utils.SingleFragmentActivity;

/**
 * @author bamboo
 * @since 3/30/14 1:02 PM
 */
public class SignUpActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new SignUpFragment();
    }
}