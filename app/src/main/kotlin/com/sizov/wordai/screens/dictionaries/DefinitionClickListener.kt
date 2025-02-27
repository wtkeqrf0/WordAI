package com.sizov.wordai.screens.dictionaries

import com.sizov.wordai.entities.WordDefinition

interface DefinitionClickListener {
    fun onClickToDefinition(wordDefinition: WordDefinition)
}
