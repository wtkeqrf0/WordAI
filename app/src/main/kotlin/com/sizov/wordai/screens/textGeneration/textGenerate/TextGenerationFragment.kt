package com.sizov.wordai.screens.textGeneration.textGenerate

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sizov.wordai.R
import com.sizov.wordai.application.WordAIApplication
import com.sizov.wordai.repositoryImplementations.lookupWordDefinitionRepository.DefinitionsRequestResult
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.LookupWordDefinitionsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextGenerationFragment : Fragment() {
    private lateinit var viewModel: TextGenerationViewModel
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_generation, container, false)
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearStates()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appComponent = (requireActivity().application as WordAIApplication).appComponent
        val factory = TextGenerationViewModelFactory(
            textGenerationRepository = appComponent.textGenerationRepository,
            dictionariesRepository = appComponent.dictionariesRepository,
            lookupWordDefinitionsRepository = appComponent.lookupWordDefRepository
        )
        viewModel = ViewModelProvider(this, factory)[TextGenerationViewModel::class.java]

        requireActivity().title = getString(R.string.title_text_generation)

        scope.launch {
            viewModel.dictionariesFlow.collect { dictionaries ->
                try {
                    Log.i("TOSH", "даров мужик, вот словари: $dictionaries")
                    val dialog = TextGenerationDialog(
                        dictionaries = dictionaries,
                        onSuccess = { subject: String ->
                            Log.i("TOSH", "Начинаем отправлять запрос на генерацию текста с темой: '$subject'")
                            viewModel.generateText(subject = subject)
                        }
                    )
                    dialog.show(parentFragmentManager, TEXT_GENERATION_DIALOG_TAG)
                } catch (e: Exception) {

                }
            }
        }

        scope.launch {
            viewModel.generateTextState.collect { state ->
                if (state is TextGenerationViewModel.TextState.Success) {
                    withContext(Dispatchers.Main) {
                        val textView = view.findViewById<TextView>(R.id.generated_text)

                        val generatedText = state.generatedText.split(' ')

                        val spannable = SpannableString(generatedText.joinToString(" "))

                        var startIndex = 0
                        generatedText.forEach { word ->
                            val endIndex = startIndex + word.length
                            val clickableSpan = object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    viewModel.getTranslations(word)
                                }
                                override fun updateDrawState(ds: android.text.TextPaint) {
                                    super.updateDrawState(ds)
                                    ds.isUnderlineText = false  // Убираем подчеркивание
                                    ds.color = Color.BLACK
                                }
                            }
                            spannable.setSpan(clickableSpan, startIndex, endIndex, 0)

                            // Создаем фон с полупрозрачным цветом и закругленными углами
                            val background = createRippleBackground()

                            // Устанавливаем фон для каждой кликабельной части текста
                            textView.setBackgroundDrawable(background)

                            startIndex = endIndex + 1 // Move to next word
                        }

                        textView.text = spannable
                        textView.isClickable = true
                        textView.movementMethod = android.text.method.LinkMovementMethod.getInstance()
                    }
                }
            }

        }

        scope.launch {
            viewModel.lookupWordState.collect { state ->
                if(state is DefinitionsRequestResult.Success) {
                    withContext(Dispatchers.Main) {
                        val textView = view.findViewById<TextView>(R.id.generated_text)
                        showPopup(textView, state.definitions.map { it.mainTranslation })
                    }
                }
            }
        }

    }

    override fun onPause() {
        scope.cancel()
        super.onPause()
    }

    private fun createRippleBackground(): Drawable {
        val backgroundColor = Color.parseColor("#80D3D3D3") // Полупрозрачный фон (80 - это альфа-канал)

        // Создаем простой фоновый drawable с радиусом и прозрачностью
        val rippleDrawable = RippleDrawable(
            ColorStateList.valueOf(Color.parseColor("#80D3D3D3")), // Цвет для ripple
            null, // Ничего не изменяем для исходного фона
            ContextCompat.getDrawable(requireContext(), R.drawable.rounded_background) // Здесь будет круглый фон
        )
        return rippleDrawable
    }

    // Функция для отображения PopupWindow
    private fun showPopup(view: View, translations: List<String>) {

        // Используем LayoutInflater для создания попапа
        val inflater = getSystemService(requireContext(), LayoutInflater::class.java) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup, null)

        // Находим контейнер для отображения списка переводов
        val linearLayout = popupView.findViewById<LinearLayout>(R.id.popup_linear_layout)

        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)

        // Добавляем переводы в LinearLayout
        for (translation in translations) {
            // Создаем TextView для каждого перевода
            val textView = TextView(requireContext())
            textView.text = translation
            textView.textSize = 16f // Устанавливаем размер шрифта
            textView.setBackgroundResource(R.drawable.rounded_background)

            textView.setPadding(10, 10, 10, 10) // Отступы между элементами

            textView.isClickable = true
            textView.setOnClickListener {

                popupWindow.dismiss()
            }
            // Добавляем TextView в LinearLayout
            linearLayout.addView(textView)

            val space = Space(requireContext())
            // Устанавливаем размеры для Space
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  // ширина
                50
            )
            space.layoutParams = params
            space.visibility = View.INVISIBLE

            // Добавляем Space в родительский контейнер
            linearLayout.addView(space)
        }

        // Отображаем PopupWindow
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
    }

    companion object {

        private const val TEXT_GENERATION_DIALOG_TAG = "TextGenerationDialog"

        @JvmStatic
        fun newInstance() = TextGenerationFragment()
    }
}
