package com.jodelXposed.hooks;

import com.jodelXposed.models.Hookvalues;
import com.jodelXposed.models.UDI;
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
        Hookvalues hooks = Options.INSTANCE.getHooks();
        findAndHookMethod(hooks.Class_UniqueDeviceIdentifier, lpparam.classLoader, hooks.UDI_GetUdiMethod, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String realUDI = (String) param.getResult();
                UDI udiOptions = Options.INSTANCE.getUdi();
                if (udiOptions.getUdi().length() == 0) {
                    udiOptions.setUdi(realUDI);
                    xlog("Backing up UDI");
                    Options.INSTANCE.save();
                }
                if (udiOptions.getActive()) {
                    if (udiOptions.getUdi().equals(realUDI) || udiOptions.getUdi().length() == 0) {
                        xlog("Using real UDI");
                    } else {
                        String spoofUDI = udiOptions.getUdi();
                        xlog("UDI spoof = " + spoofUDI);
                        param.setResult(spoofUDI);
                    }
                }
            }
        });
    }
}
