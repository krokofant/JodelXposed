package com.jodelXposed.hooks;

import com.jodelXposed.utils.Options;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class BetaStuff {
    private static class Storage {
        static String UnlockFeatures = "cU";
        static String DisableSchoolScreen1 = "Ac";
        static String DisableSchoolScreen2 = "Ad";
        static String DisableSchoolScreen3 = "zT";
        static String ChannelsLimit = "Ag";
    }

    public BetaStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        // Unlock experiments (features that are available on some devices like post pining or searching for hashtags)
        findAndHookMethod("com.jodelapp.jodelandroidv3.model.Storage", lpparam.classLoader, Storage.UnlockFeatures, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (Options.getInstance().getLocationObject().isActive())
                    param.setResult(true);
            }
        });

        // Disable the schoolscreen #1
        findAndHookMethod("com.jodelapp.jodelandroidv3.model.Storage", lpparam.classLoader, Storage.DisableSchoolScreen1, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false);
            }
        });

        // Disable the schoolscreen #2
        findAndHookMethod("com.jodelapp.jodelandroidv3.model.Storage", lpparam.classLoader, Storage.DisableSchoolScreen2, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false);
            }
        });

        // Disable the schoolscreen #3
        findAndHookMethod("com.jodelapp.jodelandroidv3.model.Storage", lpparam.classLoader, Storage.DisableSchoolScreen3, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false);
            }
        });

        // Set channels limit from default (5) to 100
        findAndHookMethod("com.jodelapp.jodelandroidv3.model.Storage", lpparam.classLoader, Storage.ChannelsLimit, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(100);
            }
        });
    }
}
