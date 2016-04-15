package com.jodelXposed.charliekelly.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jodelXposed.R;

import java.util.List;

/**
 * Created by Admin on 13.04.2016.
 */
public class SpinnerAdapterLocation extends ArrayAdapter<Location> {

    LayoutInflater inflater;
    private List<Location> locations;

    public SpinnerAdapterLocation(Context context, int textViewResourceId, List<Location> values) {
        super(context, textViewResourceId, values);
        this.locations = values;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount(){
        return locations.size();
    }

    public Location getItem(int position){
        return locations.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.spinner_entry, parent, false);

        TextView name        = (TextView)row.findViewById(R.id.textView);
        ImageButton delete = (ImageButton) row.findViewById(R.id.imageButton);
        delete.setVisibility(View.INVISIBLE);
        Location l = locations.get(position);
        name.setText(l.getCity());
//        SettingsActivity.notifyDataSetChanged();
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_entry, parent, false);
        final Location l = locations.get(position);
        TextView name        = (TextView)row.findViewById(R.id.textView);
        ImageButton delete = (ImageButton) row.findViewById(R.id.imageButton);
        delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location.delete(l);
                SettingsActivity.locationList = Location.listAll(Location.class);
//                SettingsActivity.notifyDataSetChanged();
            }
        });
        if(position==0){
            name.setText(l.getCity()+" ("+l.getLat()+" / "+l.getLng()+")");
        }
        else
        {
            name.setText(l.getCity()+" ("+l.getLat()+" / "+l.getLng()+")");
        }
        return row;
    }
}
