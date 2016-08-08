package com.jodelXposed.hooks;

import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.RequestReplacer;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.jodelXposed.utils.Log.xlog;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class LocationStuff {



    private static class OkClient$2 {
        static String InputStream = "EZ";
    }

    private static class FeedFragment {
        static String UpdateCityName = "dw";
    }

    public LocationStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        // Keep the ResponseBody as String
        findAndHookConstructor("retrofit.client.OkClient$2", lpparam.classLoader, "com.squareup.okhttp.ResponseBody", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String bodyString = IOUtils.toString((InputStream) callMethod(param.args[0], OkClient$2.InputStream), "UTF-8");
                xlog(bodyString);
                setAdditionalInstanceField(param.thisObject, "bodyString", bodyString);
            }
        });

        // Return the kept ResponseBody as InputStream on the original method
        findAndHookMethod("retrofit.client.OkClient$2", lpparam.classLoader, "in", new XC_MethodReplacement() {
            @Override
            protected InputStream replaceHookedMethod(MethodHookParam param) throws Throwable {
                String bodyString = (String) getAdditionalInstanceField(param.thisObject, "bodyString");
                return IOUtils.toInputStream(bodyString, "UTF-8");
            }
        });

        // Inject location data into request
        findAndHookConstructor(
            "retrofit.client.Request",
            lpparam.classLoader,
            String.class,
            String.class,
            List.class,
            "retrofit.mime.TypedOutput",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object request = param.thisObject;
                    Object body = callMethod(request, "getBody");
                    List headers = (List) callMethod(request, "getHeaders");
                    String method = (String) callMethod(request, "getMethod");
                    String url = (String) callMethod(request, "getUrl");

                    xlog(method + ": " + url);

                    if (method.equalsIgnoreCase("GET") && RequestReplacer.processable(url)) {
                        setObjectField(request, "url", RequestReplacer.processURL(url));
                    } else if (body != null && RequestReplacer.processable(url)) {
                        byte[] bodyBytes = (byte[]) getObjectField(body, "jsonBytes");
                        bodyBytes = RequestReplacer.processBody(bodyBytes);
                        setObjectField(body, "jsonBytes", bodyBytes);
                        xlog("Body: " + new String(bodyBytes));
                    }

                    if (headers != null) {
                        xlog("Headers: " + headers.toString());
                    }
                }
            });

        // Display correct city name
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.FeedFragment", lpparam.classLoader, FeedFragment.UpdateCityName, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (Options.getInstance().getLocationObject().isActive())
                    param.args[0] = Options.getInstance().getLocationObject().getCity();
            }
        });

        findAndHookMethod("com.jodelapp.jodelandroidv3.view.FeedFragment", lpparam.classLoader, FeedFragment.UpdateCityName, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {

                if (Options.getInstance().getLocationObject().isActive())
                    param.args[0] = Options.getInstance().getLocationObject().getCity();
            }
        });
    }
}
