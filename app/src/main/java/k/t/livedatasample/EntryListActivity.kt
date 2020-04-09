package k.t.livedatasample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import k.t.livedatasample.data.ClassifiedEntry
import k.t.livedatasample.data.classify
import k.t.livedatasample.databinding.ActivityEntryListBinding
import k.t.livedatasample.ui.main.EntryEditDialog
import k.t.livedatasample.ui.main.EntryListViewModel
import k.t.livedatasample.ui.main.EntryListViewModelFactory
import k.t.livedatasample.ui.main.EntryViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EntryListActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityEntryListBinding.inflate(layoutInflater)
    }

    private val preferenceName by lazy {
        requireNotNull(intent.getStringExtra(PREF_NAME))
    }

    private val entryViewAdapter = EntryViewAdapter {
        launch {
            handleItemClick(it)
        }
    }

    private val entryListViewModel: EntryListViewModel by viewModels {
        EntryListViewModelFactory(
            this,
            preferenceName
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.searchText.doAfterTextChanged {
            entryListViewModel.searchKeyword.value = it.toString()
        }

        binding.entriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = entryViewAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        entryListViewModel.filteredEntries.observe(this, Observer {
            launch {
                entryViewAdapter.setEntries(it)
            }
        })
    }

    private fun handleItemClick(it: Pair<String, Any?>) {
        when (val entry = it.classify()) {
            is ClassifiedEntry.BooleanEntry -> {
                entryListViewModel.entries.setValue(entry.key, !entry.value)
            }
            else -> {
                EntryEditDialog(
                    preferenceName,
                    entry
                ).show(
                    supportFragmentManager,
                    EntryEditDialog::class.java.simpleName
                )
            }
        }
    }

    companion object {
        private const val PREF_NAME = "PREF_NAME"
        fun newInstance(context: Context, sharedPreferenceName: String): Intent {
            return Intent(context, EntryListActivity::class.java)
                .putExtra(PREF_NAME, sharedPreferenceName)
        }
    }
}
