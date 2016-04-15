package com.jodelXposed.charliekelly.activities;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jodelXposed.R;

import java.util.List;

/**
 * Created by Admin on 13.04.2016.
 */
public class SpinnerAdapter extends ArrayAdapter<Location> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private List<Location> locations;
    LayoutInflater inflater;

    public SpinnerAdapter(Context context, int textViewResourceId,List<Location> values) {
        super(context, textViewResourceId, values);
        this.context = context;
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


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.spinner_entry, parent, false);

        TextView name        = (TextView)row.findViewById(R.id.textView);
        ImageButton delete = (ImageButton) row.findViewById(R.id.imageButton);
        delete.setVisibility(View.INVISIBLE);
        Location l = locations.get(position);
        name.setText(l.getCity());
        return row;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
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
                notifyDataSetChanged();
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
