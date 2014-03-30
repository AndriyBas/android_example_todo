package com.oyster.DBandContentProviderEx;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * @author bamboo
 * @since 3/30/14 2:57 PM
 */
public class SignUpFragment extends Fragment {

    private EditText mEditTextUsername;
    private EditText mEditTextFullName;
    private EditText mEditTextPassword;
    private EditText mEditTextEmail;

    private Button mButtonSignUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sign_up_fragment, container, false);

        init(v);

        bindViews();

        return v;
    }

    /**
     * set listeners on SignIn and LogIn buttons
     */
    private void bindViews() {

        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String username = mEditTextUsername.getText().toString();
                final String fulName = mEditTextFullName.getText().toString();
                final String password = mEditTextPassword.getText().toString();
                final String email = mEditTextEmail.getText().toString();

                boolean validationError = false;
                StringBuilder validationErrorMsg = new StringBuilder("Please enter");

                // check if username is not empty (1)
                if (isEmpty(username)) {
                    validationError = true;
                    validationErrorMsg.append(" a username");
                }

                // check if password is not empty (2)
                if (isEmpty(password)) {
                    if (validationError)
                        validationErrorMsg.append(", and");
                    validationError = true;
                    validationErrorMsg.append(" a password");
                }

                // check if fullName is not empty (3)
                if (isEmpty(fulName)) {
                    if (validationError)
                        validationErrorMsg.append(", and");
                    validationError = true;
                    validationErrorMsg.append(" a full name");
                }

                // check if fullName is not empty (3)
                if (isEmpty(email)) {
                    if (validationError)
                        validationErrorMsg.append(", and");
                    validationError = true;
                    validationErrorMsg.append(" an email");
                }

                validationErrorMsg.append(" !");

                // show message if (1) or (2) and return, login failed
                if (validationError) {
                    onSignUpError(validationErrorMsg.toString());
                    return;
                }

                // Dialog to show while logging in in the background
                final ProgressDialog dlg = new ProgressDialog(getActivity());
                dlg.setTitle("Signing up ...");
                dlg.setMessage("Signing up. Please, wait");
                dlg.show();

                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.put("fullName", fulName);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (dlg != null && dlg.isShowing()) {
                            dlg.dismiss();
                        }
                        if (e == null) {
                            startMainActivity();
                        } else {
                            onSignUpError(e.getMessage());
                        }
                    }
                });
            }
        });
    }

    // Show Toast message on login failure
    private void onSignUpError(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG)
                .show();
    }

    /**
     * Start MainActivity on successful login
     */
    private void startMainActivity() {
        Intent i = new Intent(getActivity(), TodoMainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    /**
     * Initialize Views from the layout inflated into v
     *
     * @param v View where the layout is inflated
     */
    private void init(View v) {

        mEditTextUsername = (EditText) v.findViewById(R.id.signup_username);
        mEditTextFullName = (EditText) v.findViewById(R.id.signup_full_name);
        mEditTextPassword = (EditText) v.findViewById(R.id.signup_password);
        mEditTextEmail = (EditText) v.findViewById(R.id.signup_email);


        mButtonSignUp = (Button) v.findViewById(R.id.signup_btnSignUp);
    }

    /**
     * Check if current String consists at least of 1 alphanumeric character
     *
     * @param s String to check
     * @return true if String contains at lest 1 alphanumeric character
     */
    private boolean isEmpty(String s) {
        return s.trim().length() == 0;
    }
}