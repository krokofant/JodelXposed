package com.jodelXposed.krokofant.hooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AndroidAppHelper;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jodelXposed.krokofant.utils.RequestReplacer;
import com.jodelXposed.krokofant.utils.ResponseReplacer;
import com.jodelXposed.krokofant.utils.Settings;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.R.layout.simple_list_item_1;
import static com.jodelXposed.krokofant.utils.Bitmap.loadBitmap;
import static com.jodelXposed.krokofant.utils.Log.xlog;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class JodelHooks {

    Settings settings = Settings.getInstance();

    public static class PhotoEditFragment {
        public static String Bitmap = "aAs";
        public static String ImageView = "aAl";
        public static String Method = "BI";
    }

    public static class OkClient$2 {
        public static String InputStream = "EZ";
    }

    public static class RecyclerPostsAdapter {
        public static String TrackPoster = "a";
        public static String TrackOP = "r";
    }

    public static class JodelImageHelper{
        public static String Bitmap = "a";
    }

    public static class RecyclerPostsAdapter$ViewHolder {
        public static String TimeView = "aCL";
    }

    public static class UDI {
        public static String GetUID = "Ar";
    }


    /**
     * These are the only accepted Colors by the Jodel Server, credits to pydel by rolsdorph
     */
    public static class Colors {
        public static ArrayList<String> Colors = new ArrayList<String>(){{
            add("#FFFF9908"); //Orange
            add("#FFFFBA00"); //Yellow
            add("#FFDD5F5F"); //Red
            add("#FF06A3CB"); //Blue
            add("#FF8ABDB0"); //Bluegrayish
            add("#FF9EC41C"); //Green
        }};
    }

    public static class CreateTextPostFragment{
        public static String color = "axZ";
        public static int BackgroundViewId = 2131689668;
        public static int ImageViewCamera = 2131689671;
    }

    public static class MyMenuItem{
        public static String displayName = "aAj";
        public static String RandomIntValue = "aAk";
    }

    public static class MyMenuFragment {
        public static String AddEntriesMethod = "BD";
    }

    public static class FeedFragment {
        public static String UpdateCityName = "dw";
    }

    public static class Storage {
        public static String UnlockFeatures = "cU";
    }


    public void hook(final XC_LoadPackage.LoadPackageParam lpparam) {
        /**
         * Add features on ImageView - load custom stored image, adjust ScaleType
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.PhotoEditFragment", lpparam.classLoader, PhotoEditFragment.Method, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                final boolean[] isInjected = {false};
                final Bitmap original = (Bitmap) getObjectField(param.thisObject, PhotoEditFragment.Bitmap);
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
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.adapter.RecyclerPostsAdapter", lpparam.classLoader, RecyclerPostsAdapter.TrackOP, List.class, new XC_MethodHook() {
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
        findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.JodelImageHelper", lpparam.classLoader, JodelImageHelper.Bitmap, Context.class, Bitmap.class, new XC_MethodReplacement() {
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
                xlog(bodyString);
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
        findAndHookMethod("com.jodelapp.jodelandroidv3.utilities.UniqueDeviceIdentifier", lpparam.classLoader, UDI.GetUID, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                xlog("UDI = " + param.getResult());
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

        /**
         * Track posts #1
         * Set additional data on the TimeView of each Post to track the
         * user_handle / poster
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.adapter.RecyclerPostsAdapter", lpparam.classLoader, RecyclerPostsAdapter.TrackPoster, "com.jodelapp.jodelandroidv3.view.adapter.RecyclerPostsAdapter$ViewHolder", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object textView = getObjectField(param.args[0], RecyclerPostsAdapter$ViewHolder.TimeView);
                List posts = (List)callMethod(param.thisObject, "getPosts");
                HashMap<String, String> ids = new HashMap<>(posts.size());

                for(Object post : posts) {
                    String user_handle = (String)getObjectField(post, "userHandle");
                    if(!ids.containsKey(user_handle)) {
                        ids.put(user_handle, String.valueOf(ids.size()));
                    }
                    setAdditionalInstanceField(post, "updateExtraPost", ids.get(user_handle));
                }

                try {
                    int i = (int)param.args[1];
                    String id = (String)getAdditionalInstanceField(posts.get(i), "updateExtraPost");
                    setAdditionalInstanceField(textView, "updateExtraView", id);
                }catch(IndexOutOfBoundsException ignored){
                    //In case you reached the last available post (found on Mt. Everest)
                }

            }
        });

        /**
         * Track posts #2
         * Use the additional data from the TimeView to insert the poster ID
         * next to the regular TimeView text
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.TimeView", lpparam.classLoader, "update", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String id = (String)getAdditionalInstanceField(param.thisObject, "updateExtraView");
                callMethod(param.thisObject, "append", " #" + id);
            }
        });

        /**
         * Post-background color
         * Instantiate a chooser button / dialog beside the Camera button
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.CreateTextPostFragment", lpparam.classLoader, "onCreateView",LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @SuppressWarnings("ResourceType")
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {

                final Activity activity = (Activity) callMethod(param.thisObject,"getActivity");

                final Button color = new Button(activity);
                color.setText("Choose\ncolor");
                color.setBackgroundColor(Color.TRANSPARENT);
                color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, simple_list_item_1, new String[] { "ORANGE", "YELLOW", "RED", "BLUE", "BLUEGRAYISH", "GREEN" });
                        new AlertDialog.Builder(activity).setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                            @SuppressWarnings("ResourceType")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Set background color
                                ((View)param.getResult()).findViewById(CreateTextPostFragment.BackgroundViewId).setBackgroundColor(Color.parseColor(Colors.Colors.get(which)));
                                //set instance field
                                XposedHelpers.setObjectField(param.thisObject,CreateTextPostFragment.color,Colors.Colors.get(which));
                                dialog.dismiss();
                            }
                        }).show();
                    }
                });

                LinearLayout linearLayout = (LinearLayout) ((View)param.getResult()).findViewById(CreateTextPostFragment.ImageViewCamera).getParent();
                linearLayout.addView(color);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            }
        });

        /* *
        * Hook constructor of MyMenuItem to apply strings (Item names) which are not in strings.xml
        * Seamless integration #1
        * */
        findAndHookConstructor("com.jodelapp.jodelandroidv3.view.MyMenuItem", lpparam.classLoader,Context.class,String.class,int.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                setObjectField(methodHookParam.thisObject,"name",methodHookParam.args[1]);
                try {
                    setObjectField(methodHookParam.thisObject,MyMenuItem.displayName, AndroidAppHelper.currentApplication().getResources().getString((int)methodHookParam.args[2]));
                }catch(Exception e){
                    switch ((int)methodHookParam.args[2]){
                        case 0:
                            setObjectField(methodHookParam.thisObject,MyMenuItem.displayName,"Xposed general");
                            break;
                        case 1:
                            setObjectField(methodHookParam.thisObject,MyMenuItem.displayName,"Choose location");
                            break;
                        case 3:
                            setObjectField(methodHookParam.thisObject,MyMenuItem.displayName,"Restart Jodel");
                            break;
                    }
                }
                setObjectField(methodHookParam.thisObject,MyMenuItem.RandomIntValue,-1);
                return null;
            }
        });

        /**
         * Add JodelXposed entries in ListView
         * Seamless integration #2
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.MyMenuFragment", lpparam.classLoader, MyMenuFragment.AddEntriesMethod, new XC_MethodHook() {
            @SuppressWarnings("unchecked")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                Class<?> MyMenuItem = findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem",lpparam.classLoader);
                final Activity activity = (Activity) callMethod(param.thisObject,"getActivity");
                Object xposedLocationInformationItem = XposedHelpers.newInstance(MyMenuItem,activity,"xposedInfo", 0);
                Object xposedMapItem = XposedHelpers.newInstance(MyMenuItem,activity,"xposedMap", 1);
                Object xposedRestartItem = XposedHelpers.newInstance(MyMenuItem,activity,"xposedRestart", 3);
                ArrayAdapter myMenuItemArrayAdapter = (ArrayAdapter) XposedHelpers.callMethod(param.thisObject,"getListAdapter");
                myMenuItemArrayAdapter.add(xposedLocationInformationItem);
                myMenuItemArrayAdapter.add(xposedMapItem);
                myMenuItemArrayAdapter.add(xposedRestartItem);
                myMenuItemArrayAdapter.notifyDataSetChanged();

            }
        });

        /**
         * Add JodelXposed entries in ListView - Handle clicks on Items
         * Seamless integration #3
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.MyMenuFragment", lpparam.classLoader, "onListItemClick", ListView.class, View.class, int.class, long.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object selected = ((ArrayAdapter) XposedHelpers.callMethod(param.thisObject,"getListAdapter")).getItem(((int)param.args[2])-1);

                Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
                final Context context = (Context) callMethod(activityThread, "getSystemContext");
                final Intent launchIntent = new Intent(Intent.ACTION_MAIN);
                launchIntent.setComponent(new ComponentName("com.jodelXposed", "com.jodelXposed.BackgroundOperations"));
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (((String)getObjectField(selected,"name")).equalsIgnoreCase("xposedMap")){
                    context.startActivity(launchIntent.putExtra("choice",1));
                }else if (((String)getObjectField(selected,"name")).equalsIgnoreCase("xposedRestart")){
                    context.startActivity(launchIntent.putExtra("choice",3));
                } else if (((String)getObjectField(selected,"name")).equalsIgnoreCase("xposedInfo")){
                    final Activity activity = (Activity) callMethod(param.thisObject,"getActivity");
                    new AlertDialog.Builder(activity).setTitle("Location info")
                        .setMessage("City: "+settings.getCity()
                            +"\nCountry: "+settings.getCountry()
                            +"\nLat: "+settings.getLat()
                            +"\nLng: "+settings.getLng())
                        .setNeutralButton(settings.isActive() ? "Disable Xposed" : "Enable Xposed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                settings.setActive(!settings.isActive());
                                try {
                                    settings.save();
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(launchIntent.putExtra("choice",3));
                            }
                        })
                        .setNegativeButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                context.startActivity(launchIntent.putExtra("choice",2));
                            }
                        }).setPositiveButton("Yarrr", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                    }).show();
                }
            }
        });

        /* *
        * Display the correct city name on the sliding tab strip
        * */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.FeedFragment", lpparam.classLoader, FeedFragment.UpdateCityName, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (settings.isActive())
                    param.args[0] = settings.getCity();
            }
        });

        /* *
        * Unlock experiments (features that are available on some devices like post pining or searching for hashtags)
        * */
        findAndHookMethod("com.jodelapp.jodelandroidv3.model.Storage", lpparam.classLoader, Storage.UnlockFeatures, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (settings.isActive())
                    param.setResult(true);
            }
        });


    }
}
