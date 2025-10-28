package com.passwordmanager.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "password_manager_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun setMasterPasswordHash(hash: String) {
        sharedPreferences.edit().putString(KEY_MASTER_PASSWORD_HASH, hash).apply()
    }
    
    fun getMasterPasswordHash(): String? {
        return sharedPreferences.getString(KEY_MASTER_PASSWORD_HASH, null)
    }
    
    fun isMasterPasswordSet(): Boolean {
        return getMasterPasswordHash() != null
    }
    
    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }
    
    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }
    
    fun setAutoLockTimeout(minutes: Int) {
        sharedPreferences.edit().putInt(KEY_AUTO_LOCK_TIMEOUT, minutes).apply()
    }
    
    fun getAutoLockTimeout(): Int {
        return sharedPreferences.getInt(KEY_AUTO_LOCK_TIMEOUT, 5)
    }
    
    fun setLastUnlockTime(timestamp: Long) {
        sharedPreferences.edit().putLong(KEY_LAST_UNLOCK_TIME, timestamp).apply()
    }
    
    fun getLastUnlockTime(): Long {
        return sharedPreferences.getLong(KEY_LAST_UNLOCK_TIME, 0)
    }
    
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }
    
    companion object {
        private const val KEY_MASTER_PASSWORD_HASH = "master_password_hash"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_AUTO_LOCK_TIMEOUT = "auto_lock_timeout"
        private const val KEY_LAST_UNLOCK_TIME = "last_unlock_time"
    }
}