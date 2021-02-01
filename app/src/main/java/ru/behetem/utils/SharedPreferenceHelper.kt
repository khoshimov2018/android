package ru.behetem.utils

import android.content.Context

object SharedPreferenceHelper {

    fun saveStringToShared(context: Context, key: String, value: String){
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun getStringFromShared(context: Context, key: String): String? {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    fun saveBooleanToShared(context: Context, key: String, value: Boolean){
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            commit()
        }
    }

    fun getBooleanFromShared(context: Context, key: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, false)
    }

    fun clearShared(context: Context) {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}