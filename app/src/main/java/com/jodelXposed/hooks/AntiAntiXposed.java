package com.jodelXposed.hooks;

import android.app.Application;

import com.jodelXposed.utils.Hooks;

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

    /*
    * Our respect JodelDevelopers, this one was very hard to figure out
    * */

    public AntiAntiXposed(final XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            /*
            * Disable the xposed check and all crash reporters #1
            * Disable the checks for any suspicious class names
            * This is going to be an interesting StackTrace
            * */
            findAndHookMethod("java.lang.StackTraceElement", lpparam.classLoader, "getClassName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String s = (String)param.getResult();
                    if (s.contains("reflect") || s.contains("xposed") || s.contains("ActivityThread") || s.contains("otto")){
                        param.setResult(Long.toHexString(Double.doubleToLongBits(Math.random())));
                    }
                }
            });

            /*
            * Disable the xposed check and all crash reporters #2
            * Emulate that the app was installed by GooglePlay
            * */
            findAndHookMethod("android.app.ApplicationPackageManager", lpparam.classLoader, "getInstallerPackageName", String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult("com.android.vending");
                }
            });

            /*
             * Disable the xposed check and all crash reporters #3
             * @JodelCreators hopefully you dont get any anoying crash reports anymore :)
             * Replicate the JodelApp.onCreate() and disable the crash reporter
             */
            findAndHookMethod("com.jodelapp.jodelandroidv3.JodelApp", lpparam.classLoader, "onCreate", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {

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

                    callMethod(methodHookParam.thisObject, Hooks.JodelApp.FirstMethod);
                    callMethod(methodHookParam.thisObject, Hooks.JodelApp.SecondMethod);
                    return null;
                }
            });


            /*
             * Disable the xposed check and all crash reporters #4
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
