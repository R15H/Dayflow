package com.alextra.tools

import android.content.Context
import android.content.SharedPreferences

class Storage private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    }

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    // Add similar functions for other data types as needed (e.g., Boolean, Long, etc.)

    companion object {
        private const val PREFS_FILENAME = "my_app_preferences"
        private var instance: Storage? = null

        fun getInstance(context: Context): Storage {
            if (instance == null) {
                instance = Storage(context)
            }
            return instance!!
        }
    }
}