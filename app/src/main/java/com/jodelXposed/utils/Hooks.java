package com.jodelXposed.utils;

import android.widget.ImageView;

import com.jodelXposed.hooks.AntiAntiXposed;
import com.jodelXposed.hooks.BetaStuff;
import com.jodelXposed.hooks.GCMStuff;
import com.jodelXposed.hooks.LocationStuff;
import com.jodelXposed.hooks.ThemeStuff;
import com.jodelXposed.hooks.UniqueDeviceIdentifierStuff;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hooks {

    private XC_LoadPackage.LoadPackageParam loadPackageParam;

    public Hooks(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.loadPackageParam = loadPackageParam;
    }


    public static class PostStuff{

        public static class RecyclerPostsAdapter$ViewHolder {
            public static String TimeView;
        }

        public static class RecyclerPostsAdapter {
            public static String TrackPoster;
        }
    }

    public static class SettingsStuff{
        public static class MyMenuItem {
            public static String displayName;
            public static String RandomIntValue;
        }

        public static class MyMenuFragment {
            public static String AddEntriesMethod;
        }
    }

    public static class JodelApp{
        public static String FirstMethod = null;
        public static String SecondMethod = null;
    }

    public static class ImageStuff{
        public static String ImageView;
    }

    public static class UDI {
        public static String GetUID;
    }

    public void findHooks(){
        XC_LoadPackage.LoadPackageParam param = loadPackageParam;
        try {
            Log.dlog("**** Searching for AntiAntiXposed hooks ****");
            antiAntiXposed(param);
            Log.dlog("**** Found AntiAntiXposed hooks! ****");
        }catch(Exception e){
            Log.dlog("**** Searching for AntiAntiXposed hooks FAILED! ****");
        }

        try {
            Log.dlog("**** Searching for PostStuff hooks ****");
            postStuff(param);
            Log.dlog("**** Found PostStuff hooks! ****");
        }catch(Exception e){
            Log.dlog("**** Searching for PostStuff hooks FAILED! ****");
        }

        try {
            Log.dlog("**** Searching for ImageStuff hooks ****");
            imageStuff(param);
            Log.dlog("**** Found ImageStuff hooks! ****");
        }catch(Exception e){
            Log.dlog("**** Searching for ImageStuff hooks FAILED! ****");
        }

        try {
            Log.dlog("**** Searching for SettingsStuff hooks ****");
            settingsStuff(param);
            Log.dlog("**** Found SettingsStuff hooks! ****");
        }catch(Exception e){
            Log.dlog("**** Searching for SettingsStuff hooks FAILED! ****");
        }

        try {
            Log.dlog("**** Searching for UniqueDeviceIdentifier hooks ****");
            udiStuff(param);
            Log.dlog("**** Found UniqueDeviceIdentifier hooks! ****");
        }catch(Exception e){
            Log.dlog("**** Searching for UniqueDeviceIdentifier hooks FAILED! ****");
        }
    }

    public void hook(){
        XC_LoadPackage.LoadPackageParam lpparam = loadPackageParam;
        try {
            Log.dlog("#### Loading AntiAntiXposed hooks ####");
            new AntiAntiXposed(lpparam);
            Log.dlog("#### AntiAntiXposed hooks loaded! ####");
        }catch(Exception e){
            Log.dlog("!!!! FAILED loading AntiAntiXposed hooks! !!!!\n" +
                "\n");
        }

        try {
            Log.dlog("#### Loading BetaStuff hooks ####");
            new BetaStuff(lpparam);
            Log.dlog("#### BetaStuff hooks loaded! ####");
        }catch(Exception e){
            Log.dlog("!!!! FAILED loading BetaStuff hooks! !!!!\n\n");
        }

        try {
            Log.dlog("#### Loading ImageStuff hooks ####");
            new com.jodelXposed.hooks.ImageStuff(lpparam);
            Log.dlog("#### ImageStuff hooks loaded! ####");
        }catch(Exception e){
            Log.dlog("!!!! FAILED loading ImageStuff hooks! !!!!\n" +
                "\n");
        }

        try {
            Log.dlog("#### Loading LocationStuff hooks ####");
            new LocationStuff(lpparam);
            Log.dlog("#### LocationStuff hooks loaded! ####");
        }catch(Exception e){
            Log.dlog("!!!! FAILED loading LocationStuff hooks! !!!!\n" +
                "\n");
        }

        try {
            Log.dlog("#### Loading PostStuff hooks ####");
            new com.jodelXposed.hooks.PostStuff(lpparam);
            Log.dlog("#### PostStuff hooks loaded! ####");
        }catch(Exception e){
            Log.dlog("!!!! FAILED loading PostStuff hooks! !!!!\n" +
                "\n");
        }

        try {
            Log.dlog("#### Loading SettingsStuff hooks ####");
            new com.jodelXposed.hooks.SettingsStuff(lpparam);
            Log.dlog("#### SettingsStuff hooks loaded! ####");
        }catch(Exception e){
            Log.dlog("!!!! FAILED loading SettingsStuff hooks! !!!!\n\n");
        }

        try {
            Log.dlog("#### Loading UniqueDeviceIdentifierStuff hooks ####");
            new UniqueDeviceIdentifierStuff(lpparam);
            Log.dlog("#### UniqueDeviceIdentifierStuff hooks loaded! ####");
        }catch(Exception e){
            Log.dlog("!!!! FAILED loading UniqueDeviceIdentifierStuff hooks! !!!!\n\n");
        }

        try {
            Log.dlog("#### Loading ThemeStuff hooks ####");
            new ThemeStuff(lpparam);
            Log.dlog("#### ThemeStuff hooks loaded! ####");
        }catch(Exception e){
            Log.dlog("!!!! FAILED loading ThemeStuff hooks! !!!!\n\n");
        }

        try {
            Log.dlog("#### Loading GCMStuff hooks ####");
            new GCMStuff(lpparam);
            Log.dlog("#### GCMStuff hooks loaded! ####");
        }catch(Exception e){
            Log.dlog("!!!! FAILED loading GCMStuff hooks! !!!!\n\n");
        }
    }

    private void udiStuff(XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> UDIClass = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.utilities.UniqueDeviceIdentifier",lpparam.classLoader);
        for (Method m : UDIClass.getDeclaredMethods()){
            if (m.getModifiers() == Modifier.PUBLIC && m.getReturnType().getName().equals(String.class.getName()) && !m.getName().equals("getValue")){
                UDI.GetUID = m.getName();
            }
        }
    }

    private void postStuff(XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> RecyclerPostsAdapterClass = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.view.adapter.RecyclerPostsAdapter",lpparam.classLoader);
        Class<?> ViewHolder = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.view.adapter.RecyclerPostsAdapter$ViewHolder",lpparam.classLoader);
        Class<?> TimeView = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.view.TimeView",lpparam.classLoader);

        for (Method m : RecyclerPostsAdapterClass.getDeclaredMethods()){
            if (m.getReturnType().getName().equals(void.class.getName())){
                if (m.getParameterTypes().length == 2){
                    if (m.getParameterTypes()[0].getName().equals(ViewHolder.getName())){
                        if (m.getParameterTypes()[1].getName().equals(int.class.getName())){
                            PostStuff.RecyclerPostsAdapter.TrackPoster = m.getName();
                        }
                    }
                }
            }
        }

        for (Field f : ViewHolder.getDeclaredFields()){
            if (f.getType().getName().equals(TimeView.getName())){
                PostStuff.RecyclerPostsAdapter$ViewHolder.TimeView = f.getName();
            }
        }
    }

    private void antiAntiXposed(XC_LoadPackage.LoadPackageParam lpparam){

        Class<?> JodelAppClass = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.JodelApp",lpparam.classLoader);

        for (Method m : JodelAppClass.getDeclaredMethods()){
            if (m.getReturnType() == void.class && m.getParameterTypes().length == 0 && !m.getName().equals("onCreate")){
                if (JodelApp.FirstMethod == null){
                    JodelApp.FirstMethod = m.getName();
                }else{
                    JodelApp.SecondMethod = m.getName();
                }
            }
        }
    }

    private void settingsStuff(XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> MyMenuItemClass = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.view.MyMenuItem",lpparam.classLoader);
        Class<?> MyMenuFragmentClass = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.view.MyMenuFragment",lpparam.classLoader);

        for (Field f : MyMenuItemClass.getDeclaredFields()){
            if (f.getType().getName().equals(String.class.getName()) && !f.getName().equals("name")){
                SettingsStuff.MyMenuItem.displayName = f.getName();
            }else if (f.getType().getName().equals(int.class.getName())){
                SettingsStuff.MyMenuItem.RandomIntValue = f.getName();
            }
        }

        for (Method m : MyMenuFragmentClass.getDeclaredMethods()){
            if (m.getModifiers() == Modifier.SYNCHRONIZED + Modifier.PRIVATE){
                SettingsStuff.MyMenuFragment.AddEntriesMethod = m.getName();
            }
        }
    }

    private void imageStuff(XC_LoadPackage.LoadPackageParam lpparam){
        Class<?> clazz = XposedHelpers.findClass("com.jodelapp.jodelandroidv3.view.PhotoEditFragment",lpparam.classLoader);
        for (Field f : clazz.getDeclaredFields()){
            if (f.getType().getName().equals(ImageView.class.getName())){
                ImageStuff.ImageView = f.getName();
                break;
            }
        }
    }
}
