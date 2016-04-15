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
 * Created by Admin on 14.04.2016.
 */
public class SpinnerAdapterUuid extends ArrayAdapter<UUID> {

    private Context context;
    private List<UUID> uuids;
    LayoutInflater inflater;

    public SpinnerAdapterUuid(Context context, int textViewResourceId,List<UUID> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.uuids = values;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount(){
        return uuids.size();
    }

    public UUID getItem(int position){
        return uuids.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_entry, parent, false);

        TextView name        = (TextView)row.findViewById(R.id.textView);
        ImageButton delete = (ImageButton) row.findViewById(R.id.imageButton);
        delete.setVisibility(View.INVISIBLE);
        UUID u = uuids.get(position);
        name.setText(u.getName());
        return row;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_entry, parent, false);
        final UUID u = uuids.get(position);
        TextView name        = (TextView)row.findViewById(R.id.textView);
        ImageButton delete = (ImageButton) row.findViewById(R.id.imageButton);
        delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID.delete(u);
                assert SettingsActivity.uuid != null;
                SettingsActivity.uuid.notifyDataSetChanged();
            }
        });
        name.setText(u.getName()+" ("+u.getUUID()+")");
        return row;
    }
}
