package com.jodelXposed.hooks;

import android.os.Bundle;

import com.jodelXposed.utils.Options;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Admin on 24.08.2016.
 */
public class GCMStuff {

    private boolean originalstate;

    //TODO DYNAMIC HOOKING
    public GCMStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.jodelapp.jodelandroidv3.data.gcm.MyGcmListenerService", lpparam.classLoader, "a", String.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Bundle bundle = (Bundle) param.args[1];
                String bundle_id = bundle.getString("subject_id");

                if (Options.getInstance().getBetaObject().getNotificationList().contains(bundle_id)){
                    originalstate = XposedHelpers.getBooleanField(XposedHelpers.getObjectField(param.thisObject,"avG"),"avs");
                    XposedHelpers.setObjectField(XposedHelpers.getObjectField(param.thisObject,"avG"),"avs",false);
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setObjectField(XposedHelpers.getObjectField(param.thisObject,"avG"),"avs",originalstate);
            }
        });
    }
}
