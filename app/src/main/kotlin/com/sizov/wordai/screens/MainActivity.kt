package com.sizov.wordai.screens

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sizov.wordai.R
import com.sizov.wordai.application.WordAIApplication
import com.sizov.wordai.entities.Dictionary
import com.sizov.wordai.screens.bottomNavigation.BottomNavigator
import com.sizov.wordai.screens.dictionaries.addDictionaryScreen.AddDictionaryFragment
import com.sizov.wordai.screens.dictionaries.definitionDetailsScreen.DefinitionDetailsFragment
import com.sizov.wordai.screens.dictionaries.dictionariesScreen.DictionariesFragment
import com.sizov.wordai.screens.dictionaries.dictionariesScreen.TrainersBottomSheet
import com.sizov.wordai.screens.dictionaries.dictionaryDetailsScreen.DictionaryDetailsFragment
import com.sizov.wordai.screens.dictionaries.lookupWordDefinitionsScreen.LookupWordDefinitionsFragment
import com.sizov.wordai.screens.screensUtils.FragmentResult
import com.sizov.wordai.screens.textGeneration.RecognizedTextBottomSheet
import com.sizov.wordai.screens.textGeneration.RecognizedWordsFragment
import com.sizov.wordai.screens.trainers.TrainFlashcardsFragment
import com.sizov.wordai.screens.trainers.TrainWriteFragment
import com.sizov.wordai.services.NotificationReceiver
import java.util.Calendar

@Suppress("TooManyFunctions")
class MainActivity : AppCompatActivity() {
    private var bottomNavigationView: BottomNavigationView? = null
    private val bottomNavigator: BottomNavigator by lazy {
        (application as WordAIApplication).appComponent.bottomNavigator
    }
    private var trainedDictionaryId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        setContentView(R.layout.activity_main)
        initViews()
        bottomNavigator.subscribe(supportFragmentManager)
        setupDictionariesFragmentResultListeners()
        setupDefinitionsResultListeners()
        setupTrainersDialogResultListeners()
        setupRecognizeResultListener()
        createNotificationChannel()
        scheduleDailyNotification()

