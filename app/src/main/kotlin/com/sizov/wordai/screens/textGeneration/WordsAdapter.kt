package com.sizov.wordai.screens.textGeneration

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sizov.wordai.R
import com.google.android.flexbox.FlexboxLayoutManager

class WordsAdapter(
    private val context: Context,
    private val wordClickListener: WordClickListener,
    var words: List<Word>
) : RecyclerView.Adapter<WordsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordButton: TextView = view.findViewById(R.id.word_button)

        fun bind(word: Word) {
            wordButton.text = word.writing
            val layoutParams = wordButton.layoutParams
            if (layoutParams is FlexboxLayoutManager.LayoutParams) {
                layoutParams.flexGrow = FLEXBOX_PARAM_FLEX_GROW
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.item_word, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = words[position]
        holder.bind(word)
        holder.wordButton.setOnClickListener {
            wordClickListener.onWordClicked(word)
        }
    }

    override fun getItemCount(): Int {
        return words.size
    }

    companion object {
        private const val FLEXBOX_PARAM_FLEX_GROW = 1f
    }
}

interface WordClickListener {
    fun onWordClicked(word: Word)
}
