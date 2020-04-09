package k.t.livedatasample.data

sealed class ClassifiedEntry<T>(val key: String, var value: T) {

    class BooleanEntry(key: String, value: Boolean) : ClassifiedEntry<Boolean>(key, value)
    class IntEntry(key: String, value: Int) : ClassifiedEntry<Int>(key, value)
    class LongEntry(key: String, value: Long) : ClassifiedEntry<Long>(key, value)
    class FloatEntry(key: String, value: Float) : ClassifiedEntry<Float>(key, value)
    class StringEntry(key: String, value: String) : ClassifiedEntry<String>(key, value)
    class StringSetEntry(key: String, value: Set<String>) : ClassifiedEntry<Set<String>>(key, value)
    class UnknownTypeEntry(key: String, value: Any?) : ClassifiedEntry<Any?>(key, value)

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun classify(key: String, value: Any?): ClassifiedEntry<*> {
            return runCatching {
                when (value) {
                    is Boolean -> BooleanEntry(key, value)
                    is Int -> IntEntry(key, value)
                    is Long -> LongEntry(key, value)
                    is Float -> FloatEntry(key, value)
                    is String -> StringEntry(key, value)
                    is Set<*> -> StringSetEntry(key, value as Set<String>)
                    else -> UnknownTypeEntry(key, value)
                }
            }.getOrDefault(
                UnknownTypeEntry(key, value)
            )
        }
    }
}

fun Pair<String, Any?>.classify() =
    ClassifiedEntry.classify(
        this.first,
        this.second
    )