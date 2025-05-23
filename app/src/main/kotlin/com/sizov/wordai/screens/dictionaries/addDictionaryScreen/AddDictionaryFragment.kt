package com.sizov.wordai.screens.dictionaries.addDictionaryScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.sizov.wordai.R
import com.sizov.wordai.application.WordAIApplication
import com.sizov.wordai.screens.screensUtils.FragmentResult
import com.sizov.wordai.screens.screensUtils.MarginItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
class AddDictionaryFragment : Fragment(),
    DefinitionSelectionListener,
    DictLanguageSelectionListener {
    private var saveDictionaryFab: FloatingActionButton? = null
    private var dictionaryLabelField: TextInputLayout? = null
    private var languagesList: RecyclerView? = null
    private var definitionsList: RecyclerView? = null
    private var addDictViewModel: AddDictViewModel? = null
    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchQuery = savedInstanceState?.getString(SEARCH_QUERY_KEY) ?: ""

        setHasOptionsMenu(true)
        initViews(view)
        val activity = requireActivity()
        activity.title = getString(R.string.title_add_new_dictionary)
        val context = requireContext()

        val dictLanguagesAdapter = DictLanguagesAdapter(context, this, emptyList())
        val adapter = SelectableDefsAdapter(context, this, emptyList())
        val languagesLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        setupLanguagesList(dictLanguagesAdapter, languagesLayoutManager, languagesList)
        setupSelectedDefinitionsList(adapter, layoutManager, definitionsList)

        val factory = AddDictViewModelFactory(
            (activity.application as WordAIApplication)
                .appComponent.dictionariesRepository,
            (activity.application as WordAIApplication)
                .appComponent.dictDefinitionsRepository,
        )
        addDictViewModel = ViewModelProvider(this, factory)[AddDictViewModel::class.java]
        setupViewModel(addDictViewModel, adapter, dictLanguagesAdapter)
        setupListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addDictViewModel = null
        clearViews()
    }

    override fun onDefinitionSelectionChanged(selectedDefinition: SelectableWordDefinition) {
        addDictViewModel?.changeItemSelection(selectedDefinition.def, selectedDefinition.isSelected)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        if (searchQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(searchQuery, true)
        }
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchQuery = newText.orEmpty()
                    if (newText.orEmpty().isEmpty()) {
                        definitionsList?.scrollToPosition(SCROLL_START_POSITION)
                    }
                    addDictViewModel?.filter(newText.orEmpty())
                    return true
                }
            }
        )
    }

    private fun setupViewModel(
        viewModel: AddDictViewModel?,
        adapter: SelectableDefsAdapter,
        languagesAdapter: DictLanguagesAdapter,
    ) {
        viewModel?.allLanguages?.observe(this) { languagesList ->
            onLanguagesChanged(languagesList, languagesAdapter)
        }
        viewModel?.allDefinitions?.observe(this) { definitionsList ->
            onDefinitionsChanged(definitionsList, adapter)
        }
    }

    private fun onLanguagesChanged(
        newLanguages: List<LanguageModel>,
        adapter: DictLanguagesAdapter,
    ) {
        val diffCallback = SelectableLanguageDiffCallback(
            newList = newLanguages,
            oldList = adapter.languageList
        )
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
        adapter.languageList = newLanguages
    }

    private fun onDefinitionsChanged(
        newDefinitions: List<SelectableWordDefinition>,
        adapter: SelectableDefsAdapter,
    ) {
        val diffCallback = SelectableWordDefDiffCallback(
            newList = newDefinitions,
            oldList = adapter.definitions
        )
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
        adapter.definitions = newDefinitions
    }

    private fun setupListeners() {
        saveDictionaryFab?.setOnClickListener {
            dictionaryLabelField?.error = null
            val label = dictionaryLabelField?.editText?.text?.toString()

            if (label.isNullOrEmpty()) {
                dictionaryLabelField?.error = getString(R.string.error_dict_label_field)
                return@setOnClickListener
            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    val newDictId = addDictViewModel?.addDictionary(label)
                        ?: NOT_EXIST_DICT_ID
                    val bundle = Bundle().apply {
                        putLong(DICT_ID_KEY, newDictId)
                        putString(DICT_LABEL_KEY, label)
                    }
                    setFragmentResult(FragmentResult.DictionariesTab.OPEN_NEW_DICTIONARY_KEY, bundle)
                }
            }
        }
    }

    private fun setupLanguagesList(
        adapter: DictLanguagesAdapter,
        layoutManager: LinearLayoutManager,
        languagesList: RecyclerView?,
    ) {
        languagesList?.adapter = adapter
        languagesList?.layoutManager = layoutManager
    }

    private fun setupSelectedDefinitionsList(
        adapter: SelectableDefsAdapter,
        layoutManager: LinearLayoutManager,
        definitionsList: RecyclerView?,
    ) {
        definitionsList?.addItemDecoration(
            MarginItemDecoration(
                marginVertical = 10,
                marginHorizontal = 12
            )
        )
        definitionsList?.adapter = adapter
        definitionsList?.layoutManager = layoutManager
    }

    private fun initViews(view: View) {
        saveDictionaryFab = view.findViewById(R.id.save_dictionary_fab)
        dictionaryLabelField = view.findViewById(R.id.enter_dictionary_label)
        languagesList = view.findViewById(R.id.languages_list)
        definitionsList = view.findViewById(R.id.added_dictionaries_list)
    }

    private fun clearViews() {
        saveDictionaryFab = null
        dictionaryLabelField = null
        languagesList = null
        definitionsList = null
    }

    override fun onLanguageSelectionChanged(selectedLanguageModel: LanguageModel) {
        addDictViewModel?.changeLanguageItemSelection(selectedLanguageModel)
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query_key"
        private const val SCROLL_START_POSITION = 0
        private const val NOT_EXIST_DICT_ID = 0L
        const val DICT_ID_KEY = "dictionary_id_key"
        const val DICT_LABEL_KEY = "dictionary_label_key"

        @JvmStatic
        fun newInstance() = AddDictionaryFragment()
    }
}
