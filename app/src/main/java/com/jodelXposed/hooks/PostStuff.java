package com.jodelXposed.hooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.R.layout.simple_list_item_1;
import static com.jodelXposed.utils.Log.xlog;
import static com.jodelXposed.utils.Utils.Colors.Colors;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class PostStuff {
    private static class RecyclerPostsAdapter {
        static String TrackPoster = "a";
        static String TrackOP = "r";
    }

    private static class CreateTextPostFragment {
        static String color = "axZ";
        static int BackgroundViewId = 2131689668;
        static int ImageViewCamera = 2131689671;
    }

    private static class RecyclerPostsAdapter$ViewHolder {
        static String TimeView = "aCL";
    }

    public PostStuff(XC_LoadPackage.LoadPackageParam lpparam) {
        /*
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

        /*
         * Track posts #1
         * Set additional data on the TimeView of each Post to track the
         * user_handle / poster
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.adapter.RecyclerPostsAdapter", lpparam.classLoader, RecyclerPostsAdapter.TrackPoster, "com.jodelapp.jodelandroidv3.view.adapter.RecyclerPostsAdapter$ViewHolder", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object textView = getObjectField(param.args[0], RecyclerPostsAdapter$ViewHolder.TimeView);
                List posts = (List) callMethod(param.thisObject, "getPosts");
                HashMap<String, String> ids = new HashMap<>(posts.size());

                for (Object post : posts) {
                    String user_handle = (String) getObjectField(post, "userHandle");
                    if (!ids.containsKey(user_handle)) {
                        ids.put(user_handle, String.valueOf(ids.size()));
                    }
                    setAdditionalInstanceField(post, "updateExtraPost", ids.get(user_handle));
                }

                try {
                    int i = (int) param.args[1];
                    String id = (String) getAdditionalInstanceField(posts.get(i), "updateExtraPost");
                    setAdditionalInstanceField(textView, "updateExtraView", id);
                } catch (IndexOutOfBoundsException ignored) {
                    //In case you reached the last available post (found on Mt. Everest)
                }

            }
        });

        /*
         * Track posts #2
         * Use the additional data from the TimeView to insert the poster ID
         * next to the regular TimeView text
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.TimeView", lpparam.classLoader, "update", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String id = (String) getAdditionalInstanceField(param.thisObject, "updateExtraView");
                callMethod(param.thisObject, "append", " #" + id);
            }
        });

        /*
         * Post-background color
         * Instantiate a chooser button / dialog beside the Camera button
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.CreateTextPostFragment", lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @SuppressWarnings("ResourceType")
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {

                final Activity activity = (Activity) callMethod(param.thisObject, "getActivity");

                final Button color = new Button(activity);
                color.setText("Choose\ncolor");
                color.setBackgroundColor(Color.TRANSPARENT);
                color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, simple_list_item_1, new String[]{"ORANGE", "YELLOW", "RED", "BLUE", "BLUEGRAYISH", "GREEN"});
                        new AlertDialog.Builder(activity).setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                            @SuppressWarnings("ResourceType")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Set background color
                                ((View) param.getResult()).findViewById(CreateTextPostFragment.BackgroundViewId).setBackgroundColor(Color.parseColor(Colors.get(which)));
                                //set instance field
                                XposedHelpers.setObjectField(param.thisObject, CreateTextPostFragment.color, Colors.get(which));
                                dialog.dismiss();
                            }
                        }).show();
                    }
                });

                LinearLayout linearLayout = (LinearLayout) ((View) param.getResult()).findViewById(CreateTextPostFragment.ImageViewCamera).getParent();
                linearLayout.addView(color);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            }
        });
    }
}
