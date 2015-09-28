package com.squeaker.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squeaker.squeaker.R;
import com.squeaker.squeaker.UserMetadata;

import java.util.ArrayList;

public class UserMetadataArrayAdapter extends ArrayAdapter<UserMetadata> {
    private final Context context;
    private final int resource;
    private final ArrayList<UserMetadata> users;

    final LayoutInflater inflater;

    public UserMetadataArrayAdapter(Context context, int resource, ArrayList<UserMetadata> users) {
        super(context, resource, users);
        this.context = context;
        this.resource = resource;
        this.users = users;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UserMetadata userMetadata = users.get(position);

        final View rowView = inflater.inflate(resource, parent, false);
        final TextView userName = (TextView) rowView.findViewById(R.id.userName);
        final TextView numSqueaks = (TextView) rowView.findViewById(R.id.numSqueaks);

        userName.setText(userMetadata.getEmail());
        numSqueaks.setText(String.valueOf(userMetadata.getNumSqueaks()));

        return rowView;
    }
}
