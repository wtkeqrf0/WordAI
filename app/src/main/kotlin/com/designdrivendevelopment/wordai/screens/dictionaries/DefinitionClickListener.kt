package com.designdrivendevelopment.wordai.screens.dictionaries

import com.designdrivendevelopment.wordai.entities.WordDefinition

interface DefinitionClickListener {
    fun onClickToDefinition(wordDefinition: WordDefinition)
}
