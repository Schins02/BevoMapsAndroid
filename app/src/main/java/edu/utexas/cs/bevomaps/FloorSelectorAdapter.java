package edu.utexas.cs.bevomaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by johnschindler on 4/5/15.
 */
public class FloorSelectorAdapter extends ArrayAdapter<String> {

    private ListView listView;
    private final Context context;
    private final String[] floors;


    public FloorSelectorAdapter(Context context, String[] floors, ListView listView) {
        super(context, R.layout.floor_selector_cell, floors);
        this.context = context;
        this.listView = listView;
        this.floors = floors;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.floor_selector_cell, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        textView.setText(floors[position]);
        return rowView;
    }

}