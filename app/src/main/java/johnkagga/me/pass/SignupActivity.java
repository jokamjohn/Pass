package johnkagga.me.pass;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignUpButton;
    protected EditText mConfirmPassword;

    private String possibleEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUsername = (EditText) findViewById(R.id.signup_username);
        mPassword = (EditText) findViewById(R.id.signup_password);
        mEmail = (EditText) findViewById(R.id.signup_email);
        mConfirmPassword = (EditText) findViewById(R.id.re_enter_password);
        mSignUpButton = (Button) findViewById(R.id.signup_button);

        settingDefaultUserEmail();

//        mConfirmPassword.addTextChangedListener(new TextWatcher() {
//        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();
                String confirmedEmail = mConfirmPassword.getText().toString();

                //Remove any space in case the user accidentally added it
                username = username.trim();
                password = password.trim();
                email = email.trim();
                confirmedEmail = confirmedEmail.trim();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || confirmedEmail.isEmpty()) {
                    String title = getString(R.string.signup_title_dialog);
                    String message = getString(R.string.signup_dialog_msg);
                    //Dialog
                    Helper.alertDialog(SignupActivity.this, title, message);


                } else if (!isEmailValid(email)) {
                    Toast.makeText(SignupActivity.this, R.string.signup_invalid_email_error, Toast.LENGTH_LONG)
                            .show();
                } else if (!isPasswordMatching(password, confirmedEmail)) {
                    String errorMessage = getString(R.string.password_does_not_match);
                    String title = getString(R.string.password_error_title);
                    //Dialog
                    Helper.alertDialog(SignupActivity.this, title, errorMessage);
                } else {
                    //create user
                    ParseUser parseUser = new ParseUser();
                    parseUser.setUsername(username);
                    parseUser.setEmail(email);
                    parseUser.setPassword(password);

                    parseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //Proceed to the main activity
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                String title = getString(R.string.sign_up_error_title);
                                String message = e.getMessage();
                                //Show the error in the dialog
                                Helper.alertDialog(SignupActivity.this, title, message);
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     * Setting the User`s default Account email
     */
    private void settingDefaultUserEmail() {
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts)
        {
            if (isEmailValid(account.name))
            {
                possibleEmail = account.name;
            }
        }
        mEmail.setText(possibleEmail);
    }

    /**
     * Validating an email address
     * @param email Email Address
     * @return Boolean
     */
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Confirming the user`s password
     * @param password User password
     * @param passwordConfirmation Confirmation password
     * @return boolean
     */
    boolean isPasswordMatching(String password, String passwordConfirmation)
    {
        Pattern pattern = Pattern.compile(password);
        Matcher matcher = pattern.matcher(passwordConfirmation);
        return matcher.matches();
    }

}
