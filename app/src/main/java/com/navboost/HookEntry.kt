package com.navboost

import android.media.AudioManager
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HookEntry : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!lpparam.packageName.contains("autonavi") &&
            !lpparam.packageName.contains("baidu")) return

        Config.loadConfig()

        try {
            val cls = XposedHelpers.findClass("android.media.AudioTrack", lpparam.classLoader)
            XposedHelpers.findAndHookMethod(cls, "play", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    Config.loadConfig()
                    try {
                        val audioManager = XposedHelpers.callStaticMethod(
                            XposedHelpers.findClass("android.media.AudioSystem", null),
                            "getAudioService"
                        ) as? AudioManager

                        for (i in 1..Config.gainDb) {
                            audioManager?.adjustStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_RAISE,
                                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
                            )
                        }

                        Config.log("导航音量增强 +${Config.gainDb}dB 成功")
                    } catch (e: Throwable) {
                        Log.e("NavBoost", "音量调整失败", e)
                    }
                }
            })
        } catch (e: Throwable) {
            Log.e("NavBoost", "Hook 导航失败", e)
        }
    }
}