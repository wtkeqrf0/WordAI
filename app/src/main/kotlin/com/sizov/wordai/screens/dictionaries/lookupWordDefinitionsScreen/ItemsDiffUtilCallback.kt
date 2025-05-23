package com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen

import androidx.recyclerview.widget.DiffUtil
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.CategoryHeaderItem
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemViewTypes
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemWithType
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.WordDefinitionItem

class ItemsDiffUtilCallback(
    private val oldList: List<ItemWithType>,
    private val newList: List<ItemWithType>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].viewType == newList[newItemPosition].viewType
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldList[oldItemPosition].viewType == newList[newItemPosition].viewType) {
            val viewType = newList[newItemPosition].viewType
            return when (viewType) {
                ItemViewTypes.ITEM_WORD_DEFINITION -> {
                    val oldDefinitionItem = (oldList[oldItemPosition] as WordDefinitionItem)
                    val newDefinitionItem = (newList[newItemPosition] as WordDefinitionItem)
                    oldDefinitionItem == newDefinitionItem
                }
                ItemViewTypes.ITEM_CATEGORY_HEADER -> {
                    val oldHeader = (oldList[oldItemPosition] as CategoryHeaderItem).header
                    val newHeader = (newList[newItemPosition] as CategoryHeaderItem).header
                    oldHeader == newHeader
                }
                else -> false
            }
        } else {
            return false
        }
    }
//
//    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
//        return when ((newList[newItemPosition] as? SelectableItem)?.isPartOfSelection) {
//            true -> {
//                Log.d("SELECTION", "hide button")
//                Animations.ANIMATION_BUTTON_TO_CHECKBOX
//            }
//            false -> {
//                Log.d("SELECTION", "show button")
//                Animations.ANIMATION_CHECKBOX_TO_BUTTON
//            }
//            else -> super.getChangePayload(oldItemPosition, newItemPosition)
//        }
//    }
}
