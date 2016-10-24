package com.jodelXposed.hooks;

import android.content.Intent;

import com.jodelXposed.models.Hookvalues;
import com.jodelXposed.utils.Options;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Utils.getNewIntent;
import static com.jodelXposed.utils.Utils.getSystemContext;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class SettingsStuff {

    public SettingsStuff(final XC_LoadPackage.LoadPackageParam lpparam) {

        Hookvalues hooks = Options.INSTANCE.getHooks();

        /*
         * Add JodelXposed entries in ListView
         * Seamless integration #1
         */
        findAndHookMethod(hooks.Class_MyMenuPresenter, lpparam.classLoader, hooks.Settings_AddEntriesMethod, new XC_MethodHook() {
            @SuppressWarnings("unchecked")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ((List) param.getResult()).add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedLocation", "JX Change location"));
                ((List) param.getResult()).add(XposedHelpers.newInstance(findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader), "xposedGeneralSettings", "JX Settings"));

            }
        });

        /*
         * Add JodelXposed entries in ListView - Handle clicks on Items
         * Seamless integration #2
         */
        findAndHookMethod(hooks.Class_MyMenuPresenter, lpparam.classLoader, hooks.Settings_HandleClickEventsMethod, "com.jodelapp.jodelandroidv3.view.MyMenuItem", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String selected = (String) getObjectField(param.args[0], "name");

                if (selected.equalsIgnoreCase("xposedLocation"))
                    getSystemContext().startActivity(getNewIntent("utils.Picker").addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("choice",1));
                else if (selected.equalsIgnoreCase("xposedGeneralSettings"))
                    getSystemContext().startActivity(getNewIntent("JXPreferenceActivity").addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
    }



}
