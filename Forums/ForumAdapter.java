package com.example.weshare.Forums;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.weshare.R;

import java.util.ArrayList;

public class ForumAdapter extends ArrayAdapter<Forum> {
    private Context context;
    private ArrayList<Forum> list = new ArrayList<>();

    public ForumAdapter(Context context, ArrayList<Forum> forumList )
    {
        super(context, R.layout.list_view, forumList);
        this.context = context;
        list = forumList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String title = getItem(position).getTitle();
        String description = getItem(position).getDescription();
        String uId = getItem(position).getuId();
        String type = getItem(position).getType();
        int positionType = getItem(position).getPositionType();
        double lat = getItem(position).getLat();
        double lon = getItem(position).getLon();
        String user = getItem(position).getUsername();
        boolean anonymity = getItem(position).getAnonymity();

        Forum forum = new Forum(title, description, uId, type, positionType, lat, lon, user, anonymity);

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_view, parent, false);

        TextView titleDisplay = (TextView)convertView.findViewById(R.id.forumTitle);
        TextView descriptionDisplay = (TextView)convertView.findViewById(R.id.forumDescription);
        TextView userDisplay = (TextView)convertView.findViewById(R.id.forumUser);

        titleDisplay.setText(forum.getTitle());
        descriptionDisplay.setText(forum.getDescription());
        if( anonymity )
            userDisplay.setText("By: Anonymous");
        else
            userDisplay.setText("By: " + forum.getUsername());

        return convertView;
    }
}
