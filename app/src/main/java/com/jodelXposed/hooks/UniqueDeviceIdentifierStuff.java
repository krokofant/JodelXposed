package com.jodelXposed.hooks;

import com.jodelXposed.utils.Hooks;
import com.jodelXposed.utils.Options;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Log.xlog;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class UniqueDeviceIdentifierStuff {

    /**
     * Spoof UID
     */
    public UniqueDeviceIdentifierStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.UniqueDeviceIdentifier", lpparam.classLoader, Hooks.UDI.GetUID, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String realUDI = (String) param.getResult();
                com.jodelXposed.models.UDI udiOptions = Options.getInstance().options.udi;
                if (udiOptions.udi.length() == 0) {
                    udiOptions.udi = realUDI;
                    xlog("Backing up UDI");
                    Options.getInstance().save();
                }
                if (udiOptions.active) {
                    if (udiOptions.udi.equals(realUDI)) {
                        xlog("Using real UDI");
                    } else {
                        String spoofUDI = udiOptions.udi;
                        xlog("UDI spoof = " + spoofUDI);
                        param.setResult(spoofUDI);
                    }
                }
            }
        });
    }
}
