package com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes

abstract class CommonItem<out T : Any?> : ItemWithType {
    abstract val data: T
}
