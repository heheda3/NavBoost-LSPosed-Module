package com.navboost

import android.util.Log
import java.io.File
import java.util.*

object Config {
    var gainDb: Int = 4
    var logEnabled: Boolean = true

    private const val CONFIG_PATH = "/sdcard/Android/data/com.navboost.lsposed/config.ini"

    fun loadConfig() {
        try {
            val file = File(CONFIG_PATH)
            if (!file.exists()) {
                log("配置文件不存在，使用默认设置")
                return
            }
            val props = Properties().apply { load(file.inputStream()) }

            gainDb = props.getProperty("gain_db")?.toIntOrNull() ?: 4
            logEnabled = props.getProperty("log_enabled")?.toBooleanStrictOrNull() ?: true

            log("配置加载成功: gainDb=$gainDb, logEnabled=$logEnabled")
        } catch (e: Throwable) {
            Log.e("NavBoost", "配置文件读取失败", e)
        }
    }

    fun log(msg: String) {
        if (logEnabled) Log.i("NavBoost", msg)
    }
}