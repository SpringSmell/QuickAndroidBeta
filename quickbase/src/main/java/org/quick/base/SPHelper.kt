package org.quick.base

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import java.io.Serializable

/**
 * Created by chris zou on 2016/7/29.
 *
 * @author ChrisZou
 * @describe 快速使用SharedPreferences
 * @date 2016/7/29
 * @from https://github.com/SpringSmell/quick.library
 * @email chrisSpringSmell@gmail.com
 */
object SPHelper {

    private val mSharedPreferences: SharedPreferences by lazy { return@lazy QuickAndroid.applicationContext.getSharedPreferences(QuickAndroid.appBaseName, Context.MODE_PRIVATE) }
    private val mEditor: SharedPreferences.Editor = mSharedPreferences.edit()

    fun putValue(key: String, value: String): SPHelper {
        mEditor.putString(key, value).commit()
        return this
    }

    fun putValue(key: String, value: Boolean): SPHelper {
        mEditor.putBoolean(key, value).commit()
        return this
    }

    fun putValue(key: String, value: Float): SPHelper {
        mEditor.putFloat(key, value).commit()
        return this
    }

    fun putValue(key: String, value: Double): SPHelper {
        mEditor.putFloat(key, value.toFloat()).commit()
        return this
    }

    fun putValue(key: String, value: Long): SPHelper {
        mEditor.putLong(key, value).commit()
        return this
    }

    fun putValue(key: String, value: Int): SPHelper {
        mEditor.putInt(key, value).commit()
        return this
    }

    fun putValue(key: String, value: Set<String>): SPHelper {
        mEditor.putStringSet(key, value).commit()
        return this
    }

    fun <T> getValue(key: String, defaultValue: T): T = try {
        if (all[key] == null) defaultValue else when(defaultValue){
            is String -> all[key].toString() as T
            is Int -> all[key].toString().toInt() as T
            is Boolean -> all[key].toString().toBoolean() as T
            is Long -> all[key].toString().toLong() as T
            is Float -> all[key].toString().toFloat() as T
            is Double -> all[key].toString().toDouble() as T

            is Serializable -> all[key].toString() as T
            is java.util.ArrayList<*> -> all[key] as T
            is Bundle -> all[key].toString() as T

            else -> all[key] as T
        }
    } catch (O_o: Exception) {
        Log.e(SPHelper::class.java.simpleName, "Cannot convert with defaultValue type")
        defaultValue
    }

    fun clearAll() {
        mEditor.clear().commit()
    }

    fun removeValue(key: String): Boolean {
        return mEditor.remove(key).commit()
    }

    fun getSet(key: String, defValues: Set<String>): Set<String>? {
        return mSharedPreferences.getStringSet(key, defValues)
    }

    val all: Map<String, *> get() = mSharedPreferences.all
}
