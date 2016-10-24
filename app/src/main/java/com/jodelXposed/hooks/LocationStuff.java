package com.jodelXposed.hooks;

import android.location.Location;

import com.jodelXposed.utils.Options;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class LocationStuff {
    public LocationStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.data.googleservices.location.LocationManager", lpparam.classLoader, "onLocationChanged", Location.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if(Options.INSTANCE.getLocation().getActive()) {
                    Location l = (Location) param.args[0];
                    l.setLatitude(Options.INSTANCE.getLocation().getLat());
                    l.setLongitude(Options.INSTANCE.getLocation().getLng());
                }
            }
        });
    }
}