        if (savedInstanceState == null) {
            val item = bottomNavigationView?.menu?.findItem(R.id.dictionary_tab)
            item?.isChecked = true
            bottomNavigator.setDefaultTab(bottomNavigator.getTabByName(DICTIONARIES_TAB))
        }
    }

    override fun onDestroy() {
        bottomNavigator.unsubscribe()
        clearViews()
        super.onDestroy()
    }

    override fun onBackPressed() {
        bottomNavigator.onBackPressed()
        super.onBackPressed()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_CODE) {
            if (allPermissionsGranted(this)) {
                bottomNavigator.selectTab(bottomNavigator.getTabByName(RECOGNIZE_TAB))
                val item = bottomNavigationView?.menu?.findItem(R.id.recognition_tab)
                item?.isChecked = true
            }
        }
    }

    private fun setupDictionariesFragmentResultListeners() {
        supportFragmentManager.apply {
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_DICTIONARY_KEY,
                this@MainActivity
            ) { _, bundle ->
                val dictionaryId = bundle.getLong(DictionariesFragment.DICT_ID_KEY)
                val dictionaryLabel = bundle.getString(
                    DictionariesFragment.DICT_LABEL_KEY,
                    getString(R.string.app_name)
                )
                replaceFragment(
                    fragment = DictionaryDetailsFragment.newInstance(dictionaryId, dictionaryLabel),
                    tag = "dictionary_details_fragment"
                )
            }
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_ADD_DICTIONARY_KEY,
                this@MainActivity
            ) { _, _ ->
                replaceFragment(AddDictionaryFragment.newInstance(), "add_dictionary_fragment")
            }
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_NEW_DICTIONARY_KEY,
                this@MainActivity
            ) { _, bundle ->
                popBackStack()
                val dictionaryId = bundle.getLong(AddDictionaryFragment.DICT_ID_KEY)
                val label = bundle.getString(
                    AddDictionaryFragment.DICT_LABEL_KEY,
                    getString(R.string.app_name)
                )
                replaceFragment(
                    DictionaryDetailsFragment.newInstance(dictionaryId, label),
                    "new_dictionary_fragment"
                )
            }
        }
    }

    private fun setupDefinitionsResultListeners() {
        supportFragmentManager.apply {
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_LOOKUP_WORD_DEF_FRAGMENT_KEY,
                this@MainActivity
            ) { _, bundle ->
                val dictionaryId = bundle.getLong(
                    DictionaryDetailsFragment.RESULT_DATA_KEY,
                    Dictionary.DEFAULT_DICT_ID
                )
                val word = bundle.getString(LookupWordDefinitionsFragment.LOOKUP_WORD_KEY, "")
                replaceFragment(
                    fragment = LookupWordDefinitionsFragment.newInstance(dictionaryId, word),
                    tag = "Lookup_word_def_fragment",
                    transactionName = "open_lookup_word_def_fragment"
                )
            }
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_DEF_DETAILS_FRAGMENT_KEY,
                this@MainActivity
            ) { _, bundle ->
                val dictionaryId = bundle.getLong(FragmentResult.DictionariesTab.RESULT_DICT_ID_KEY)
                val saveMode = bundle.getInt(FragmentResult.DictionariesTab.RESULT_SAVE_MODE_KEY)
                addFragment(
                    fragment = DefinitionDetailsFragment.newInstance(dictionaryId, saveMode),
                    tag = "def_details_fragment",
                    transactionName = "open_def_details_fragment"
                )
            }
        }
    }

    private fun setupTrainersDialogResultListeners() {
        supportFragmentManager.apply {
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_TRAINERS_DIALOG_KEY,
                this@MainActivity
            ) { _, bundle ->
                trainedDictionaryId = bundle.getLong(DictionariesFragment.DICT_ID_KEY)
                val trainersBottomSheet = TrainersBottomSheet()
                trainersBottomSheet.show(supportFragmentManager, "trainers_bottom_sheet_tag")
            }
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_CARDS_TRAINER_KEY,
                this@MainActivity
            ) { _, _ ->
                replaceFragment(TrainFlashcardsFragment.newInstance(trainedDictionaryId ?: 1))
            }
            setFragmentResultListener(
                FragmentResult.DictionariesTab.OPEN_WRITER_TRAINER_KEY,
                this@MainActivity
            ) { _, _ ->
                replaceFragment(TrainWriteFragment.newInstance(trainedDictionaryId ?: 1))
            }
        }
    }

    private fun setupRecognizeResultListener() {
        supportFragmentManager.apply {
            setFragmentResultListener(
                FragmentResult.RecognizeTab.OPEN_RECOGNIZED_TEXT_DIALOG,
                this@MainActivity
            ) { _, bundle ->
                val text = bundle.getString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY, "")
                val recognizedTextBottomSheet = RecognizedTextBottomSheet.newInstance(text)
                recognizedTextBottomSheet.show(this, null)
            }
            setFragmentResultListener(
                FragmentResult.RecognizeTab.OPEN_RECOGNIZED_WORDS_FRAGMENT_KEY,
                this@MainActivity
            ) { _, bundle ->
                val text = bundle.getString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY, "")
                replaceFragment(
                    RecognizedWordsFragment.newInstance(text),
                    "recognized_words_fragment",
                    isNeedAnimate = false
                )
            }
        }
    }

    private fun initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView?.setOnItemSelectedListener { tab ->
            when (tab.itemId) {
                R.id.dictionary_tab -> {
                    bottomNavigator.selectTab(bottomNavigator.getTabByName(DICTIONARIES_TAB))
                    true
                }

                R.id.recognition_tab -> {
                    bottomNavigator.selectTab(bottomNavigator.getTabByName(RECOGNIZE_TAB))
                    true
                }

                R.id.profile_tab -> {
                    bottomNavigator.selectTab(bottomNavigator.getTabByName(PROFILE_TAB))
                    true
                }

                else -> false
            }
        }
    }

    private fun clearViews() {
        bottomNavigationView = null
    }

    private fun addFragment(
        fragment: Fragment,
        tag: String? = null,
        transactionName: String? = null,
        isNeedAnimate: Boolean = true,
    ) {
        supportFragmentManager.commit {
            if (isNeedAnimate) {
                setCustomAnimations(
                    R.anim.fragments_slide_in,
                    R.anim.fragments_fade_out,
                    R.anim.fragments_fade_in,
                    R.anim.fragments_slide_out
                )
            }
            add(R.id.fragment_container, fragment, tag)
            addToBackStack(transactionName)
            setReorderingAllowed(true)
        }
    }

    private fun replaceFragment(
        fragment: Fragment,
        tag: String? = null,
        transactionName: String? = null,
        isNeedAnimate: Boolean = true,
    ) {
        supportFragmentManager.commit {
            if (isNeedAnimate) {
                setCustomAnimations(
                    R.anim.fragments_slide_in,
                    R.anim.fragments_fade_out,
                    R.anim.fragments_fade_in,
                    R.anim.fragments_slide_out
                )
            }
            replace(R.id.fragment_container, fragment, tag)
            addToBackStack(transactionName)
            setReorderingAllowed(true)
        }
    }

    private fun allPermissionsGranted(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Daily Reminder"
            val description = "Channel for daily reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("daily_reminder", name, importance)
            channel.description = description

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleDailyNotification() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = 10
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0


        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        Log.i("TOSH", "calendar = ${calendar.timeInMillis}")


        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    companion object {
        private const val DICTIONARIES_TAB = "DICTIONARIES"
        private const val RECOGNIZE_TAB = "RECOGNIZE"
        private const val PROFILE_TAB = "PROFILE"
        private const val PERMISSIONS_CODE = 42
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
