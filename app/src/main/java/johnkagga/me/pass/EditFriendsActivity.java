package johnkagga.me.pass;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class EditFriendsActivity extends AppCompatActivity {

    private static final String LOG_TAG = EditFriendsActivity.class.getSimpleName();

    protected ListView mListView;
    protected List<ParseUser> mUsers;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.username_list);
        mProgressBar = (ProgressBar) findViewById(R.id.edit_friends_progressBar);

        //Select the clicked item
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mProgressBar.setVisibility(View.VISIBLE);
        //Query for users
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(ParseConstants.USER_LIMIT);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    mUsers = users;
                    //Get the usernames
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditFriendsActivity.this,
                            android.R.layout.simple_list_item_checked, usernames);
                    mListView.setAdapter(adapter);

                } else {
                    Log.i(LOG_TAG, e.getMessage());
                    String title = "Sorry";
                    String message = e.getMessage();
                    Helper.alertDialog(EditFriendsActivity.this, title, message);
                }
            }
        });
    }

}
