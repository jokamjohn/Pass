package johnkagga.me.pass;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends AppCompatActivity {

    private static final String LOG_TAG = EditFriendsActivity.class.getSimpleName();

    protected ListView mListView;
    protected List<ParseUser> mUsers;
    protected ProgressBar mProgressBar;
    protected ParseRelation<ParseUser> mUserParseRelation;
    protected ParseUser mCurrentUser;

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

        //When an item is selected
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Add relation and save
                mUserParseRelation.add(mUsers.get(position));
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null)
                        {
                            //error occurred
                            Log.e(LOG_TAG,e.getMessage());
                        }
                    }
                });
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Get current data at all times
        //Get current user
        mCurrentUser = ParseUser.getCurrentUser();
        //Add friends to the current user
        mUserParseRelation = mCurrentUser.getRelation(ParseConstants.KEY_USER_RELATION);

        //Show progress bar
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
                    String title = getString(R.string.query_friends_dialog_title);
                    String message = e.getMessage();
                    //Make an Alert Dialog
                    Helper.alertDialog(EditFriendsActivity.this, title, message);
                }
            }
        });
    }


}
