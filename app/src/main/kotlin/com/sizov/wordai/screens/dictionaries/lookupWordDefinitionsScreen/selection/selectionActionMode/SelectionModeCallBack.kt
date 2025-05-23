package com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.selection.selectionActionMode

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import com.sizov.wordai.R

class SelectionModeCallBack<K>(
    private val tracker: SelectionTracker<K>?,
    private val onSaveClicked: () -> Unit
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_selection_action_mode, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.save_definitions -> onSaveClicked.invoke()
        }
        tracker?.clearSelection()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        tracker?.clearSelection()
    }
}
