package com.jodelXposed.krokofant.hooks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import com.jodelXposed.krokofant.utils.RequestReplacer;
import com.jodelXposed.krokofant.utils.ResponseReplacer;
import com.jodelXposed.krokofant.utils.Settings;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.jodelXposed.krokofant.utils.Bitmap.loadBitmap;
import static com.jodelXposed.krokofant.utils.Log.xlog;
import static de.robv.android.xposed.XposedHelpers.*;

public class JodelHooks {

    public static class PhotoEditFragment {
        public static String Post = "arw";
        public static String ImageView = "arx";
    }

    public static class OkClient$2 {
        public static String InputStream = "CI";
    }

    public static class RecyclerPostsAdapter {
        public static String Bitmap = "a";
    }


    public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        /**
         * Add features on ImageView - load custom stored image, adjust ScaleType
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.PhotoEditFragment", lpparam.classLoader, "zC", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                final boolean[] isInjected = {false};
                Object o = getObjectField(param.thisObject, PhotoEditFragment.Post);
                final Bitmap original = (Bitmap) getObjectField(o, "imageBitmap");
                ImageView a = (ImageView) getObjectField(param.thisObject, PhotoEditFragment.ImageView);

                a.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        xlog("Long clicked!");
                        Bitmap b;
                        if (isInjected[0]) {
                            b = original;
                            isInjected[0] = false;
                        } else {
                            b = loadBitmap();
                            isInjected[0] = true;
                        }
                        ((ImageView) v).setImageBitmap(b);
                        return true;
                    }
                });

                xlog("Adding click listener to imageView");
                a.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP &&
                            (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                                keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
                            return true;
                        }

                        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                            ImageView iv = (ImageView) v;
                            ImageView.ScaleType sT;
                            switch (iv.getScaleType()) {
                                case CENTER:
                                    sT = ImageView.ScaleType.FIT_CENTER;
                                    break;
                                case FIT_CENTER:
                                    sT = ImageView.ScaleType.CENTER;
                                    break;
                                default:
                                    sT = ImageView.ScaleType.CENTER;
                            }
                            xlog("ScaleType set to " + sT.toString());
                            iv.setScaleType(sT);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                // Set focus on the ImageView
                a.requestFocus();
            }
        });

        /**
         * Apply darker shade to OP's posts in a thread
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.adapter.RecyclerPostsAdapter", lpparam.classLoader, "p", List.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                List posts = (List) param.args[0];
                if (posts != null) {
                    xlog("Posts: " + posts.size());
                    for (Object post : posts) {
                        String color = (String) getObjectField(post, "color");
                        Integer parentCreator = (Integer) getObjectField(post, "parentCreator");
//                        String message = (String) getObjectField(post, "message");
//                        xlog("Message:" + message + " Parent:" + parentCreator + " Color:" + color);
                        if (parentCreator != null && parentCreator == 1) {
                            float[] hsv = new float[3];
                            int c = Color.parseColor("#" + color);
                            Color.colorToHSV(c, hsv);
                            hsv[2] *= 0.9f;
                            c = Color.HSVToColor(hsv);
                            setObjectField(post, "color", Integer.toHexString(c).substring(2));
                        }
                    }
                }
            }
        });

        /**
         * Remove blur effect on posts
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.adapter.RecyclerPostsAdapter", lpparam.classLoader, RecyclerPostsAdapter.Bitmap, Context.class, Bitmap.class, new XC_MethodReplacement() {
            @Override
            protected Bitmap replaceHookedMethod(MethodHookParam param) throws Throwable {
                return (Bitmap) param.args[1];
            }
        });


        /**
         * Replace parts of requests
         */
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
        /**
         * Keep the ResponseBody as String
         */
        findAndHookConstructor("retrofit.client.OkClient$2", lpparam.classLoader, "com.squareup.okhttp.ResponseBody", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String bodyString = IOUtils.toString((InputStream) callMethod(param.args[0], OkClient$2.InputStream), "UTF-8");
                setAdditionalInstanceField(param.thisObject, "bodyString", bodyString);
            }
        });

        /**
         * Return the kept ResponseBody as InputStream on the original method
         */
        findAndHookMethod("retrofit.client.OkClient$2", lpparam.classLoader, "in", new XC_MethodReplacement() {
            @Override
            protected InputStream replaceHookedMethod(MethodHookParam param) throws Throwable {
                String bodyString = (String) getAdditionalInstanceField(param.thisObject, "bodyString");
                return IOUtils.toInputStream(bodyString, "UTF-8");
            }
        });

        /**
         * Replace parts of response
         */
        findAndHookConstructor(
            "retrofit.client.Response",
            lpparam.classLoader,
            String.class,
            "int",
            String.class,
            List.class,
            "retrofit.mime.TypedInput",
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String url = (String) param.args[0];
                    int status = (int) param.args[1];
                    String reason = (String) param.args[2];
                    List headers = (List) param.args[3];
                    Object body = param.args[4];

                    xlog("Response (" + status + ") from " + url);

                    if (body != null) {
                        if (ResponseReplacer.processable(url)) {
                            setAdditionalInstanceField(body, "bodyString", ResponseReplacer.processBody(
                                (String) getAdditionalInstanceField(body, "bodyString")
                            ));
                            xlog("Manipulated response: " + getAdditionalInstanceField(body, "bodyString"));
                        }
                    }

                    if (headers != null) {
                        xlog("Headers: " + headers.toString());
                    }

                    if (reason != null) {
                        xlog("Reason: " + reason);
                    }
                }
            }
        );

        /**
         * Spoof UID
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.UniqueDeviceIdentifier", lpparam.classLoader, "yr", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                xlog("UDI = " + param.getResult());
                Settings settings = Settings.getInstance();
                try {
                    if (!settings.isLoaded())
                        settings.load();
                    if(settings.getUid().length() == 0) {
                        settings.setUid((String)param.getResult());
                        settings.save();
                    } else if(settings.getUid().equals(param.getResult())) {
                        xlog("UDI not spoofed");
                    } else {
                        xlog("UDI spoof = " + settings.getUid());
                        param.setResult(settings.getUid());
                    }
                } catch (JSONException | IOException e) {
                    xlog("Error: " + e.getLocalizedMessage());
                }
            }
        });
    }
}
