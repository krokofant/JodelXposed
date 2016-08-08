package com.jodelXposed;

import android.content.res.XModuleResources;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;

/**
 * Created by Admin on 08.08.2016.
 */
public class LayoutHooks {

    public static int dialogMainView;
    public static int dialogMainTextView;
    public static int locationSwitch;
    public static int udiSwitch;
    public static int editTextUdi;

    public void hook(XC_InitPackageResources.InitPackageResourcesParam resparam){
        XModuleResources modRes = XModuleResources.createInstance(App.MODULE_PATH, resparam.res);
        dialogMainView = resparam.res.addResource(modRes,R.layout.xposed_dialog);
        dialogMainTextView = resparam.res.addResource(modRes,R.id.dialogMainTextView);
        locationSwitch = resparam.res.addResource(modRes,R.id.locationSwitch);
        udiSwitch = resparam.res.addResource(modRes,R.id.udiSwitch);
        editTextUdi = resparam.res.addResource(modRes,R.id.editTextUdi);
    }
}
