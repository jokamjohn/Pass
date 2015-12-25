package johnkagga.me.pass;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by jokamjohn on 12/21/2015.
 */
public class InboxFragment extends ListFragment {

    private static final String LOG_TAG = InboxFragment.class.getSimpleName();
    protected List<ParseObject> mMessages;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //Get the file type
        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        //Getting the file
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if (messageType.equals(ParseConstants.IMAGE_TYPE))
        {
            //View Image
            Intent imageIntent = new Intent(getActivity(),ViewImageActivity.class);
            imageIntent.setData(fileUri);
            startActivity(imageIntent);
        }
        else {
            //View Video
            Intent intent = new Intent(Intent.ACTION_VIEW,fileUri);
            intent.setDataAndType(fileUri,"video/*");
            startActivity(intent);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        //Find the messages that belong to the user
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGE);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENTS_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if (e == null)
                {
                    //success
                    mMessages = messages;
                    String[] senderName = new String[messages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages)
                    {
                        senderName[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }

                    MessageAdapter adapter = new MessageAdapter(getActivity(),mMessages);
                    setListAdapter(adapter);
                }
                else {
                    //Error
                    Log.v(LOG_TAG,e.getMessage());
                }
            }
        });
    }
}
