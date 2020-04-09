package k.t.livedatasample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import k.t.livedatasample.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        launch {
            SampleMaker.generate(this@MainActivity)
        }

        binding.buttonNext.setOnClickListener {
            startActivity(EntryListActivity.newInstance(this@MainActivity, sharedPreferenceName))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    companion object {
        const val sharedPreferenceName = "sample"
    }
}
