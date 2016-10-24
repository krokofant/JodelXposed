package com.jodelXposed.hooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.FileObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jodelXposed.models.Hookvalues;
import com.jodelXposed.utils.Options;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.R.layout.simple_list_item_1;
import static android.os.FileObserver.CLOSE_WRITE;
import static com.jodelXposed.utils.Bitmap.loadBitmap;
import static com.jodelXposed.utils.Utils.getActivity;
import static com.jodelXposed.utils.Utils.getIdentifierById;
import static com.jodelXposed.utils.Utils.getNewIntent;
import static com.jodelXposed.utils.Utils.getSystemContext;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findMethodsByExactParameters;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class ImageStuff {
    public static boolean imageShared = false;

    /**
     * Add features on ImageView - load custom stored image, adjust ScaleType
     * Remove blur effect
     */
    public ImageStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        Hookvalues hooks = Options.INSTANCE.getHooks();
        findAndHookMethod(hooks.Class_PhotoEditFragment, lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {

                if (imageShared) {
                    loadImage(loadBitmap(), param);
                    imageShared = false;
                }

                final Activity activity = getActivity(param);

                final int id = getIdentifierById(param, "save_to_gallery_button");

                final Button addImage = new Button(activity);
                addImage.setText("Replace\nImage");
                addImage.setTextColor(Color.BLACK);
                addImage.setBackgroundColor(Color.TRANSPARENT);

                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, simple_list_item_1, new String[]{"Shared Image", "Gallery"});
                        new AlertDialog.Builder(activity).setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        loadImage(loadBitmap(), param);
                                        break;
                                    case 1:
                                        getSystemContext().startActivity(getNewIntent("utils.Picker").putExtra("choice", 3));
                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).show();
                    }
                });

                /*
                * Apply layout params
                * */
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, id);
                params.addRule(RelativeLayout.ALIGN_BOTTOM);
                addImage.setLayoutParams(params);
                RelativeLayout relativeLayout = (RelativeLayout) ((View) param.getResult()).findViewById(id).getParent();
                relativeLayout.addView(addImage);

                /*
                * Start file observer to react on a PictureChoosenEvent
                * */
                new FileObserver(com.jodelXposed.utils.Bitmap.jodelImagePath, CLOSE_WRITE) {
                    @Override
                    public void onEvent(int i, String s) {
                        loadImage(loadBitmap(), param);
                    }
                }.startWatching();
            }
        });

        Class<?> JodelImageHelper = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.utilities.JodelImageHelper", lpparam.classLoader);
        Method[] methods = findMethodsByExactParameters(JodelImageHelper, Bitmap.class, Context.class, Bitmap.class);
        findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.JodelImageHelper", lpparam.classLoader, methods[0].getName(), Context.class, Bitmap.class, new XC_MethodReplacement() {
            @Override
            protected Bitmap replaceHookedMethod(MethodHookParam param) throws Throwable {
                return (Bitmap) param.args[1];
            }
        });

    }

    private void loadImage(final Bitmap bitmap, XC_MethodHook.MethodHookParam param) {
        final ImageView a = (ImageView) getObjectField(param.thisObject, Options.INSTANCE.getHooks().ImageHookValues_ImageView);
        ((Activity) callMethod(param.thisObject, "getActivity"))
            .runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    a.setImageBitmap(bitmap);
                    a.requestFocus();
                }
            });
    }
}
