package com.sizov.wordai.screens.textGeneration.textGenerate

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.sizov.wordai.entities.Dictionary
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class TextGenerationDialog(
    private val dictionaries: List<Dictionary>,
    private val onSuccess: (dictionary: Dictionary, subject: String) -> Unit
) : DialogFragment() {

    private var chosenDictionary: Dictionary? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Создаем контейнер для размещения полей ввода
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL // Вертикальное расположение элементов
            setPadding(50, 40, 50, 10) // Отступы (можно настроить по своему усмотрению)
        }

        // Создаем EditText для ввода темы текста
        val topicInput = EditText(requireContext()).apply {
            hint = "Тема текста"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        // Создаем ChipGroup для выбора словаря
        val dictionaryChipGroup = ChipGroup(requireContext()).apply {
            isSingleSelection = true // Разрешить выбор только одного чипа
            // Добавляем чипы из списка доступных словарей
            dictionaries.forEach { dictionary ->
                val chip = Chip(context).apply {
                    id = dictionary.id.toInt()
                    text = dictionary.label
                    isClickable = true
                    isCheckable = true
                }
                addView(chip)
            }
        }

        // Добавляем поля ввода в контейнер
        layout.addView(topicInput)
        layout.addView(dictionaryChipGroup)

        // Создаем AlertDialog с двумя полями ввода
        return AlertDialog.Builder(requireActivity())
            .setTitle("Добавить текст")
            .setView(layout)
            .setPositiveButton("Добавить") { dialog, _ ->
                if(dictionaryChipGroup.checkedChipId == -1) return@setPositiveButton

                val topic = topicInput.text.toString()
                val dictionary = dictionaries.firstOrNull { it.id.toInt() == dictionaryChipGroup.checkedChipId }


                dictionary?.let {
                    Log.i("TOSH", "topic = $topic | dictionary = $dictionary")
                    onSuccess(dictionary, topic)
                }

                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
            .create()
    }
}

