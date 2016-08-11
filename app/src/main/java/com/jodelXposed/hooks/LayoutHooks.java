package com.jodelXposed.hooks;

import android.content.res.XModuleResources;

import com.jodelXposed.App;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;

/**
 * Created by Admin on 08.08.2016.
 */
public class LayoutHooks {

//    public static int stringGeneral;
//    public static int stringLocation;

    public void hook(XC_InitPackageResources.InitPackageResourcesParam resparam) {
        XModuleResources modRes = XModuleResources.createInstance(App.MODULE_PATH, resparam.res);
//        stringGeneral = resparam.res.addResource(modRes, R.string.xgeneral);
//        stringLocation = resparam.res.addResource(modRes,R.string.xlocation);

    }
}
