package com.jodelXposed.hooks;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Bitmap.loadBitmap;
import static com.jodelXposed.utils.Log.xlog;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class ImageStuff {
    private static class PhotoEditFragment {
        static String Bitmap = "aAo";
        static String ImageView = "aAh";
        static String Method = "BK";
    }

    private static class JodelImageHelper {
        static String Bitmap = "a";
    }

    /**
     * Add features on ImageView - load custom stored image, adjust ScaleType
     * Remove blur effect
     */
    public ImageStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.PhotoEditFragment", lpparam.classLoader, PhotoEditFragment.Method, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final boolean[] isInjected = {false};
                final Bitmap original = (Bitmap) getObjectField(param.thisObject, PhotoEditFragment.Bitmap);
                ImageView a = (ImageView) getObjectField(param.thisObject, PhotoEditFragment.ImageView);

                a.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        xlog("Long clicked!");
                        Bitmap b;
                        if (isInjected[0]) {
                            b = original;
                            isInjected[0] = false;
                        } else {
                            b = loadBitmap();
                            isInjected[0] = true;
                        }
                        ((ImageView) v).setImageBitmap(b);
                        return true;
                    }
                });

                xlog("Adding click listener to imageView");
                a.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP &&
                            (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                                keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
                            return true;
                        }

                        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                            ImageView iv = (ImageView) v;
                            ImageView.ScaleType sT;
                            switch (iv.getScaleType()) {
                                case CENTER:
                                    sT = ImageView.ScaleType.FIT_CENTER;
                                    break;
                                case FIT_CENTER:
                                    sT = ImageView.ScaleType.CENTER;
                                    break;
                                default:
                                    sT = ImageView.ScaleType.CENTER;
                            }
                            xlog("ScaleType set to " + sT.toString());
                            iv.setScaleType(sT);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                // Set focus on the ImageView
                a.requestFocus();
            }
        });

        findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.JodelImageHelper", lpparam.classLoader, JodelImageHelper.Bitmap, Context.class, Bitmap.class, new XC_MethodReplacement() {
            @Override
            protected Bitmap replaceHookedMethod(MethodHookParam param) throws Throwable {
                return (Bitmap) param.args[1];
            }
        });
    }
}
