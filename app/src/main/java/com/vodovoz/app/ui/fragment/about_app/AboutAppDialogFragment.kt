package com.vodovoz.app.ui.fragment.about_app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.R
import com.vodovoz.app.databinding.DialogFragmentAboutAppBinding
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.fragment.about_services.AboutServicesViewModel
import com.vodovoz.app.ui.extensions.ContextExtensions.isTablet


class AboutAppDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentAboutAppBinding
    private lateinit var viewModel: AboutAppDialogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[AboutAppDialogViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = DialogFragmentAboutAppBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initAppBar()
        initView()
    }.root

    private fun initAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun initView() {
        binding.version.text = StringBuilder()
            .append("Версия")
            .append(BuildConfig.VERSION_NAME)

        binding.rateApp.setOnClickListener {
            val appPackageName = requireActivity().packageName
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        }
        binding.shareApp.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBodyText = "https://play.google.com/store/apps/details?id=com.m.vodovoz"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
            startActivity(Intent.createChooser(sharingIntent, "Shearing Option"))
        }

        binding.writeDevelopers.setOnClickListener {
            val aboutDevice = StringBuilder()
                .append("Android:").append(Build.MODEL + " ,Версия:" + Build.VERSION.RELEASE)
                .append(when(requireContext().isTablet()) {
                    true -> "Планшет "
                    false ->"Телефон "
                })

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("android@vodovoz.ru"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Обращение по мобильному приложению")

            intent.putExtra(Intent.EXTRA_TEXT,
                Html.fromHtml(StringBuilder()
                    .append("<br><br><br><br><font size=\"2\">${aboutDevice.toString()}</font>")
                    .append("<br>Версия приложения: ${BuildConfig.VERSION_NAME}")
                    .append("<br>User id:" + viewModel.fetchUserId())
                    .toString())
            )

            startActivity(Intent.createChooser(intent, "Написать разработчиков..."))
        }
    }

}