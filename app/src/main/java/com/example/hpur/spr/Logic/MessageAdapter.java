package com.example.hpur.spr.Logic;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hpur.spr.R;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<ChatBubble> {
    private final String TAG = "MessageAdapter:";
    private Activity activity;
    private List<ChatBubble> messages;

    public MessageAdapter(Activity context, int resource, List<ChatBubble> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource = 0; // determined by view type
        ChatBubble chatBubble = getItem(position);
//        int viewType = getItemViewType(position);

        if (chatBubble.getmMyMessage()) {
            Log.d(TAG,"right chat bubble layout");
            layoutResource = R.layout.right_chat_bubble;
        } else {
            Log.d(TAG,"left chat bubble layout");
            layoutResource = R.layout.left_chat_bubble;
        }

        convertView = inflater.inflate(layoutResource, parent, false);
        holder = new ViewHolder(convertView);
        convertView.setTag(holder);


//        if (convertView != null) {
//            Log.d(TAG,"converview != null");
//            holder = (ViewHolder) convertView.getTag();
//        } else {
//            Log.d(TAG,"else");
//            convertView = inflater.inflate(layoutResource, parent, false);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);
//        }

        //set message content
        holder.msg.setText(chatBubble.getmTextMessage());

        return convertView;
    }

//    @Override
//    public int getViewTypeCount() {
//        // return the total number of view types. this value should never change
//        // at runtime. Value 2 is returned because of left and right views.
//        return 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        // return a value between 0 and (getViewTypeCount - 1)
//        return position % 2;
//    }

    private class ViewHolder {
        private TextView msg;

        public ViewHolder(View v) {
            msg = (TextView) v.findViewById(R.id.txt_msg);
        }
    }
}
