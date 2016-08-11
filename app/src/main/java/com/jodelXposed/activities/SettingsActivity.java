package com.jodelXposed.activities;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.jodelXposed.R;
import com.jodelXposed.models.Location;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.PlacePicker;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private SwitchPreference locationSwitch;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
        root.addView(toolbar, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        location = Options.getInstance().getLocationObject();

        this.addPreferencesFromResource(R.xml.pref_general);

        locationSwitch = (SwitchPreference) findPreference("switch_location");

        locationSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                location.setActive(locationSwitch.isChecked());
                return false;
            }
        });

        Preference locationChooserButton = findPreference("button_choose_location");
        locationChooserButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getApplicationContext(), PlacePicker.class).putExtra("choice", 1));
                return false;
            }
        });

        updateFieldsFromSettings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateFieldsFromSettings();
    }

    private void updateFieldsFromSettings() {
        locationSwitch.setChecked(location.isActive());
        locationSwitch.setSummary("Current location: " + location.getCity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(0, 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
