package com.jodelXposed.hooks;

import android.location.Location;

import com.jodelXposed.utils.Options;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class LocationStuff {
    public LocationStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.location.LocationManager", lpparam.classLoader, "onLocationChanged", Location.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if(Options.getInstance().getLocationObject().isActive()) {
                    Location l = (Location) param.args[0];
                    l.setLatitude(Options.getInstance().getLocationObject().getLat());
                    l.setLongitude(Options.getInstance().getLocationObject().getLng());
                }
            }
        });
    }
}
