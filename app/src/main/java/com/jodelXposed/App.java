package com.jodelXposed;

import android.content.pm.PackageInfo;

import com.jodelXposed.hooks.AntiAntiXposed;
import com.jodelXposed.hooks.BetaStuff;
import com.jodelXposed.hooks.ImageStuff;
import com.jodelXposed.hooks.LocationStuff;
import com.jodelXposed.hooks.PostStuff;
import com.jodelXposed.hooks.SettingsStuff;
import com.jodelXposed.hooks.UniqueDeviceIdentifierStuff;
import com.jodelXposed.utils.Options;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static com.jodelXposed.utils.Log.xlog;
import static com.jodelXposed.utils.Utils.getSystemContext;

public class App implements IXposedHookLoadPackage {

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
}
