package k.t.livedatasample.ui.main

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import java.util.LinkedHashMap

class EntryListViewModel(preferences: SharedPreferences) : ViewModel() {
    val entries = PrefMutableLiveData(preferences)

    val searchKeyword = MutableLiveData<String>()

    val filteredEntries: MediatorLiveData<LinkedHashMap<String, Any?>> = searchKeyword
        .combineAndCompute(entries) { keyword, all ->
            all.filterTo(LinkedHashMap()) { entry: Map.Entry<String, Any?> ->
                val grainedKeyword = keyword.trim()
                if (grainedKeyword.isNotBlank()) {
                    entry.key.contains(grainedKeyword)
                            || (entry.value?.toString()?.contains(grainedKeyword) == true)
                } else {
                    true
                }
            }
        }
}

@Suppress("UNCHECKED_CAST")
class EntryListViewModelFactory(
    private val context: Context,
    private val preferenceName: String): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EntryListViewModel(
            context.applicationContext.getSharedPreferences(
                preferenceName,
                Context.MODE_PRIVATE
            )
        ) as T
    }
}

/**
 * modified version http://kko.to/lab3Ucw0T
 */
fun <T> LiveData<String?>.combineAndCompute(
    other: LiveData<LinkedHashMap<String, Any?>?>,
    onChange: (String, LinkedHashMap<String, Any?>) -> T
): MediatorLiveData<T> {

    var source1emitted = false
    var source2emitted = false

    val result = MediatorLiveData<T>()

    val mergeFunction = {
        val source1Value = this.value ?: ""
        val source2Value = other.value ?: LinkedHashMap()

        if (source1emitted || source2emitted) {
            result.value = onChange.invoke(source1Value, source2Value)
        }
    }

    result.addSource(this) {
        source1emitted = true
        mergeFunction.invoke()
    }
    result.addSource(other) {
        source2emitted = true
        mergeFunction.invoke()
    }

    return result
}
