package com.vodovoz.app.common.speechrecognizer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SpeechDialogFragment : DialogFragment(R.layout.fragment_speech_dialog) {

    private val getSpeechResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val resultsStringArray = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!resultsStringArray.isNullOrEmpty()) {
                    debugLog { "result ${resultsStringArray[0]}" }
                    findNavController().navigate(SpeechDialogFragmentDirections.actionToSearchFragment(resultsStringArray[0]))
                    dismiss()
                }
            } else {
                dismiss()
            }

        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите...")
        }

        getSpeechResultLauncher.launch(speechRecognizerIntent)
    }

}