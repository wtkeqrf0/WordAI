package com.sizov.wordai.screens.dictionaries.addDictionaryScreen

import androidx.recyclerview.widget.DiffUtil

class SelectableLanguageDiffCallback(
    private val oldList: List<LanguageModel>,
    private val newList: List<LanguageModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
