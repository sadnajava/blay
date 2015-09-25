package com.squeaker.squeaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SqueakArrayAdapter extends ArrayAdapter<SqueakMetadata> {
    private final Context context;
    private final int resource;
    private final SessionId sid;
    private final ArrayList<SqueakMetadata> squeaks;

    final LayoutInflater inflater;

    public SqueakArrayAdapter(Context context, int resource, SessionId sid, ArrayList<SqueakMetadata> squeaks) {
        super(context, resource, squeaks);
        this.context = context;
        this.resource = resource;
        this.sid = sid;
        this.squeaks = squeaks;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SqueakMetadata squeak = squeaks.get(position);

        final View rowView = inflater.inflate(resource, parent, false);
        final TextView squeakCaption = (TextView) rowView.findViewById(R.id.squeakCaption);
        final TextView squeakDuration = (TextView) rowView.findViewById(R.id.squeakDuration);
        final TextView squeakDate = (TextView) rowView.findViewById(R.id.squeakDate);
        final TextView squeakAuthor = (TextView) rowView.findViewById(R.id.squeakAuthor);
        final ImageButton squeakPlayButton = (ImageButton) rowView.findViewById(R.id.squeakPlayButton);

        squeakCaption.setText(squeak.getCaption());

        String formattedDuration = (new SimpleDateFormat("mm:ss").format(new Date(squeak.getDuration())));
        squeakDuration.setText(formattedDuration);

        squeakDate.setText(squeak.getDate());
        squeakAuthor.setText(squeak.getEmail());

        squeakPlayButton.setOnClickListener(new FetchPlaySqueakOnClickListener(sid, squeak.getSqueakId()));

        return rowView;
    }
}
