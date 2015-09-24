package com.squeaker.squeaker;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class SqueakListAdapter extends ArrayAdapter<SqueakMetadata> {
    public SqueakListAdapter(Context context, int resource, int textViewResourceId, ArrayList<SqueakMetadata> objects) {
        super(context, resource, textViewResourceId, objects);
    }
}
