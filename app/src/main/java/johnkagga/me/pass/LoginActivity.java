package johnkagga.me.pass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    protected TextView mSignUpText;
    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected ProgressBar mLoginBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeScreen();

        mLoginBar.setVisibility(View.INVISIBLE);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                //Remove any space in case the user accidentally added it
                username = username.trim();
                password = password.trim();

                if (username.isEmpty() || password.isEmpty()) {
                    String title = getString(R.string.signup_title_dialog);
                    String message = getString(R.string.login_error_message);
                    //Dialog
                    Helper.alertDialog(LoginActivity.this, title, message);
                }
                else {
                    //Login user
                    //make progress bar visible
                    mLoginBar.setVisibility(View.VISIBLE);
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            //hide the progress bar
                            mLoginBar.setVisibility(View.INVISIBLE);
                            if (e == null)
                            {
                                //successfully logged in
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                String title = getString(R.string.sin_in_error_title);
                                String message = e.getMessage();

                                Helper.alertDialog(LoginActivity.this,title,message);
                            }
                        }
                    });
                }
            }
        });

        startSignUpActivity();

    }

    /**
     * Start the sign up activity by an intent
     */
    private void startSignUpActivity() {
        mSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initialize the widgets
     */
    private void initializeScreen() {
        mSignUpText = (TextView) findViewById(R.id.signupText);
        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginBar = (ProgressBar) findViewById(R.id.login_progress_bar);
    }

}
