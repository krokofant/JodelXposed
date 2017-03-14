package com.jodelXposed.hooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.FileObserver;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jodelXposed.utils.Log;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Utils;
import com.jodelXposed.utils.XposedUtilHelpers;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.os.FileObserver.CLOSE_WRITE;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static com.jodelXposed.utils.Bitmap.loadBitmap;
import static com.jodelXposed.utils.Utils.getActivity;
import static com.jodelXposed.utils.Utils.getJXSharedImage;
import static com.jodelXposed.utils.Utils.getNewIntent;
import static com.jodelXposed.utils.Utils.getSystemContext;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class ColorAndGalleryPicker {

    /**
     * Add features on ImageView - load custom stored image, adjust ScaleType
     * Remove blur effect
     */
    public ColorAndGalleryPicker(final XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.CreateTextPostFragment", lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws IllegalAccessException {
                setupGalleryPicker(param, lpparam);
                setupColorPicker(param);
            }
        });
    }

    private void setupGalleryPicker(final XC_MethodHook.MethodHookParam param, final XC_LoadPackage.LoadPackageParam lpparam) {
        final FileObserver imageFileObserver = new FileObserver(getJXSharedImage(), CLOSE_WRITE) {
            @Override
            public void onEvent(int i, String s) {
                Log.dlog("File Observer issued, loading image");
                this.stopWatching();
                Log.dlog("Image loading, FileObserver stopped!");

                Object eventBus = getObjectField(param.thisObject, "bus");

                Class PictureTakenEvent = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.events.PictureTakenEvent", lpparam.classLoader);
                Object pictureTakenEvent = XposedHelpers.newInstance(PictureTakenEvent, loadBitmap());

                callMethod(eventBus, Options.INSTANCE.getHooks().Method_Otto_Append_Bus_Event, pictureTakenEvent);
            }
        };

        (((View) param.getResult()).findViewWithTag("gallery_button"))
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageFileObserver.startWatching();
                    getSystemContext().startActivity(getNewIntent("utils.Picker").putExtra("choice", 3));
                }
            });
    }

    private String findColorField(XC_MethodHook.MethodHookParam param) {
        String colorField = null;
        for (Field f : param.thisObject.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getType().getName().equals(String.class.getName()) && f.isAccessible()) {
                String field = null;
                try {
                    field = (String) f.get(param.thisObject);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (field != null && field.contains("#")) {
                    colorField = f.getName();
                    break;
                }
            }
        }
        return colorField;
    }

    private void setupColorPicker(final XC_MethodHook.MethodHookParam param) throws IllegalAccessException {
        final Activity activity = getActivity(param);

        final View create_post_layout = ((View) param.getResult()).findViewById(activity.getResources().getIdentifier("create_post_layout", "id", "com.tellm.android.app"));

        (((View) param.getResult()).findViewWithTag("color_chooser"))
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    View dialoglayout = getColorPickerView();

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Pick your desired color");
                    builder.setView(dialoglayout);

                    AlertDialog alertDialog = builder.create();

                    ColorPickerOnClickListener colorPickerOnClickListener = new ColorPickerOnClickListener(create_post_layout, param, findColorField(param), alertDialog);

                    dialoglayout.findViewWithTag("cp_orange").setOnClickListener(colorPickerOnClickListener);
                    dialoglayout.findViewWithTag("cp_yellow").setOnClickListener(colorPickerOnClickListener);
                    dialoglayout.findViewWithTag("cp_red").setOnClickListener(colorPickerOnClickListener);
                    dialoglayout.findViewWithTag("cp_blue").setOnClickListener(colorPickerOnClickListener);
                    dialoglayout.findViewWithTag("cp_bluegrayish").setOnClickListener(colorPickerOnClickListener);
                    dialoglayout.findViewWithTag("cp_green").setOnClickListener(colorPickerOnClickListener);

                    alertDialog.show();
                }
            });
    }

    private View getColorPickerView() {
        Context ctx = XposedUtilHelpers.getActivityFromActivityThread();

        LinearLayout.LayoutParams colorLayoutParams = new LinearLayout.LayoutParams(Utils.dpToPx(70), Utils.dpToPx(70));
        colorLayoutParams.setMargins(Utils.dpToPx(20), Utils.dpToPx(20), Utils.dpToPx(20), Utils.dpToPx(20));

        LinearLayout rootLayout = new LinearLayout(ctx);
        rootLayout.setOrientation(VERTICAL);
        LinearLayout.LayoutParams rootLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        rootLayout.setLayoutParams(rootLayoutParams);

        LinearLayout firstRow = new LinearLayout(ctx);
        firstRow.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams firstRowLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        firstRowLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        firstRow.setLayoutParams(firstRowLayoutParams);

        ImageView orange = new ImageView(ctx);
        orange.setTag("cp_orange");
        orange.setBackgroundColor(Color.parseColor("#FFFF9908"));
        orange.setLayoutParams(colorLayoutParams);
        firstRow.addView(orange);

        ImageView yellow = new ImageView(ctx);
        yellow.setTag("cp_yellow");
        yellow.setBackgroundColor(Color.parseColor("#FFFFBA00"));
        yellow.setLayoutParams(colorLayoutParams);
        firstRow.addView(yellow);

        ImageView red = new ImageView(ctx);
        red.setTag("cp_red");
        red.setBackgroundColor(Color.parseColor("#FFDD5F5F"));
        red.setLayoutParams(colorLayoutParams);
        firstRow.addView(red);
        rootLayout.addView(firstRow);

        LinearLayout secondRow = new LinearLayout(ctx);
        secondRow.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams secondRowLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        secondRowLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        secondRow.setLayoutParams(secondRowLayoutParams);

        ImageView blue = new ImageView(ctx);
        blue.setTag("cp_blue");
        blue.setBackgroundColor(Color.parseColor("#FF06A3CB"));
        blue.setLayoutParams(colorLayoutParams);
        secondRow.addView(blue);

        ImageView bluegrayish = new ImageView(ctx);
        bluegrayish.setTag("cp_bluegrayish");
        bluegrayish.setBackgroundColor(Color.parseColor("#FF8ABDB0"));
        bluegrayish.setLayoutParams(colorLayoutParams);
        secondRow.addView(bluegrayish);

        ImageView green = new ImageView(ctx);
        green.setTag("cp_green");
        green.setBackgroundColor(Color.parseColor("#FF9EC41C"));
        green.setLayoutParams(colorLayoutParams);
        secondRow.addView(green);
        rootLayout.addView(secondRow);

        return rootLayout;
    }

    private class ColorPickerOnClickListener implements View.OnClickListener {
        private View create_post_layout;
        private XC_MethodHook.MethodHookParam param;
        private String finalColorField;
        private AlertDialog alertDialog;

        ColorPickerOnClickListener(View view, XC_MethodHook.MethodHookParam param, String string, AlertDialog alertDialog) {
            this.create_post_layout = view;
            this.param = param;
            this.finalColorField = string;
            this.alertDialog = alertDialog;
        }

        @Override
        public void onClick(View v) {
            final String tag = (String) v.getTag();
            switch (tag) {
                case "cp_orange":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(0)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(0));
                    break;
                case "cp_yellow":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(1)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(1));
                    break;
                case "cp_red":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(2)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(2));
                    break;
                case "cp_blue":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(3)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(3));
                    break;
                case "cp_bluegrayish":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(4)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(4));
                    break;
                case "cp_green":
                    create_post_layout.setBackgroundColor(Color.parseColor(Utils.Colors.Colors.get(5)));
                    XposedHelpers.setObjectField(param.thisObject, finalColorField, Utils.Colors.Colors.get(5));
                    break;
            }
            alertDialog.dismiss();
        }
    }
}
