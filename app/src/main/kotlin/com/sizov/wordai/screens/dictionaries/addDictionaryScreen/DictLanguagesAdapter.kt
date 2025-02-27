package com.sizov.wordai.screens.dictionaries.addDictionaryScreen

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sizov.wordai.R

class DictLanguagesAdapter(
    private val context: Context,
    private val dictLanguageSelectionListener: DictLanguageSelectionListener,
    var languageList: List<LanguageModel>,
) : RecyclerView.Adapter<DictLanguagesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val languageTitle: TextView = view.findViewById(R.id.language_title)

        fun bind(language: LanguageModel, isSelected: Boolean) {
            languageTitle.text = language.title
            if (isSelected) {
                languageTitle.setBackgroundColor(Color.GREEN)
            } else {
                languageTitle.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.item_language, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var selectableLanguage = languageList[position]
        holder.bind(selectableLanguage, selectableLanguage.isSelected)
        holder.itemView.setOnClickListener {
            // Create new list with only the clicked item selected
            val newList = languageList.mapIndexed { index, lang ->
                lang.copy(isSelected = index == position)
            }
            languageList = newList
            notifyDataSetChanged() // Refresh all items to update their UI

            dictLanguageSelectionListener.onLanguageSelectionChanged(newList[position])
        }
    }

    override fun getItemCount(): Int {
        return languageList.size
    }
}

interface DictLanguageSelectionListener {
    fun onLanguageSelectionChanged(selectedLanguageModel: LanguageModel)
}
