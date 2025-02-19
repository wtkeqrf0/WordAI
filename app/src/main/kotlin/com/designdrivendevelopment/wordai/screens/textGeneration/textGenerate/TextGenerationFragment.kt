package com.designdrivendevelopment.wordai.screens.textGeneration.textGenerate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.wordai.R
import com.designdrivendevelopment.wordai.application.WordAIApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TextGenerationFragment : Fragment() {
    private lateinit var viewModel: TextGenerationViewModel
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_generation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = TextGenerationViewModelFactory(
            (requireActivity().application as WordAIApplication)
                .appComponent.dictionariesRepository
        )
        viewModel = ViewModelProvider(this, factory)[TextGenerationViewModel::class.java]


        requireActivity().title = getString(R.string.title_text_generation)

        scope.launch {
            viewModel.dictionariesFlow.collect { dictionaries ->
                try {
                    Log.i("TOSH", "даров мужик, вот словари: $dictionaries")
                    val dialog = TextGenerationDialog(dictionaries)
                    dialog.show(parentFragmentManager, TEXT_GENERATION_DIALOG_TAG)
                } catch (e: Exception) {
                    
                }
            }
        }
    }

    companion object {

        private const val TEXT_GENERATION_DIALOG_TAG = "TextGenerationDialog"

        @JvmStatic
        fun newInstance() = TextGenerationFragment()
    }
}
