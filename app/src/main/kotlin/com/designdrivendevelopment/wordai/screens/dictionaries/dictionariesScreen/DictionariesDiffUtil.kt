package com.designdrivendevelopment.wordai.screens.dictionaries.dictionariesScreen

import androidx.recyclerview.widget.DiffUtil
import com.designdrivendevelopment.wordai.entities.Dictionary

class DictionariesDiffUtil : DiffUtil.ItemCallback<Dictionary>() {
    override fun areItemsTheSame(oldItem: Dictionary, newItem: Dictionary): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Dictionary, newItem: Dictionary): Boolean {
        return oldItem == newItem
    }
}
