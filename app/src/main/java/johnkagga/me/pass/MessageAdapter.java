package johnkagga.me.pass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by jokamjohn on 12/25/2015.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter(Context context,List<ParseObject> messages) {
        super(context, R.layout.message_list_item, messages);
        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //ViewHolder for smooth scrolling
        ViewHolder holder;

        if (convertView == null) {
            //inflate the view
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject message = mMessages.get(position);

        if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.IMAGE_TYPE)) {
            holder.mIcon.setImageResource(R.drawable.ic_image_black_36dp);
        }
        else {
            holder.mIcon.setImageResource(R.drawable.ic_videocam_black_36dp);
        }
        holder.mLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));

        return convertView;
    }

    public class ViewHolder{
        public final ImageView mIcon;
        public final TextView mLabel;

        public ViewHolder(View view){
            mIcon = (ImageView) view.findViewById(R.id.messageIcon);
            mLabel = (TextView) view.findViewById(R.id.senderName);
        }
    }
}
