package com.jodelXposed.activities;


import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.pedrovgs.lynx.LynxActivity;
import com.github.pedrovgs.lynx.LynxConfig;
import com.jodelXposed.R;
import com.jodelXposed.models.Beta;
import com.jodelXposed.models.Location;
import com.jodelXposed.models.UDI;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Picker;

public class SettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private SwitchPreference locationSwitch;
    private SwitchPreference udiSwitch;
    private Location location;
    private Options options;
    private UDI udi;
    private SwitchPreference betaSwitch;
    private Beta beta;
    private EditTextPreference editUdi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();

        location = Options.getInstance().getLocationObject();
        options = Options.getInstance();
        udi = Options.getInstance().getUDIObject();
        beta = Options.getInstance().getBetaObject();

        addPreferencesFromResource(R.xml.pref_general);


        locationSwitch = (SwitchPreference) findPreference("switch_location");
        udiSwitch = (SwitchPreference) findPreference("switch_udi");
        betaSwitch = (SwitchPreference) findPreference("switch_beta");
        editUdi = (EditTextPreference) findPreference("button_edit_udi");

        findPreference("button_choose_location").setOnPreferenceClickListener(this);
        locationSwitch.setOnPreferenceChangeListener(this);
        udiSwitch.setOnPreferenceChangeListener(this);
        betaSwitch.setOnPreferenceChangeListener(this);

        editUdi.setOnPreferenceClickListener(this);

        findPreference("button_logcat").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LynxConfig lynxConfig = new LynxConfig();
                lynxConfig.setMaxNumberOfTracesToShow(4000).setFilter("Xposed");

                Intent lynxActivityIntent = LynxActivity.getIntent(SettingsActivity.this, lynxConfig);
                startActivity(lynxActivityIntent);
                return true;
            }
        });

        updateFieldsFromSettings();
    }

    private void setupToolbar() {
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
        root.addView(toolbar, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateFieldsFromSettings();
    }

    private void updateFieldsFromSettings() {
        locationSwitch.setChecked(location.isActive());
        locationSwitch.setSummary("Current location: " + location.getCity());
        udiSwitch.setChecked(udi.isActive());
        betaSwitch.setChecked(beta.isActive());
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

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        switch (preference.getKey()) {
            case "switch_location":
                if (location.getLat() == 0 && location.getLng() == 0) {
                    locationSwitch.setChecked(false);
                    Toast.makeText(SettingsActivity.this, "Please choose a location first", Toast.LENGTH_SHORT).show();
                } else {
                    location.setActive((boolean) o);
                    options.save();
                }
                return true;
            case "switch_udi":
                udi.setActive((boolean) o);
                options.save();
                return true;
            case "switch_beta":
                beta.setActive((boolean) o);
                options.save();
                return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "button_choose_location":
                startActivity(new Intent(getApplicationContext(), Picker.class).putExtra("choice", 1));
                return true;
            case "button_edit_udi":
//                View v = getLayoutInflater().inflate(R.layout.dialog_udi, null);
//                Spinner udiSpinner = (Spinner) v.findViewById(R.id.udiSpinner);
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, (String[]) udi.getUdis().keySet().toArray());
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                udiSpinner.setAdapter(adapter);
//
//                new AlertDialog.Builder(this).setTitle("Choose your UDI").setView(v).show();
                break;
        }
        return false;
    }
}
