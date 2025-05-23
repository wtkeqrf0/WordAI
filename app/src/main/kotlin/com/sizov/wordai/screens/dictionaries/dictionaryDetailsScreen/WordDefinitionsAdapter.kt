package com.sizov.wordai.screens.dictionaries.dictionaryDetailsScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sizov.wordai.R
import com.sizov.wordai.entities.WordDefinition
import com.sizov.wordai.screens.dictionaries.DefinitionClickListener
import com.sizov.wordai.screens.screensUtils.PlaySoundBtnClickListener
import com.sizov.wordai.screens.screensUtils.capitalizeFirstChar

class WordDefinitionsAdapter(
    private val context: Context,
    private val playSoundBtnClickListener: PlaySoundBtnClickListener,
    private val definitionClickListener: DefinitionClickListener,
    var wordDefinitions: List<WordDefinition>
) : RecyclerView.Adapter<WordDefinitionsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val writingText: TextView = view.findViewById(R.id.writing_text)
        private val translationText: TextView = view.findViewById(R.id.translation_text)
        private val originalExampleText: TextView = view.findViewById(R.id.original_example_text)
        private val translationExampleText: TextView =
            view.findViewById(R.id.translation_example_text)

        fun bind(definition: WordDefinition) {
            writingText.text = definition.writing.capitalizeFirstChar()
            if (definition.partOfSpeech != null) {
                translationText.text = context.resources.getString(
                    R.string.translation_pos_line,
                    definition.mainTranslation.capitalizeFirstChar(),
                    definition.partOfSpeech
                )
            } else {
                translationText.text = definition.mainTranslation.capitalizeFirstChar()
            }
            if (definition.examples.isNotEmpty()) {
                val mainExample = definition.examples.first()
                originalExampleText.visibility = View.VISIBLE
                originalExampleText.text = mainExample.originalText.capitalizeFirstChar()
                if (mainExample.translatedText != null) {
                    translationExampleText.visibility = View.VISIBLE
                    translationExampleText.text = mainExample.translatedText.capitalizeFirstChar()
                } else {
                    translationExampleText.visibility = View.GONE
                }
            } else {
                originalExampleText.visibility = View.GONE
                translationExampleText.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.item_word_definition, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(wordDefinitions[position])
        val playSoundBtn: Button = holder.itemView.findViewById(R.id.play_speech_btn)
        val text = wordDefinitions[position].writing
        playSoundBtn.setOnClickListener {
            playSoundBtnClickListener.onPlayBtnClick(text)
        }
        holder.itemView.setOnClickListener {
            definitionClickListener.onClickToDefinition(wordDefinitions[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return wordDefinitions.size
    }
}
