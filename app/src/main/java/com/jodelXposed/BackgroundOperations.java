package com.jodelXposed;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.jodelXposed.charliekelly.asynctasks.GeocoderAsync;
import com.jodelXposed.krokofant.utils.Settings;
import com.spazedog.lib.rootfw4.RootFW;
import com.spazedog.lib.rootfw4.utils.Device;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.jodelXposed.krokofant.utils.Log.xlog;


public class BackgroundOperations extends AppCompatActivity implements GeocoderAsync.OnGeoListener {

    private int PLACEPICKER_REQUEST = 0;
    private Settings mSettings = Settings.getInstance();
    private static final int REQUEST_CODE_PERMISSIONS = 200;
    public static String currentlocation = null;
    private ProgressDialog barProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RootFW.connect();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }

        try {
            mSettings.load();
        } catch (JSONException | IOException e) {
            xlog(e.getMessage());
        }

        switch (getIntent().getIntExtra("choice",0)){
            case 1:
                pickLocation();
                break;
            case 2:
                mSettings.resetLocation();
                restartJodel();
                break;
            case 3:
                restartJodel();
                finish();
                break;
        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        List<String> permissions = new ArrayList<>();
        if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(BackgroundOperations.this, "Permission denied, this app wont work properly!", Toast.LENGTH_SHORT).show();
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
    private void pickLocation() {
        //Start place picker with these coordinates in the center
        double lat = mSettings.getLat();
        double lng = mSettings.getLng();

        xlog(String.format("Loaded latlng from Settings:Lat: %s, Lng: %s", lat, lng));

        PlacePicker.IntentBuilder i = new PlacePicker.IntentBuilder();
        i.setLatLngBounds(new LatLngBounds(
            new LatLng((lat - 0.20), (lng - 0.20)),
            new LatLng((lat + 0.20), (lng + 0.20))
        ));


        try {
            xlog("Opening maps app");
            //Intent in = i.build((Activity)context);
            startActivityForResult(i.build(this), PLACEPICKER_REQUEST);
        } catch (Exception ex) {
            xlog("Error opening maps:");
            xlog(ex.getMessage());
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("Fetching ...");
        barProgressDialog.setMessage("Fetching your location and restarting, please wait ...");
        barProgressDialog.setIndeterminate(true);
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        barProgressDialog.show();


        super.onActivityResult(requestCode, resultCode, data);
        xlog("onActivityResult requestcode: " + requestCode);

        if (resultCode != RESULT_OK) {
            xlog("Error, resultCode: " + String.valueOf(resultCode));
            barProgressDialog.dismiss();
            Toast.makeText(BackgroundOperations.this, "Error, please try again!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (requestCode == PLACEPICKER_REQUEST) {
            xlog("Recieved data from placepicker activity");

            if (data == null) {
                xlog("data was null");
                barProgressDialog.dismiss();
                Toast.makeText(BackgroundOperations.this, "Error, please try again!", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            Place place = PlacePicker.getPlace(this, data);
            this.setSettingsFromPlace(place);

        }
    }

    /**
     * Extract location properties from place object and insert to the Settings-object, then save settings to file
     */
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
        if (addresses == null) {
            Toast.makeText(this, "No addresses nearby, unable to set location", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int length = addresses.size();
        boolean save = false;

        //Find an adress that has all needed indexes
        for (int i = 0; i < length; i++) {
            Address a = addresses.get(i);
            String locality = a.getLocality();
            String country = a.getCountryName();
            String countryCode = a.getCountryCode();

            if (locality == null) {
                xlog("Locality was null");
                continue;
            }

            if (country == null) {
                xlog("Country was null");
                continue;
            }

            if (countryCode == null) {
                xlog("CountryCode was null");
                continue;
            }

            mSettings.setCity(locality);
            mSettings.setCountry(country);
            mSettings.setCountryCode(countryCode);
            save = true;
            Toast.makeText(BackgroundOperations.this, "Success!", Toast.LENGTH_LONG).show();
            break;
        }

        //If we found an adress with all indexes, save to file
        if (save) {
            try {
                mSettings.save();
                Toast.makeText(getApplicationContext(), "Location saved", Toast.LENGTH_SHORT).show();
            } catch (JSONException | IOException e) {
                xlog(e.getLocalizedMessage());
            }
            restartJodel();
            finish();

        } else {
            Toast.makeText(getApplicationContext(), "Could not save location, try again", Toast.LENGTH_SHORT).show();
        }
        barProgressDialog.dismiss();
        restartJodel();
        finish();
    }

    private void restartJodel(){
        Device.Process process = RootFW.getProcess("com.tellm.android.app");
        if (process.kill()){
            openApp(BackgroundOperations.this, "com.tellm.android.app");
            finish();
        }
    }


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
}

