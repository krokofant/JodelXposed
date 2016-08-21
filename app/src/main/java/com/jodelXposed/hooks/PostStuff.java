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

import com.jodelXposed.utils.Hooks;
import com.jodelXposed.utils.Utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.R.layout.simple_list_item_1;
import static com.jodelXposed.utils.Utils.Colors.Colors;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class PostStuff {

    public PostStuff(XC_LoadPackage.LoadPackageParam lpparam) {

        /*
         * Track posts #1
         * Set additional data on the TimeView of each Post to track the
         * user_handle / poster
         * Apply darker shade to OP's posts in a thread
         */
        findAndHookMethod("com.jodelapp.jodelandroidv3.view.adapter.RecyclerPostsAdapter", lpparam.classLoader, Hooks.PostStuff.RecyclerPostsAdapter.TrackPoster, "com.jodelapp.jodelandroidv3.view.adapter.RecyclerPostsAdapter$ViewHolder", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    List posts = (List) callMethod(param.thisObject, "getPosts");
                    Object post =  posts.get((int)param.args[1]);

                    if (getObjectField(post,"replierMark") != null && getObjectField(post,"replierMark").equals("OP")){
                        String color = (String) getObjectField(post, "color");
                        float[] hsv = new float[3];
                        int c = Color.parseColor("#" + color);
                        Color.colorToHSV(c, hsv);
                        hsv[2] *= 0.8f;
                        c = Color.HSVToColor(hsv);

                        if (getAdditionalInstanceField(post,"newColor")== null){
                            setAdditionalInstanceField(post,"newColor",Integer.toHexString(c).substring(2));
                            setObjectField(post, "color", Integer.toHexString(c).substring(2));
                        }

                    }
                } catch (IndexOutOfBoundsException ignored) {
                    //In case you reached the last available post (found on Mt. Everest)
                }

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object textView = getObjectField(param.args[0], Hooks.PostStuff.RecyclerPostsAdapter$ViewHolder.TimeView);
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
                if (id != null){
                    callMethod(param.thisObject, "append", " #" + id);
                }
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
                String colorField = null;

                for (Field f : param.thisObject.getClass().getDeclaredFields()){
                    if (f.getType().getName().equals(String.class.getName())){
                        if (f.toGenericString().startsWith("#")){
                            colorField = f.getName();
                        }
                    }
                }
                final String finalColorField = colorField;

                final Activity activity = Utils.getActivity(param);
                final int id = Utils.getIdentifierById(param,"cameraButton");

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
                                ((View) ((View) param.getResult()).findViewById(id).getParent().getParent()).setBackgroundColor(Color.parseColor(Colors.get(which)));
                                //set instance field
                                XposedHelpers.setObjectField(param.thisObject, finalColorField, Colors.get(which));
                                dialog.dismiss();
                            }
                        }).show();
                    }
                });

                LinearLayout linearLayout = (LinearLayout) ((View) param.getResult()).findViewById(id).getParent();
                linearLayout.addView(color);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            }
        });
    }
}
