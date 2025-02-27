package com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.selection

import androidx.recyclerview.selection.ItemKeyProvider
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.ItemWithTypesAdapter

class DefinitionsKeyProvider(
    private val itemWithTypesAdapter: ItemWithTypesAdapter
) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int): String {
        return itemWithTypesAdapter.items[position].stringKey
    }

    override fun getPosition(key: String): Int {
        return itemWithTypesAdapter.items.indexOfFirst { item -> item.stringKey == key }
    }

    companion object {
        const val HEADER_KEY_SUBSTRING = "item_header_"
    }
}
