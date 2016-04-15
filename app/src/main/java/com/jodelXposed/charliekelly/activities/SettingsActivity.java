package com.jodelXposed.charliekelly.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.jodelXposed.R;
import com.jodelXposed.charliekelly.asynctasks.GeocoderAsync;
import com.jodelXposed.krokofant.utils.Settings;
import com.orm.SugarContext;
import com.pixplicity.easyprefs.library.Prefs;
import com.spazedog.lib.rootfw4.RootFW;
import com.spazedog.lib.rootfw4.utils.Device;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jodelXposed.krokofant.utils.Log.xlog;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, GeocoderAsync.OnGeoListener{

    private static final int REQUEST_CODE_PERMISSIONS = 200;
    public static String currentlocation = null;
    static Boolean isTouched = false;
    static List<Location> locationList;
    static List<UUID> uuidList;
    private static SpinnerAdapterLocation spinnerAdapterLocation;
    private static SpinnerAdapterUuid spinnerAdapterUuid;
    AppCompatSpinner spinnerUuid, spinnerLocations;
    private int PLACEPICKER_REQUEST = 0;
    private Settings mSettings = Settings.getInstance();
    private SwitchCompat chkIsActive;
    private ActionProcessButton btnSetPosition, btnSetUuid, btnPickLocation, btnAddUuid;
    private Button btnResetUuid, btnResetLocation, btnRestartJodel;

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                throw new PackageManager.NameNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SugarContext.init(this);
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        if (!Prefs.getBoolean("firstRun", false)) {
            new Location(mSettings.getLat(), mSettings.getLng(), mSettings.getCity(), mSettings.getCountry(), mSettings.getCountryCode()).save();
            //TODO PUT DEFAULT UUID IN DATABASE
            new UUID("UUIDPLACEHOLDER", "NAMEPLACEHOLDER").save();
            Prefs.putBoolean("firstRun", true);
        }

        setSupportActionBar((Toolbar) findViewById(R.id.tool_bar));
        init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) checkPermissions();

        try{
            mSettings.load();
        } catch (JSONException | IOException e){
            xlog(e.getMessage());
        }
        setInformation();

        RootFW.connect();
    }

    private void setOnClickListener() {
        chkIsActive.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isTouched = true;
                return false;
            }
        });
        chkIsActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    mSettings.setActive(chkIsActive.isChecked());
                    Log.d("isactive", String.valueOf(chkIsActive.isChecked()));
                    mSettings.save();
                    setInformation();
