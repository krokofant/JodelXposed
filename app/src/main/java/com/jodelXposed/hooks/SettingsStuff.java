package com.jodelXposed.hooks;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jodelXposed.utils.Hooks;
import com.jodelXposed.utils.Hooks.SettingsStuff.MyMenuItem;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Utils.getActivity;
import static com.jodelXposed.utils.Utils.getNewIntent;
import static com.jodelXposed.utils.Utils.getSystemContext;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class SettingsStuff {

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
                } catch (Exception ignored) {
                    if ((int) methodHookParam.args[2] == 0)
                        setObjectField(methodHookParam.thisObject, MyMenuItem.displayName, "XGeneral");
                }
                setObjectField(methodHookParam.thisObject, MyMenuItem.RandomIntValue, -1);
                return null;
            }
        });

        /*
         * Add JodelXposed entries in ListView
         * Seamless integration #2
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.MyMenuFragment", lpparam.classLoader, Hooks.SettingsStuff.MyMenuFragment.AddEntriesMethod, new XC_MethodHook() {
            @SuppressWarnings("unchecked")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                Class<?> MyMenuItem = findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader);
                Object xposedLocationInformationItem = XposedHelpers.newInstance(MyMenuItem, getActivity(param), "xposedInfo", 0);

                ArrayAdapter myMenuItemArrayAdapter = (ArrayAdapter) XposedHelpers.callMethod(param.thisObject, "getListAdapter");

                myMenuItemArrayAdapter.add(xposedLocationInformationItem);
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
                String selected = (String) getObjectField(((ArrayAdapter) XposedHelpers.callMethod(param.thisObject, "getListAdapter")).getItem(((int) param.args[2]) - 1), "name");

                if (selected.equalsIgnoreCase("xposedInfo"))
                    getSystemContext().startActivity(getNewIntent("activities.SettingsActivity"));

            }
        });
    }
}
