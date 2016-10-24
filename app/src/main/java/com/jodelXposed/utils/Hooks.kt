package com.jodelXposed.utils

import com.jodelXposed.hooks.BetaStuff
import com.jodelXposed.hooks.ImageStuff
import com.jodelXposed.hooks.LocationStuff
import com.jodelXposed.hooks.PostStuff
import com.jodelXposed.hooks.SettingsStuff
import com.jodelXposed.hooks.UniqueDeviceIdentifierStuff
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hooks(private val lpparam: LoadPackageParam) {

    fun hook() {
        try {
            Log.dlog("#### Loading BetaStuff hooks ####")
            BetaStuff(lpparam)
            Log.dlog("#### BetaStuff hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.dlog("!!!! FAILED loading BetaStuff hooks! !!!!\n\n")
        }

        try {
            Log.dlog("#### Loading ImageStuff hooks ####")
            ImageStuff(lpparam)
            Log.dlog("#### ImageStuff hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.dlog("!!!! FAILED loading ImageStuff hooks! !!!!\n" + "\n")
        }

        try {
            Log.dlog("#### Loading LocationStuff hooks ####")
            LocationStuff(lpparam)
            Log.dlog("#### LocationStuff hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.dlog("!!!! FAILED loading LocationStuff hooks! !!!!\n" + "\n")
        }

        try {
            Log.dlog("#### Loading PostStuff hooks ####")
            PostStuff(lpparam)
            Log.dlog("#### PostStuff hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.dlog("!!!! FAILED loading PostStuff hooks! !!!!\n" + "\n")
        }

        try {
            Log.dlog("#### Loading SettingsStuff hooks ####")
            SettingsStuff(lpparam)
            Log.dlog("#### SettingsStuff hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.dlog("!!!! FAILED loading SettingsStuff hooks! !!!!\n\n")
        }

        try {
            Log.dlog("#### Loading UniqueDeviceIdentifierStuff hooks ####")
            UniqueDeviceIdentifierStuff(lpparam)
            Log.dlog("#### UniqueDeviceIdentifierStuff hooks loaded! ####")
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.dlog("!!!! FAILED loading UniqueDeviceIdentifierStuff hooks! !!!!\n\n")
        }
    }
}
