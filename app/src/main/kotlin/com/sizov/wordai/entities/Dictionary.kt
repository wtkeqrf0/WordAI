package com.sizov.wordai.entities

data class Dictionary(
    val id: Long,
    val label: String,
    val size: Int,
    var isFavorite: Boolean,
    val language: String
) {
    companion object {
        const val NEW_DICTIONARY_ID = 0L
        const val DEFAULT_DICT_ID = -1L
        const val SIZE_EMPTY = 0
    }
}
