package johnkagga.me.pass;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

public class RecipientActivity extends AppCompatActivity {

    private static final String LOG_TAG = FriendsFragment.class.getSimpleName();

    protected ProgressBar mProgressBar;
    protected ListView mFriendsList;

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mUserParseRelation;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFriendsList = (ListView) findViewById(android.R.id.list);
        mProgressBar = (ProgressBar) findViewById(R.id.recipient_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Get current user
        mCurrentUser = ParseUser.getCurrentUser();
        //Get the parse relation
        mUserParseRelation = mCurrentUser.getRelation(ParseConstants.KEY_USER_RELATION);
        //Query for the user`s friends

        //Show progress bar
        mProgressBar.setVisibility(View.VISIBLE);
        ParseQuery<ParseUser> query = mUserParseRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    mFriends = friends;
                    //Add the usernames of the friends in an array
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser friend : mFriends) {
                        usernames[i] = friend.getUsername();
                        i++;
                    }

                    //Make an ArrayAdapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RecipientActivity.this,
                            android.R.layout.simple_list_item_checked, usernames);
                    mFriendsList.setAdapter(adapter);
                } else {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        });
    }

}
