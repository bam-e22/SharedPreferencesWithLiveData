package k.t.livedatasample

import android.content.Context
import k.t.livedatasample.utils.setEntry

class SampleMaker {

    companion object {
        fun generate(context: Context) {

            with(
                context.getSharedPreferences(
                    MainActivity.sharedPreferenceName,
                    Context.MODE_PRIVATE
                )
            ) {
                setEntry("booleanEntry", true)
                setEntry("intEntry", 999)
                setEntry("longEntry", 8888L)
                setEntry("floatEntry", 777.666f)
                setEntry("stringEntry", "kind of september")
                setEntry("stringSetEntry", setOf("stay", "while", "and", "listen"))
            }
        }
    }
}