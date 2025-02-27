package com.sizov.wordai.screens.dictionaries.addDictionaryScreen

import com.sizov.wordai.entities.WordDefinition
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.selection.SelectableItem

data class SelectableWordDefinition(
    val def: WordDefinition,
    override val isSelected: Boolean = false,
    override val isPartOfSelection: Boolean = false
) : SelectableItem
