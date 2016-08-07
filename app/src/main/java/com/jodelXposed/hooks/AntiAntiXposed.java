package com.jodelXposed.hooks;

import android.app.Application;

import java.lang.reflect.Array;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Log.xlog;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class AntiAntiXposed {
    private static class JodelApp {
        static String FirstMethod = "yU";
        static String SecondMethod = "yV";
    }

    public AntiAntiXposed(final XC_LoadPackage.LoadPackageParam lpparam) {
        try {

            /*
             * Disable the xposed check and all crash reporters #1
             * @JodelCreators hopefully you dont get any anoying crash reports anymore :)
             * Let the Thread.currentThread.getStacktracke() return a empty StackTraceElementArray
             */
            findAndHookMethod("java.lang.Thread", lpparam.classLoader, "getStackTrace", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(new StackTraceElement[]{});
                }
            });

            /*
             * Disable the xposed check and all crash reporters #2
             * @JodelCreators hopefully you dont get any anoying crash reports anymore :)
             * Replicate the JodelApp.onCreate() and disable the crash reporter
             */
            findAndHookMethod("com.jodelapp.jodelandroidv3.JodelApp", lpparam.classLoader, "onCreate", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    Class<?> CrashlyticsCoreBuilder = findClass("com.crashlytics.android.core.CrashlyticsCore.Builder", lpparam.classLoader);
                    Class<?> CrashlyticsBuilder = findClass("com.crashlytics.android.Crashlytics.Builder", lpparam.classLoader);
                    final Class<?> Kit = findClass("io.fabric.sdk.android.Kit", lpparam.classLoader);
                    Class<?> Fabric = findClass("io.fabric.sdk.android.Fabric", lpparam.classLoader);

                    // CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(true).build();
                    Object coreBuilderInstance = newInstance(CrashlyticsCoreBuilder);
                    callMethod(coreBuilderInstance, "disabled", true);
                    Object crashlyticsCore = callMethod(coreBuilderInstance, "build");

                    // Crashlytics crashlytics = new Crashlytics.Builder().core(core).build()
                    Object crashlyticsBuilderInstance = newInstance(CrashlyticsBuilder);
                    callMethod(crashlyticsBuilderInstance, "core", crashlyticsCore);
                    final Object crashlyticsInstance = callMethod(crashlyticsBuilderInstance, "build");

                    Object kits = Array.newInstance(Kit, 1);
                    Array.set(kits, 0, crashlyticsInstance);
                    callStaticMethod(Fabric, "a", methodHookParam.thisObject, kits);

                    callMethod(methodHookParam.thisObject, JodelApp.FirstMethod);
                    callMethod(methodHookParam.thisObject, JodelApp.SecondMethod);
                    return null;
                }
            });


            /*
             * Disable the xposed check and all crash reporters #3
             * @JodelCreators hopefully you dont get any anoying crash reports anymore :)
             * Remove the Analytics URL so there cant be sent any Analytics reports anymore
             */
            findAndHookConstructor("com.jodelapp.jodelandroidv3.api.ApiModule", lpparam.classLoader, Application.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    setObjectField(param.thisObject, "ANALYTICS_URL", "");
                }
            });

        } catch (Exception ignored) {
            xlog("Oh, you have 4.12.3? No need for AntiAntiXposed");
        }
    }
}
