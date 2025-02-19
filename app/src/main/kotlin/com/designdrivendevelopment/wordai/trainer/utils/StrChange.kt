package com.designdrivendevelopment.wordai.trainer.utils

enum class StrChange {
    KEEP, INSERT, REPLACE, DELETE
}
typealias WordChangeArray = Array<Pair<Char, StrChange>>
