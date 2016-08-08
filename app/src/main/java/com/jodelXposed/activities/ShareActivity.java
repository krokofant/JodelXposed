package com.jodelXposed.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.jodelXposed.R;
import com.jodelXposed.utils.Utils;

import java.io.IOException;

import static com.jodelXposed.utils.Bitmap.saveBitmap;
import static com.jodelXposed.utils.Log.xlog;

public class ShareActivity extends Activity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shareactivity);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                verifyStoragePermissions();
            }
        } else {
            xlog("What the fudge is this? (I should only accept images)");
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        xlog("Got shared image URI " + imageUri.getPath());

        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                saveBitmap(bitmap);
            } catch (IOException e) {
                xlog("Error accessing file: " + e.getLocalizedMessage());
            }
        }
        Utils.openApp(this, "com.tellm.android.app");
        finish();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        xlog("onRequestPermissionResult enter");
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    xlog("Permission granted");
                    handleSendImage(getIntent());
                } else {
                    xlog("Permission not granted");
                    Toast.makeText(this, "Failed to get permissions",
                        Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    /**
     * Verify permissions
     */
    public void verifyStoragePermissions() {
        xlog("Verify permission storage entered");

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            xlog("Prompting user for permission");
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            );
        } else {
            xlog("We have permission");
            handleSendImage(getIntent());
        }
    }
}
