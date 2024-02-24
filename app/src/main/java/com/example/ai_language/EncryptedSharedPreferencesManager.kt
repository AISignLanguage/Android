package com.example.ai_language

import android.content.Context
import android.content.SharedPreferences

class EncryptedSharedPreferencesManager {
    private val PREFERENCES_NAME = "my_preferences"

    fun getPreferences(mContext: Context): SharedPreferences? {
        return mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun setLoginInfo(context: Context, email: String, password: String) {
        val prefs = getPreferences(context)
        val editor = prefs!!.edit()

        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    fun getLoginInfo(context: Context): Map<String, String> {
        val prefs = getPreferences(context)
        val loginInfo : MutableMap<String, String> = HashMap()
        val email = prefs!!.getString("email","")
        val password = prefs!!.getString("password","")

        if (email != null) {
            loginInfo.put("email", email)
        }
        if (password != null) {
            loginInfo.put("password", password)
        }

        return loginInfo
    }

    fun clearPreferences(context: Context) {
        val prefs = getPreferences(context)
        val editor = prefs!!.edit()
        editor.clear()
        editor.apply()
    }

}
