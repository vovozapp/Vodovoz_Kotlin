package com.vodovoz.app.ui.fragment.about_app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentAboutAppBinding
import com.vodovoz.app.databinding.FragmentAboutAppFlowBinding
import com.vodovoz.app.feature.bottom.aboutapp.AboutAppFlowViewModel
import com.vodovoz.app.feature.bottom.aboutapp.adapter.AboutApp
import com.vodovoz.app.feature.bottom.aboutapp.adapter.AboutAppClickListener
import com.vodovoz.app.feature.bottom.aboutapp.adapter.AboutAppFlowAdapter
import com.vodovoz.app.ui.adapter.AboutAppAction
import com.vodovoz.app.ui.adapter.AboutAppActionsAdapter
import com.vodovoz.app.ui.extensions.ContextExtensions.isTablet
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutAppDialogFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_about_app_flow

    private val binding: FragmentAboutAppFlowBinding by viewBinding {
        FragmentAboutAppFlowBinding.bind(
            contentView
        )
    }
    private val viewModel: AboutAppFlowViewModel by viewModels()

    private val space by lazy { resources.getDimension(R.dimen.space_8).toInt() }

    private val aboutAppFlowAdapter = AboutAppFlowAdapter(
        object : AboutAppClickListener {
            override fun onActionClick(action: AboutApp) {
                when(action.id) {
                    1 -> rate()
                    2 -> writeToDevelopers()
                    0 -> share()
                }
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(resources.getString(R.string.about_app_title), showNavBtn = true)
        setupVersion()
        setupAboutAppActionsRecycler()
        observeList()
    }

    private fun observeList() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeAboutAppList()
                .collect {
                    aboutAppFlowAdapter.submitList(it)
                }
        }
    }

    private fun setupVersion() {
        binding.tvAppVersion.text = String.format(
            requireContext().getString(R.string.app_version_text),
            BuildConfig.VERSION_NAME
        )
    }

    private fun setupAboutAppActionsRecycler() {
        binding.rvActions.layoutManager = LinearLayoutManager(requireContext())

        binding.rvActions.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space
        }

        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.bg_border_gray)
            ?.let { divider.setDrawable(it) }
        binding.rvActions.addItemDecoration(divider)
        binding.rvActions.adapter = aboutAppFlowAdapter
    }

    private fun share() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBodyText = "https://play.google.com/store/apps/details?id=com.m.vodovoz"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
        startActivity(Intent.createChooser(sharingIntent, "Shearing Option"))
    }

    private fun rate() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireActivity().packageName}"))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun writeToDevelopers() {
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
/*
@AndroidEntryPoint
class AboutAppDialogFragment : Fragment() {

    private lateinit var binding: FragmentAboutAppBinding
    private val viewModel: AboutAppDialogViewModel by viewModels()

    private val aboutAppActionsAdapter = AboutAppActionsAdapter()

    private val aboutAppActionsList = listOf(
        AboutAppAction.WRITE_DEVELOPERS, AboutAppAction.RATE, AboutAppAction.SHARE
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentAboutAppBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initAppBar()
        setupVersion()
        setupAboutAppActionsRecycler()
    }.root

    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = resources.getString(R.string.about_app_title)
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setupVersion() {
        binding.tvAppVersion.text = String.format(
            requireContext().getString(R.string.app_version_text),
            BuildConfig.VERSION_NAME
        )
    }

    private fun setupAboutAppActionsRecycler() {
        binding.rvActions.layoutManager = LinearLayoutManager(requireContext())
        val space = resources.getDimension(R.dimen.space_8).toInt()
        binding.rvActions.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space
        }

        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.bg_border_gray)
            ?.let { divider.setDrawable(it) }
        binding.rvActions.addItemDecoration(divider)
        binding.rvActions.adapter = aboutAppActionsAdapter
        aboutAppActionsAdapter.setupListeners { aboutAppAction ->
            when(aboutAppAction) {
                AboutAppAction.RATE -> rate()
                AboutAppAction.WRITE_DEVELOPERS -> writeToDevelopers()
                AboutAppAction.SHARE -> share()
            }
        }
        aboutAppActionsAdapter.updateData(aboutAppActionsList)
    }

    private fun share() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBodyText = "https://play.google.com/store/apps/details?id=com.m.vodovoz"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
        startActivity(Intent.createChooser(sharingIntent, "Shearing Option"))
    }

    private fun rate() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireActivity().packageName}")))
    }

    private fun writeToDevelopers() {
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

}*/
