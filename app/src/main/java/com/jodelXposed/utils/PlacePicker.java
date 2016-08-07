package com.jodelXposed.utils;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.jodelXposed.models.Location;
import com.schibstedspain.leku.LocationPickerActivity;


public class PlacePicker extends Activity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Location location = Options.getInstance().getLocationObject();

        switch (getIntent().getIntExtra("choice",0)){
            case 1:
                Intent intent = new Intent(this, LocationPickerActivity.class);
                intent.putExtra(LocationPickerActivity.LATITUDE, location.getLat());
                intent.putExtra(LocationPickerActivity.LONGITUDE, location.getLng());
                startActivityForResult(intent,200);
                break;
            case 2:
                Options.getInstance().resetLocation();
                finish();
                break;
            default:
                finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
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

