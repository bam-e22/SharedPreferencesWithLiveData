package k.t.livedatasample.ui.main

import android.content.SharedPreferences
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import k.t.livedatasample.utils.getEntry
import k.t.livedatasample.utils.setEntry

class PrefMutableLiveData(private val sharedPreference: SharedPreferences) :
    MutableLiveData<LinkedHashMap<String, Any?>>() {

    override fun postValue(value: LinkedHashMap<String, Any?>?) {
        applyChanges(value)
        super.postValue(value)
    }

    fun getValue(key: String): Any? {
        return value?.get(key)
    }

    @MainThread
    fun setValue(key: String, newValue: Any?) {
        val items = value ?: linkedMapOf()
        items[key] = newValue
        this.value = items
    }

    @MainThread
    fun postValue(key: String, newValue: Any?) {
        val items = value ?: linkedMapOf()
        items[key] = newValue
        this.postValue(items)
    }

    private fun applyChanges(value: LinkedHashMap<String, Any?>?) {
        with(sharedPreference) {
            value?.forEach {
                when {
                    !contains(it.key) && it.value != null -> {
                        setEntry(it.key, it.value)
                    }
                    contains(it.key) && getEntry(it.key, it.value) != it.value -> {
                        setEntry(it.key, it.value)
                    }
                    contains(it.key) && it.value == null -> {
                        setEntry(it.key, it.value)
                    }
                }
            }
        }
    }

    override fun onActive() {
        super.onActive()
        this.value = sharedPreference.all
            .map { it.key to it.value }
            .toMap(linkedMapOf())
    }
}