package com.jodelXposed.utils;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.jodelXposed.utils.Log.xlog;

public final class Bitmap {
    public static String jodelImagePath = Environment.getExternalStorageDirectory() + "/.jodel-input.jpg";

    public static android.graphics.Bitmap loadBitmap() {
        xlog("Loading bitmap image");
        File file = new File(jodelImagePath);
        return file.exists() ? bitmapFromFile(file) : bitmapFromFile(new File(jodelImagePath));
    }

    public static void saveBitmap(android.graphics.Bitmap bitmap) {
        xlog("Saving bitmap of size: " + bitmap.getByteCount());

        try {
            FileOutputStream fos = new FileOutputStream(new File(jodelImagePath));
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            xlog("File not found: " + e.getLocalizedMessage());
        } catch (IOException e) {
            xlog("Error accessing file: " + e.getLocalizedMessage());
        }
    }

    public static android.graphics.Bitmap bitmapFromFile(File file) {
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        android.graphics.Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (bitmap != null) bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, blob);
        else xlog("Loaded bitmap is null");
        return bitmap;
    }

    public static android.graphics.Bitmap toGrayscale(android.graphics.Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        android.graphics.Bitmap bmpGrayscale = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}
