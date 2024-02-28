package com.example.ai_language

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

class EncryptedSharedPreferencesManager (private val context: Context) {
    private val PREFERENCES_NAME = "my_preferences"
    lateinit var encryptedSharedPreferencesViewModel: EncryptedSharedPreferencesViewModel


    //마스터 키 - 데이터 암호화, 복호화 (
    //Android keystore system 사용해서 키를 앱 내부가 아닌 시스템만이 접근 가능한 컨테이너에 데이터 저장 (앱 외부에서 접근 불가능)
    private val masterKeyAlias = MasterKey
        .Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFERENCES_NAME,
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getPreferences(mContext: Context): SharedPreferences? {
        return mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun setLoginInfo(email: String, password: String) {
        //val prefs = getPreferences(context)
        val editor = encryptedSharedPreferences!!.edit()
        val viewModelProvider = ViewModelProvider(context as ViewModelStoreOwner)
        encryptedSharedPreferencesViewModel = viewModelProvider.get(EncryptedSharedPreferencesViewModel::class.java)

        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
        encryptedSharedPreferencesViewModel.setLoginItem(email, password)
    }

    fun getLoginInfo(): Map<String, String> {
        //val prefs = getPreferences(context)
        val loginInfo : MutableMap<String, String> = HashMap()
        val email = encryptedSharedPreferences!!.getString("email","")
        val password = encryptedSharedPreferences!!.getString("password","")

        email?.let { loginInfo["email"] = it }
        password?.let { loginInfo["password"] = it }

        return loginInfo
    }

    fun clearPreferences(context: Context) {
        //val prefs = getPreferences(context)
        val editor = encryptedSharedPreferences!!.edit()
        editor.clear()
        editor.apply()
    }

}
