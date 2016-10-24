package com.jodelXposed;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.jodelXposed.models.Location;
import com.jodelXposed.utils.AppCompatPreferenceActivity;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Picker;
import com.mypopsy.maps.StaticMap;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Created by Admin on 14.10.2016.
 */

@SuppressWarnings("deprecation")
public class JXPreferenceActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    final Options options = Options.INSTANCE;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        Location location = options.getLocation();
        setContentView(R.layout.layout_jx_prefs);
        StaticMap map = new StaticMap().marker(location.getLat(),location.getLng()).size(1280, 480);
        ImageView ivMap = (ImageView) findViewById(R.id.ivMap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_back_button);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("");
        try {
            Picasso.with(this).load(String.valueOf(map.toURL().toURI())).placeholder(R.drawable.progress_animation).into(ivMap);
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }


        addPreferencesFromResource(R.xml.jx_prefs);
        findPreference("switch_location").setOnPreferenceChangeListener(this);
        findPreference("change_location").setOnPreferenceClickListener(this);

        ((SwitchPreference)findPreference("switch_location")).setChecked(location.getActive());
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case "change_location":
                startActivity(new Intent(this, Picker.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("choice",1));
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()){
            case "switch_location":
                if(newValue instanceof Boolean){
                    options.getLocation().setActive((Boolean)newValue);
                }
                options.save();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