//                    Toast.makeText(getApplicationContext(), "Saved settings to file\n" + mSettings.toJson(), Toast.LENGTH_LONG).show();
                } catch (JSONException | IOException e) {
                    xlog(e.getMessage());
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setInformation(){
        TextView tvLat = (TextView) findViewById(R.id.tvLat);
        TextView tvLng = (TextView) findViewById(R.id.tvLng);
        TextView tvCity = (TextView) findViewById(R.id.tvCity);
        TextView tvCountry = (TextView) findViewById(R.id.tvCountry);
        TextView tvCountrycode = (TextView) findViewById(R.id.tvCountrycode);
        assert tvLat != null;
        tvLat.setText("Lat: "+String.valueOf(mSettings.getLat()));
        assert tvLng != null;
        tvLng.setText("Lng: "+String.valueOf(mSettings.getLng()));
        assert tvCity != null;
        tvCity.setText("City: " + String.valueOf(mSettings.getCity()));
        assert tvCountry != null;
        tvCountry.setText("Country: "+String.valueOf(mSettings.getCountry()));
        assert tvCountrycode != null;
        tvCountrycode.setText("Countrycode: "+String.valueOf(mSettings.getCountryCode()));
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == this.btnSetPosition.getId()) {
            Location l = (Location) spinnerLocations.getSelectedItem();
            setSettingsFromPlace(l.getLat(), l.getLng());
        } else if (viewID == this.btnSetUuid.getId()) {
            UUID u = (UUID) spinnerUuid.getSelectedItem();
            //TODO CALL SET UUID METHOD WITH UUID u
        } else if (viewID == this.btnResetLocation.getId()) {
            mSettings.createDefaultFile(new File(Settings.settingsPath));
            setInformation();
        } else if(viewID == this.btnResetUuid.getId()){
            UUID u = UUID.findById(UUID.class, 1);
            //TODO RESET UUID TO u
        } else if (viewID == this.btnPickLocation.getId()) {
            this.pickLocation();
        } else if (viewID == this.btnAddUuid.getId()) {
            addUuid(R.layout.load_diag);
        } else if (viewID == btnRestartJodel.getId()) {
            Device.Process process = RootFW.getProcess("com.tellm.android.app");
            if (process.kill())
                openApp(getApplicationContext(), "com.tellm.android.app");
            Log.d("Killed", "Jodel!");
        }
    }

    private void init() {
        this.btnSetPosition = (ActionProcessButton) findViewById(R.id.btn_select_position);
        this.btnSetUuid = (ActionProcessButton) findViewById(R.id.btn_set_uuid);
        this.btnPickLocation = (ActionProcessButton) findViewById(R.id.btn_add_location);
        this.btnAddUuid = (ActionProcessButton) findViewById(R.id.btn_add_uuid);
        this.btnResetLocation = (Button) findViewById(R.id.btn_reset_location);
        this.btnResetUuid = (Button) findViewById(R.id.btn_reset_uuid);

        this.btnSetPosition.setOnClickListener(this);
        this.btnSetUuid.setOnClickListener(this);
        this.btnResetUuid.setOnClickListener(this);
        this.btnResetLocation.setOnClickListener(this);
        this.btnAddUuid.setOnClickListener(this);
        this.btnPickLocation.setOnClickListener(this);

        locationList = Location.listAll(Location.class);
        spinnerAdapterLocation = new SpinnerAdapterLocation(this, R.layout.spinner_entry, locationList);
        spinnerLocations = (AppCompatSpinner) findViewById(R.id.spinnerLocation);
        assert spinnerLocations != null;
        spinnerLocations.setAdapter(spinnerAdapterLocation);

        uuidList = UUID.listAll(UUID.class);
        spinnerAdapterUuid = new SpinnerAdapterUuid(this, R.layout.spinner_entry, uuidList);
        spinnerUuid = (AppCompatSpinner) findViewById(R.id.spinnerUuid);
        assert spinnerUuid != null;
        spinnerUuid.setAdapter(spinnerAdapterUuid);

        this.btnSetPosition.setMode(ActionProcessButton.Mode.ENDLESS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem tbSwitch = menu.findItem(R.id.toggleservice);
        MenuItem button = menu.findItem(R.id.toggleservice);
        this.chkIsActive = (SwitchCompat) MenuItemCompat.getActionView(tbSwitch);
        this.btnRestartJodel = (Button) MenuItemCompat.getActionView(button);
        this.btnRestartJodel.setTextColor(Color.WHITE);
        this.btnRestartJodel.setOnClickListener(this);
        chkIsActive.setChecked(mSettings.isActive());
        setOnClickListener();
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions(){
        List<String> permissions = new ArrayList<>();
        if( checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.WRITE_EXTERNAL_STORAGE );
        }

        if( !permissions.isEmpty() ) {
            requestPermissions( permissions.toArray( new String[permissions.size()] ), REQUEST_CODE_PERMISSIONS );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch ( requestCode ) {
            case REQUEST_CODE_PERMISSIONS: {
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                        setInformation();
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                        //noinspection ConstantConditions
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.scrollview), "Permission denied, this app wont work properly!", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Check that", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //open up app settings page
                                        startActivity(
                                                new Intent().setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                        .setData(Uri.fromParts("package", getApplicationContext().getPackageName(), null)));
                                    }
                                });
                        snackbar.show();
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    /**
     * Open a Place picker, zoomed in on coordinates from settings
     */
    private void pickLocation(){
        Log.d("pickLocation", "started");
        //Start place picker with these coordinates in the center
        double lat = mSettings.getLat();
        double lng = mSettings.getLng();

        xlog(String.format("Loaded latlng from Settings:Lat: %s, Lng: %s", lat, lng));

        PlacePicker.IntentBuilder i = new PlacePicker.IntentBuilder();
        i.setLatLngBounds(new LatLngBounds(
                new LatLng((lat - 0.20), (lng - 0.20)),
                new LatLng((lat + 0.20), (lng + 0.20))
        ));


        try{
            xlog("Opening maps app");
            //Intent in = i.build((Activity)context);
            startActivityForResult(i.build(this), PLACEPICKER_REQUEST);
        }
        catch(Exception ex){
            xlog("Error opening maps:");
            xlog(ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        xlog("onActivityResult requestcode: " + requestCode);

        if(resultCode != RESULT_OK) {
            xlog("Error, resultCode: " + String.valueOf(resultCode));
            return;
        }

        if(requestCode == PLACEPICKER_REQUEST){
            xlog("Recieved data from placepicker activity");

            if(data == null){
                xlog("data was null");
                return;
            }
            this.btnSetPosition.setProgress(1);
            Place place = PlacePicker.getPlace(this, data);
            this.setSettingsFromPlace(place);

        }
    }

    private void setSettingsFromPlace(double lat, double lng) {
        mSettings.setLat(lat);
        mSettings.setLng(lng);
        new GeocoderAsync(lat, lng, this, this).execute();
    }

    private void setSettingsFromPlace(Place place) {
        LatLng latlng = place.getLatLng();
        double lat = latlng.latitude;
        double lng = latlng.longitude;

        mSettings.setLat(lat);
        mSettings.setLng(lng);

        //Get location data on seperate thread, then call onGeoFinished
        new GeocoderAsync(lat, lng, this, this).execute();
    }

    @Override
    public void onGeoFinished(List<Address> addresses) {
        if(addresses == null){
            Toast.makeText(this, "No addresses nearby, unable to set location", Toast.LENGTH_SHORT).show();
            this.btnSetPosition.setProgress(-1); //error in geofetching
            resetProgress();
            return;
        }

        int length = addresses.size();
        boolean save = false;

        //Find an adress that has all needed indexes
        for(int i = 0; i < length; i++){
            final Address a = addresses.get(i);
            String locality = a.getLocality();
            final String country = a.getCountryName();
            final String countryCode = a.getCountryCode();

            if(locality == null){
                xlog("Locality was null");
                continue;
            }

            if(country == null){
                xlog("Country was null");
                continue;
            }

            if(countryCode == null){
                xlog("CountryCode was null");
                continue;
            }

            mSettings.setCity(locality);
            mSettings.setCountry(country);
            mSettings.setCountryCode(countryCode);
            save = true;
            setInformation();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Save location?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Location l = new Location();
                    l.setCity(a.getLocality());
                    l.setCountry(country);
                    l.setCountryCode(countryCode);
                    l.setLat(a.getLatitude());
                    l.setLng(a.getLongitude());
                    l.save();
                }
            }).setNegativeButton("No",null).show();
            setInformation();
            this.btnSetPosition.setProgress(100);
            resetProgress();
            //noinspection ConstantConditions
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.scrollview), "For changes to take effect please restart Jodel!", Snackbar.LENGTH_LONG)
                    .setAction("Okay", null);
            snackbar.show();
            break;
        }

        //If we found an adress with all indexes, save to file
        if(save){
            try {
                mSettings.save();
                Toast.makeText(getApplicationContext(), "Location saved", Toast.LENGTH_SHORT).show();
            } catch (JSONException | IOException e) {
                xlog(e.getLocalizedMessage());
            }

        }else{
            Toast.makeText(getApplicationContext(), "Could not save location, try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetProgress(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnSetPosition.setProgress(0); //reset progress after 2,5sec
            }
        }, 2500);
    }

    private void addUuid(int layout) {
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialog);
        final TextInputEditText etUuid = (TextInputEditText) dialog.findViewById(R.id.editTextDiagUuid);
        final TextInputEditText etName = (TextInputEditText) dialog.findViewById(R.id.editTextDiagName);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!etName.getEditableText().toString().trim().isEmpty() && !etUuid.getEditableText().toString().trim().isEmpty()) {
                    new UUID(etUuid.getEditableText().toString().trim(), etName.getEditableText().toString().trim()).save();
                    notifyDataSetChanged();
                    Toast.makeText(SettingsActivity.this, "Saved UUID", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(SettingsActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", null).setTitle("Change UUID").setMessage("Please insert new name and correspondending UUID");
        builder.show();
    }

    public boolean notifyDataSetChanged() {
        //TODO THIS IS NOT WOKING AT ALL, AND I FOUND NO WORKING WORKAROUND
        uuidList = UUID.listAll(UUID.class);
        locationList = Location.listAll(Location.class);
        spinnerAdapterLocation = new SpinnerAdapterLocation(getApplicationContext(), R.layout.spinner_entry, locationList);
        spinnerLocations.setAdapter(spinnerAdapterLocation);
        spinnerAdapterLocation.notifyDataSetChanged();
        spinnerAdapterUuid = new SpinnerAdapterUuid(this, R.layout.spinner_entry, uuidList);
        spinnerUuid.setAdapter(spinnerAdapterUuid);
        spinnerAdapterUuid.notifyDataSetChanged();
        return true;
    }
}
