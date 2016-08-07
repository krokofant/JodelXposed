package com.jodelXposed.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.jodelXposed.R;
import com.jodelXposed.models.Location;
import com.permissioneverywhere.PermissionEverywhere;
import com.permissioneverywhere.PermissionResponse;
import com.permissioneverywhere.PermissionResultCallback;
import com.schibstedspain.leku.LocationPickerActivity;

import static com.jodelXposed.utils.Utils.getSystemContext;


public class PlacePicker extends Activity {


    private static final int PERMISSION_REQUEST_CODE = 201;
    private static final int PLACEPICKER_REQUEST_CODE = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Location location = Options.getInstance().getLocationObject();

        switch (getIntent().getIntExtra("choice",0)){
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !permissionsGranted()) {
                    PermissionEverywhere.getPermission(getApplicationContext(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE,
                        "JodelXposed",
                        "This app needs a write permission",
                        R.mipmap.ic_launcher)
                        .enqueue(new PermissionResultCallback() {
                            @Override
                            public void onComplete(PermissionResponse permissionResponse) {
                                Toast.makeText(getSystemContext(), "is Granted " + permissionResponse.isGranted(), Toast.LENGTH_LONG).show();
                            }
                        });

                } else{
                    Intent intent = new Intent(this, LocationPickerActivity.class);
                    intent.putExtra(LocationPickerActivity.LATITUDE, location.getLat());
                    intent.putExtra(LocationPickerActivity.LONGITUDE, location.getLng());
                    startActivityForResult(intent, PLACEPICKER_REQUEST_CODE);
                }
                break;
            case 2:
                Options.getInstance().resetLocation();
                finish();
                break;
            default:
                finish();
        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    private boolean permissionsGranted(){
        return this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                finish();
            }
        }
    }
}

