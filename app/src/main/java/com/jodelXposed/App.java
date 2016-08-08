package com.jodelXposed;

import android.content.pm.PackageInfo;
import android.content.res.XModuleResources;

import com.jodelXposed.hooks.AntiAntiXposed;
import com.jodelXposed.hooks.BetaStuff;
import com.jodelXposed.hooks.ImageStuff;
import com.jodelXposed.hooks.LocationStuff;
import com.jodelXposed.hooks.PostStuff;
import com.jodelXposed.hooks.SettingsStuff;
import com.jodelXposed.hooks.UniqueDeviceIdentifierStuff;
import com.jodelXposed.utils.Options;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static com.jodelXposed.utils.Log.xlog;
import static com.jodelXposed.utils.Utils.getSystemContext;

public class App implements IXposedHookLoadPackage,IXposedHookZygoteInit, IXposedHookInitPackageResources {

    private static String MODULE_PATH = null;
    public static int dialogMainView;
    public static int dialogMainTextView;
    public static int locationSwitch;
    public static int udiSwitch;
    public static int editTextUdi;

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
            if (!lpparam.packageName.equals("com.tellm.android.app"))
                return;

            if (lpparam.packageName.equals("com.tellm.android.app")) {
                PackageInfo pkgInfo = getSystemContext().getPackageManager().getPackageInfo(lpparam.packageName, 0);
//            xlog(String.format("----------%n" +
//                    "Starting JodelXposed%n" +
//                    "Version %s (%d)%n" +
//                    "JodelTarget %s (%d)%n" +
//                    "JodelLocal %s (%d)%n" +
//                    "----------%n",
//                BuildConfig.VERSION_NAME,
//                BuildConfig.VERSION_CODE,
////                BuildConfig.JODEL_VERSION_NAME, TODO
////                BuildConfig.JODEL_VERSION_CODE, TODO
//                pkgInfo.versionName,
//                pkgInfo.versionCode
//            ));
                Options.getInstance().load();

                xlog("Loading hooks");
                new AntiAntiXposed(lpparam);
                new BetaStuff(lpparam);
                new ImageStuff(lpparam);
                new LocationStuff(lpparam);
                new PostStuff(lpparam);
                new SettingsStuff(lpparam);
                new UniqueDeviceIdentifierStuff(lpparam);

            }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.tellm.android.app"))
            return;

        XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
        dialogMainView = resparam.res.addResource(modRes,R.layout.xpose_dialog);
        dialogMainTextView = resparam.res.addResource(modRes,R.id.dialogMainTextView);
        locationSwitch = resparam.res.addResource(modRes,R.id.locationSwitch);
        udiSwitch = resparam.res.addResource(modRes,R.id.udiSwitch);
        editTextUdi = resparam.res.addResource(modRes,R.id.editTextUdi);
    }


    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }
}
