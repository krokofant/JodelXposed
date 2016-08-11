package com.jodelXposed.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jodelXposed.models.Location;
import com.schibstedspain.leku.LocationPickerActivity;

import static com.jodelXposed.utils.Log.xlog;


public class PlacePicker extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 201;
    private static final int PLACEPICKER_REQUEST_CODE = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!permissionsGranted()){
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
            }
        }

        if (permissionsGranted()){
            getAction();
        }

    }

    private void getAction(){
        switch (getIntent().getIntExtra("choice",0)){
            case 1:
                startLocationPicker();
                break;
            case 2:
                Options.getInstance().resetLocation();
                finish();
                break;
            default:
                finish();
        }
    }

    private void startLocationPicker() {
        Location location = Options.getInstance().getLocationObject();
        Intent intent = new Intent(this, LocationPickerActivity.class);
        intent.putExtra(LocationPickerActivity.LATITUDE, location.getLat());
        intent.putExtra(LocationPickerActivity.LONGITUDE, location.getLng());
        startActivityForResult(intent, PLACEPICKER_REQUEST_CODE);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private boolean permissionsGranted(){
        return this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        xlog("ActivityResult called");
        if (requestCode == PLACEPICKER_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                Options op = Options.getInstance();

                op.getLocationObject().setLat(data.getDoubleExtra(LocationPickerActivity.LATITUDE, 0));
                op.getLocationObject().setLng(data.getDoubleExtra(LocationPickerActivity.LONGITUDE, 0));

                Address fullAddress = data.getParcelableExtra(LocationPickerActivity.ADDRESS);

                op.getLocationObject().setCity(fullAddress.getLocality());
                op.getLocationObject().setCountry(fullAddress.getCountryName());
                op.getLocationObject().setCountryCode(fullAddress.getCountryCode());

                op.save();
                Toast.makeText(PlacePicker.this, "Success, please refresh your feed!", Toast.LENGTH_LONG).show();

                finish();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                xlog("Cancelled");
                finish();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAction();
                }else{
                    Toast.makeText(getApplicationContext(), "This app needs permissions in order to work", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

