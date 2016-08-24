package com.jodelXposed.hooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.jodelXposed.utils.Hooks;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.R.layout.simple_list_item_1;
import static com.jodelXposed.utils.Utils.Colors.Colors;
import static com.jodelXposed.utils.Utils.getActivity;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

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

                final Activity activity = getActivity(param);
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

        findAndHookMethod("com.jodelapp.jodelandroidv3.view.PostDetailFragment", lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                final SharedPreferences sharedPref = getActivity(param).getPreferences(Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPref.edit();

                final String postID = (String)XposedHelpers.getObjectField(param.thisObject,"postId");

                final Switch sw = (Switch) ((View)param.getResult()).findViewWithTag("sw_gcm_notification");
                sw.setVisibility(View.VISIBLE);
                sw.setChecked(!Options.getInstance().getBetaObject().getNotificationList().contains(postID));
                int color = 0;
                try {
                    color = Color.parseColor((String)getObjectField(param.thisObject,"axS"));
                    switchColor(sw,sw.isChecked(),color);
                    final int finalColor = color;
                    sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (!sharedPref.getBoolean("displayedNotificationExplanation", false)){
                                new AlertDialog.Builder(getActivity(param)).setTitle("You discovered a new Feature!")
                                    .setMessage("You discovered a new JodelXposed feature, the disabling of notifications in single threads. So now you have the possibility to mute specific threads which get annoying.")
                                    .setPositiveButton("Okay, dont display again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            editor.putBoolean("displayedNotificationExplanation", true).apply();
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                            }
                            switchColor(sw,b, finalColor);
                            if (b){
                                Options.getInstance().getBetaObject().getNotificationList().remove(postID);
                            }else{
                                Options.getInstance().getBetaObject().getNotificationList().add(postID);
                            }
                            Options.getInstance().save();
                        }
                    });
                }catch(IllegalArgumentException ignored){

                }
            }
        });

//        findAndHookMethod()

    }
    private void switchColor(Switch sw, boolean checked, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sw.getThumbDrawable().setColorFilter(checked ? color : Color.GRAY, PorterDuff.Mode.MULTIPLY);
            sw.getTrackDrawable().setColorFilter(!checked ? color : Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }
    }
}
