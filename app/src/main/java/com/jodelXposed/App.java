package com.jodelXposed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import com.jodelXposed.models.Hookvalues;
import com.jodelXposed.retrofit.Response.Classes;
import com.jodelXposed.retrofit.Response.HooksResponse;
import com.jodelXposed.retrofit.Response.Methods;
import com.jodelXposed.retrofit.RetrofitProvider;
import com.jodelXposed.utils.Hooks;
import com.jodelXposed.utils.Options;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jodelXposed.utils.Log.dlog;
import static com.jodelXposed.utils.Log.xlog;
import static com.jodelXposed.utils.Utils.getNewIntent;
import static com.jodelXposed.utils.Utils.getSystemContext;

public class App implements IXposedHookLoadPackage,IXposedHookZygoteInit {

    public static String MODULE_PATH = null;


    @SuppressLint("DefaultLocale")
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tellm.android.app"))
            return;

        if (lpparam.packageName.equals("com.tellm.android.app")) {

            final PackageInfo pkgInfo = getSystemContext().getPackageManager().getPackageInfo(lpparam.packageName, 0);

            try {
                dlog(String.format("----------%n" +
                        "Starting JodelXposed%n" +
                        "Version %s (%d)%n" +
                        "JodelTarget %s (%d)%n" +
                        "JodelLocal %s (%d)%n" +
                        "----------%n",
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                    BuildConfig.JODEL_VERSION_NAME,
                    BuildConfig.JODEL_VERSION_CODE,
                    pkgInfo.versionName,
                    pkgInfo.versionCode
                ));
            }catch(Exception e){
                e.printStackTrace();
                xlog("Information cannot be gathered");
            }
            try {
                Options.INSTANCE.load();
            }catch (Exception e){
                e.printStackTrace();
                xlog("Options cannot be loaded");
            }

            Hooks hooks = new Hooks(lpparam);

            dlog("#### Loading hooks ####");
            hooks.hook();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermissions();
            }

            if (Options.INSTANCE.getHooks().versionCode!=pkgInfo.versionCode){
                updateHooks(pkgInfo.versionCode);
            }

        }
    }

    private void checkPermissions() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSystemContext().startActivity(getNewIntent("utils.Picker").addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        },3000);
    }


    private void updateHooks(final int versionCode) {
        final Hookvalues hooks = Options.INSTANCE.getHooks();

        RetrofitProvider.getJodelXposedService().getHooks(versionCode).enqueue(new Callback<HooksResponse>() {
            @Override
            public void onResponse(Call<HooksResponse> call, Response<HooksResponse> response) {
                try {
                    HooksResponse rhooks = response.body();
                    Methods methods = rhooks.getMethods();
                    Classes classes = rhooks.getClasses();

                    hooks.BetaHook_UnlockFeatures = methods.getBetaHookUnlockFeatures();
                    hooks.ImageHookValues_ImageView = methods.getImageHookValuesImageView();
                    hooks.PostStuff_ColorField = methods.getPostStuffColorField();
                    hooks.PostStuff_TrackPostsMethod = methods.getPostStuffTrackPostsMethod();
                    hooks.Settings_AddEntriesMethod = methods.getSettingsAddEntriesMethod();
                    hooks.Settings_HandleClickEventsMethod = methods.getSettingsHandleClickEventsMethod();
                    hooks.Theme_GCMReceiverMethod = methods.getThemeGCMReceiverMethod();
                    hooks.UDI_GetUdiMethod = methods.getUDIGetUdiMethod();

                    hooks.Class_CreateTextPostFragment = classes.getClassCreateTextPostFragment();
                    hooks.Class_MyGcmListenerService = classes.getClassMyGcmListenerService();
                    hooks.Class_MyMenuPresenter = classes.getClassMyMenuPresenter();
                    hooks.Class_PhotoEditFragment = classes.getClassPhotoEditFragment();
                    hooks.Class_PostDetailRecyclerAdapter = classes.getClassPostDetailRecyclerAdapter();
                    hooks.Class_Storage = classes.getClassStorage();
                    hooks.Class_UniqueDeviceIdentifier = classes.getClassUniqueDeviceIdentifier();
                    //Success updating hooks, lets update the local version code
                    Options.INSTANCE.getHooks().versionCode = versionCode;

                    Toast.makeText(getSystemContext(), rhooks.getUpdatemessage()+" Please soft-reboot your device!", Toast.LENGTH_LONG).show();

                    Options.INSTANCE.save();
                }catch (Exception e){
                    Toast.makeText(getSystemContext(), "Your Jodel version isnt supported by JodelXposed yet.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<HooksResponse> call, Throwable t) {
                Toast.makeText(getSystemContext(), "Failed updating hooks, "+t.getLocalizedMessage()+" !", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }
}
