package com.jodelXposed.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class Utils {
    public static String SettingsPath = Environment.getExternalStorageDirectory() + "/.jodel-settings-v2";

    public static Context getSystemContext() {
        Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
        return (Context) callMethod(activityThread, "getSystemContext");
    }

    public static Activity getActivity(XC_MethodHook.MethodHookParam param) {
        return (Activity) callMethod(param.thisObject, "getActivity");
    }

    public static int getDisplayHeight(){
        return Utils.getSystemContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static int getDisplayWidth(){
        return Utils.getSystemContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static Display getDefaultDisplay(){
        return ((WindowManager)Utils.getSystemContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    public static int getIdentifierById(XC_MethodHook.MethodHookParam param , String id){
        return getActivity(param).getResources().getIdentifier(id, "id", "com.tellm.android.app");
    }

    public static Intent getNewIntent(String path) {
        Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.setComponent(new ComponentName("com.jodelXposed", "com.jodelXposed." + path));
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return launchIntent;
    }
    /**
     * These are the only accepted Colors by the Jodel Server, credits to pydel by rolsdorph
     */
    public static class Colors {
        public static ArrayList<String> Colors = new ArrayList<String>() {{
            add("#FFFF9908"); //Orange
            add("#FFFFBA00"); //Yellow
            add("#FFDD5F5F"); //Red
            add("#FF06A3CB"); //Blue
            add("#FF8ABDB0"); //Bluegrayish
            add("#FF9EC41C"); //Green
        }};
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
