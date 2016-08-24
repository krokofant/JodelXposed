package com.jodelXposed.hooks;

import android.os.Bundle;

import com.jodelXposed.utils.Options;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Color.format6;
import static com.jodelXposed.utils.Color.getReplacementColor;
import static com.jodelXposed.utils.Color.normalizeColor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class ThemeStuff {

    //TODO DYNAMIC HOOKING

    public ThemeStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.Util", lpparam.classLoader, "dq", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (Options.getInstance().getThemeObject().isActive()) {
                    String color = (String) param.getResult();
                    param.setResult(getReplacementColor(color));
                }
            }
        });

        findAndHookMethod("com.jodelapp.jodelandroidv3.data.gcm.MyGcmListenerService", lpparam.classLoader, "a", String.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (Options.getInstance().getThemeObject().isActive()) {
                    Bundle bundle = (Bundle) param.args[1];
                    String color = normalizeColor(bundle.getString("color"));
                    String newColor = getReplacementColor(color);
                    String formattedColor = format6(newColor);
                    bundle.putString("color", formattedColor);
                }
            }
        });
    }
}
