package com.sizov.wordai.persistence.queryResults

import androidx.room.ColumnInfo

data class DictionaryQueryResult(
    @ColumnInfo(name = "dict_id")
    val id: Long,
    @ColumnInfo(name = "label")
    val label: String,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,
    @ColumnInfo(name = "size")
    val size: Int,
    @ColumnInfo(name = "language")
    val language: String
)
