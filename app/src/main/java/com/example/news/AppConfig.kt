package com.example.news

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class AppConfig(context: Context){

    private val KEY_PAGE = "key page"

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    companion object {
        private var instance: AppConfig? = null

        fun getInstance(context: Context): AppConfig {
            if (instance == null) {
                instance = AppConfig(context)
            }
            return instance as AppConfig
        }
    }

    @SuppressLint("CommitPrefEdits")
    public fun setNewsPage(page: String?) {
        sharedPreferences.edit().putString(KEY_PAGE, page).apply()
    }

    public fun getNewsPage(): String {
        return sharedPreferences.getString(KEY_PAGE, "VN Express")!!
    }

}