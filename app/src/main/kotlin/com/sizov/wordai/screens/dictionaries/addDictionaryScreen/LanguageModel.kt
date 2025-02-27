package com.sizov.wordai.screens.dictionaries.addDictionaryScreen

import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.selection.SelectableItem

data class LanguageModel(
    val title: String,
    override val isSelected: Boolean = false,
    override val isPartOfSelection: Boolean = false,
) : SelectableItem
