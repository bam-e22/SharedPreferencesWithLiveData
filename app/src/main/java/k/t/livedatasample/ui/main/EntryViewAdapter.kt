package k.t.livedatasample.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import k.t.livedatasample.databinding.ItemEntryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EntryViewAdapter(private val clickAction: (Pair<String, Any?>) -> Unit) :
    RecyclerView.Adapter<EntryViewAdapter.EntryViewHolder>() {
    private var entries = linkedMapOf<String, Any?>()

    suspend fun setEntries(newEntries: LinkedHashMap<String, Any?>) {
        val diffResult = withContext(Dispatchers.IO) {
            DiffUtil.calculateDiff(EntriesDiffCallback(entries, newEntries), false)
        }

        withContext(Dispatchers.Main) {
            diffResult.dispatchUpdatesTo(this@EntryViewAdapter)
        }

        withContext(Dispatchers.IO) {
            entries.clear()
            entries.putAll(newEntries)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        return EntryViewHolder(ItemEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(entries.toList()[position], clickAction)
    }

    override fun getItemCount() = entries.size

    class EntryViewHolder(private val binding: ItemEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: Pair<String, Any?>, clickAction: (Pair<String, Any?>) -> Unit) {
            binding.keyTextView.text = entry.first
            binding.valueTextView.text = entry.second?.toString()

            binding.root.setOnClickListener {
                clickAction.invoke(entry)
            }
        }
    }

    private class EntriesDiffCallback(
        private val oldEntries: LinkedHashMap<String, Any?>,
        private val newEntries: LinkedHashMap<String, Any?>
    ) : DiffUtil.Callback() {

        private val oldEntriesList = oldEntries.toList()
        private val newEntriesList = newEntries.toList()

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldEntriesList[oldItemPosition].first == newEntriesList[newItemPosition].first
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldEntriesList[oldItemPosition] == newEntriesList[newItemPosition]
        }

        override fun getOldListSize() = oldEntriesList.size

        override fun getNewListSize() = newEntriesList.size
    }
}