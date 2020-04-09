package k.t.livedatasample.utils

import android.content.SharedPreferences
import androidx.core.content.edit

@Suppress("UNCHECKED_CAST")
fun SharedPreferences.getEntry(key: String, defaultValue: Any?): Any? {
    return when (defaultValue) {
        null -> null
        is Boolean -> getBoolean(key, defaultValue)
        is Int -> getInt(key, defaultValue)
        is Long -> getLong(key, defaultValue)
        is Float -> getFloat(key, defaultValue)
        is String -> getString(key, defaultValue) as String
        is Set<*> -> getStringSet(key, defaultValue as Set<String>) as Set<String>
        else -> throw IllegalArgumentException("Unsupported type : ${defaultValue::class.java.simpleName}")
    }
}

@Suppress("UNCHECKED_CAST")
fun SharedPreferences.setEntry(key: String, value: Any?) {
    return when (value) {
        null -> edit { remove(key) }
        is Boolean -> edit { putBoolean(key, value) }
        is Int -> edit { putInt(key, value) }
        is Long -> edit { putLong(key, value) }
        is Float -> edit { putFloat(key, value) }
        is String -> edit { putString(key, value) }
        is Set<*> -> edit { putStringSet(key, value as Set<String>) }
        else -> throw IllegalArgumentException("Unsupported type: ${value::class.java.simpleName}")
    }
}