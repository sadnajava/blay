package com.squeaker.squeaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserArrayAdapter extends ArrayAdapter<User> {
    private final Context context;
    private final int resource;
    private final ArrayList<User> users;

    final LayoutInflater inflater;

    public UserArrayAdapter(Context context, int resource, ArrayList<User> users) {
        super(context, resource, users);
        this.context = context;
        this.resource = resource;
        this.users = users;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = users.get(position);

        final View rowView = inflater.inflate(resource, parent, false);
        final TextView userName = (TextView) rowView.findViewById(R.id.userName);
        final TextView numSqueaks = (TextView) rowView.findViewById(R.id.numSqueaks);

        userName.setText(user.getEmail());
        numSqueaks.setText(String.valueOf(user.getNumSqueaks()));

        return rowView;
    }
}
