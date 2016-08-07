package com.jodelXposed.utils;

import de.robv.android.xposed.XposedBridge;

public class Log {
    public static void xlog(String s) {
       try {
           String TAG;
           StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
           TAG = String.format("%s@%s JodelXposed",
               stElements[3].getClassName(),
               stElements[3].getMethodName());
           try {
               Class.forName("de.robv.android.xposed.XposedBridge", false, Log.class.getClassLoader());
               de.robv.android.xposed.XposedBridge.log(TAG + ": " + s);
           } catch (ClassNotFoundException e) {
               android.util.Log.i(TAG, s);
           }
       }catch(ArrayIndexOutOfBoundsException ignored){
           XposedBridge.log(s);
       }
    }
}
