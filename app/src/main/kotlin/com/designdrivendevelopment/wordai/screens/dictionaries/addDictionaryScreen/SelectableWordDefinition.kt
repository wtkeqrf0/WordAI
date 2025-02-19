package com.designdrivendevelopment.wordai.screens.dictionaries.addDictionaryScreen

import com.designdrivendevelopment.wordai.entities.WordDefinition
import com.designdrivendevelopment.wordai.screens.dictionaries.lookupWordDefinitionsScreen.selection.SelectableItem

data class SelectableWordDefinition(
    val def: WordDefinition,
    override val isSelected: Boolean = false,
    override val isPartOfSelection: Boolean = false
) : SelectableItem
