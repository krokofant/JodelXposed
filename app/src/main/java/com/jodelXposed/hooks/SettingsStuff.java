package com.jodelXposed.hooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AndroidAppHelper;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jodelXposed.models.Location;
import com.jodelXposed.utils.Options;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Utils.getSystemContext;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class SettingsStuff {
    private static class MyMenuItem {
        static String displayName = "aAj";
        static String RandomIntValue = "aAk";
    }

    private static class MyMenuFragment {
        static String AddEntriesMethod = "BD";
    }

    public SettingsStuff(final XC_LoadPackage.LoadPackageParam lpparam) {
        /*
         * Hook constructor of MyMenuItem to apply strings (Item names) which are not in strings.xml
         * Seamless integration #1
         */
        findAndHookConstructor("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader, Context.class, String.class, int.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                setObjectField(methodHookParam.thisObject, "name", methodHookParam.args[1]);
                try {
                    setObjectField(methodHookParam.thisObject, MyMenuItem.displayName, AndroidAppHelper.currentApplication().getResources().getString((int) methodHookParam.args[2]));
                } catch (Exception e) {
                    switch ((int) methodHookParam.args[2]) {
                        case 0:
                            setObjectField(methodHookParam.thisObject, MyMenuItem.displayName, "XGeneral");
                            break;
                        case 1:
                            setObjectField(methodHookParam.thisObject, MyMenuItem.displayName, "XLocation");
                            break;
                    }
                }
                setObjectField(methodHookParam.thisObject, MyMenuItem.RandomIntValue, -1);
                return null;
            }
        });

        /*
         * Add JodelXposed entries in ListView
         * Seamless integration #2
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.MyMenuFragment", lpparam.classLoader, MyMenuFragment.AddEntriesMethod, new XC_MethodHook() {
            @SuppressWarnings("unchecked")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> MyMenuItem = findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader);
                final Activity activity = (Activity) callMethod(param.thisObject, "getActivity");
                Object xposedLocationInformationItem = XposedHelpers.newInstance(MyMenuItem, activity, "xposedInfo", 0);
                Object xposedMapItem = XposedHelpers.newInstance(MyMenuItem, activity, "xposedMap", 1);
                ArrayAdapter myMenuItemArrayAdapter = (ArrayAdapter) XposedHelpers.callMethod(param.thisObject, "getListAdapter");
                myMenuItemArrayAdapter.add(xposedLocationInformationItem);
                myMenuItemArrayAdapter.add(xposedMapItem);
                myMenuItemArrayAdapter.notifyDataSetChanged();

            }
        });

        /*
         * Add JodelXposed entries in ListView - Handle clicks on Items
         * Seamless integration #3
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.MyMenuFragment", lpparam.classLoader, "onListItemClick", ListView.class, View.class, int.class, long.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object selected = ((ArrayAdapter) XposedHelpers.callMethod(param.thisObject, "getListAdapter")).getItem(((int) param.args[2]) - 1);
                final Options options = Options.getInstance();
                final Context context = getSystemContext();
                final Activity activity = (Activity) callMethod(param.thisObject, "getActivity");
                final Intent launchIntent = new Intent(Intent.ACTION_MAIN);
                launchIntent.setComponent(new ComponentName("com.jodelXposed", "com.jodelXposed.utils.PlacePicker"));
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (((String) getObjectField(selected, "name")).equalsIgnoreCase("xposedMap")) {
                    context.startActivity(launchIntent.putExtra("choice", 1));
                } else if (((String) getObjectField(selected, "name")).equalsIgnoreCase("xposedInfo")) {
                    openDialog(activity, options.getLocationObject(), launchIntent);
                }

            }
        });
    }
    private void openDialog(Activity activity, final Location location, final Intent launchIntent){
        Display display = activity.getWindowManager().getDefaultDisplay();
        new AlertDialog.Builder(activity).setTitle("Location info")
            .setMessage("City: " + location.getCity()
                + "\nCountry: " + location.getCountry()
                + "\nLat: " + location.getLat()
                + "\nLng: " + location.getLng())
            .setNeutralButton(location.isActive() ? "Disable Xposed" : "Enable Xposed", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    location.setActive(!location.isActive());
                    Options.getInstance().save();
                }
            })
            .setNegativeButton("Reset", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getSystemContext().startActivity(launchIntent.putExtra("choice", 2));
                }
            }).setPositiveButton("Yarrr", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show().getWindow().setLayout(display.getWidth(),display.getHeight()-(display.getHeight()/4));

    }
}
