package johnkagga.me.pass;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class RecipientActivity extends AppCompatActivity {

    private static final String LOG_TAG = FriendsFragment.class.getSimpleName();

    protected ProgressBar mProgressBar;
    protected ListView mFriendsList;
    protected FloatingActionButton fab;

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
        mFriendsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mProgressBar = (ProgressBar) findViewById(R.id.recipient_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mFriendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //check for how many items are selected
                if (mFriendsList.getCheckedItemCount() > 0)
                {
                    fab.setVisibility(View.VISIBLE);
                }
                else {
                    fab.setVisibility(View.INVISIBLE);
                }
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create the ParseObject and send it
                ParseObject message = createMessage();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Creating a message
     *
     * @return ParseObject Message
     */
    private ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGE);
        message.put(ParseConstants.KEY_SENDER_ID,mCurrentUser.getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, mCurrentUser.getUsername());
        message.put(ParseConstants.KEY_RECIPIENTS_IDS, getRecipientIds());
        return message;
    }

    /**
     * Getting the list of recipients
     *
     * @return ArrayList of Recipients
     */
    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientsIds = new ArrayList<>();
        //loop through items in the list
        for (int i = 0; i < mFriendsList.getCount(); i++)
        {
            if (mFriendsList.isItemChecked(i))
            {
                //checked whether the item is checked at that position
                //get the objectId
                recipientsIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientsIds;
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
