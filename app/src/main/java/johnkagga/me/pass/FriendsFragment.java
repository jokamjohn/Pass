package johnkagga.me.pass;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by jokamjohn on 12/21/2015.
 */
public class FriendsFragment extends ListFragment {

    private static final String LOG_TAG = FriendsFragment.class.getSimpleName();
    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mUserParseRelation;
    protected ParseUser mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Get current user
        mCurrentUser = ParseUser.getCurrentUser();
        //Get the parse relation
        mUserParseRelation = mCurrentUser.getRelation(ParseConstants.KEY_USER_RELATION);
        //Query for the user`s friends
        ParseQuery<ParseUser> query = mUserParseRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> friends, ParseException e) {
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_list_item_1, usernames);
                        setListAdapter(adapter);
                    } else {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
            });
    }
}
