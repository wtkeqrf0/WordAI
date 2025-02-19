package com.designdrivendevelopment.wordai.screens.dictionaries.lookupWordDefinitionsScreen.selection

import com.designdrivendevelopment.wordai.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.CategoryHeaderItem
import com.designdrivendevelopment.wordai.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemWithType
import com.designdrivendevelopment.wordai.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.WordDefinitionItem

val ItemWithType.stringKey: String
    get() = when (this) {
        is CategoryHeaderItem -> {
            DefinitionsKeyProvider.HEADER_KEY_SUBSTRING + this.header
        }
        is WordDefinitionItem -> {
            this.data.toString()
        }
        else -> {
            throw IllegalArgumentException("Undefined item")
        }
    }
