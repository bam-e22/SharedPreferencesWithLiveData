package k.t.livedatasample.ui.main

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import k.t.livedatasample.data.ClassifiedEntry
import k.t.livedatasample.databinding.DialogEntryEditBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class EntryEditDialog(private val preferencesName: String, private val entry: ClassifiedEntry<*>) : DialogFragment(),
    CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private lateinit var binding: DialogEntryEditBinding

    private val entryListViewModel: EntryListViewModel by activityViewModels {
        EntryListViewModelFactory(
            this.requireContext(),
            preferencesName
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogEntryEditBinding.inflate(inflater)
        binding.title.text = entry.key
        binding.value.setText(entry.value.toString())
        binding.value.inputType = findImeType()
        binding.okButton.setOnClickListener {
            launch {
                val newValue = withContext(Dispatchers.IO) { newValue() }
                newValue?.let {
                    entryListViewModel.entries.setValue(entry.key, it)
                }
                it.clearFocus()
                dismiss()
            }
        }
        return binding.root
    }

    private fun findImeType(): Int = when (entry) {
        is ClassifiedEntry.IntEntry -> EditorInfo.TYPE_CLASS_NUMBER
        is ClassifiedEntry.LongEntry -> EditorInfo.TYPE_CLASS_NUMBER
        is ClassifiedEntry.FloatEntry -> EditorInfo.TYPE_CLASS_NUMBER
        is ClassifiedEntry.StringEntry -> EditorInfo.TYPE_CLASS_TEXT
        is ClassifiedEntry.StringSetEntry -> EditorInfo.TYPE_CLASS_TEXT
        else -> EditorInfo.TYPE_CLASS_TEXT
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun newValue(): Any? {
        return runCatching {
            val raw = binding.value.text.toString()
            when (entry) {
                is ClassifiedEntry.IntEntry -> raw.toInt()
                is ClassifiedEntry.LongEntry -> raw.toLong()
                is ClassifiedEntry.FloatEntry -> raw.toFloat()
                is ClassifiedEntry.StringEntry -> raw
                is ClassifiedEntry.StringSetEntry -> {
                    raw.drop(1).dropLast(1).split(",").map { it.trim() }.toSet()
                }
                else -> raw
            }
        }.getOrNull()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.attributes = dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
    }

}