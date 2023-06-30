package com.vodovoz.app.feature.blockapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.databinding.FragmentBlockAppBinding
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.util.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BlockAppFragment : BaseFragment() {

    override fun layout(): Int {
        return R.layout.fragment_block_app
    }

    private val binding: FragmentBlockAppBinding by viewBinding {
        FragmentBlockAppBinding.bind(contentView)
    }

    @Inject
    lateinit var siteStateManager: SiteStateManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSiteState()
        addOnBackPressedCallback { }
    }

    private fun observeSiteState() {
        lifecycleScope.launchWhenStarted {
            siteStateManager
                .observeSiteState()
                .collect { state ->
                    if (state != null) {

                        if (siteStateManager.fetchSiteStateActive()) {
                            findNavController().navigate(R.id.splashFragment)
                        }

                        val time = state.data?.time
                        if (time == null || time.isEmpty()) {
                            binding.linearTimeData.isVisible = false
                        } else {
                            binding.linearTimeData.isVisible = true
                            countDownStart(state.data.time)
                        }


                        Glide.with(requireContext())
                            .load(state.data?.logo?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.imageBlockApp)


                        initToolbar(showNavBtn = false, titleText = state.data?.title ?: "")

                        binding.txtBlockApp.text = if (state.data?.desc == null) {
                            binding.txtBlockApp.isVisible = false
                            ""
                        } else {
                            binding.txtBlockApp.isVisible = true
                            state.data.desc.fromHtml()
                        }

                        Glide.with(requireContext())
                            .load(state.data?.whatsUp?.image?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.whatsUp)

                        Glide.with(requireContext())
                            .load(state.data?.viber?.image?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.viber)

                        Glide.with(requireContext())
                            .load(state.data?.telegram?.image?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.telegram)

                        Glide.with(requireContext())
                            .load(state.data?.chat?.image?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.chat)

                        Glide.with(requireContext())
                            .load(state.data?.phone?.image?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.imageCall)

                        binding.whatsUp.setOnClickListener {
                            val url = state.data?.whatsUp?.url ?: return@setOnClickListener
                            requireActivity().startWhatsUpWithUri(url)
                        }

                        binding.viber.setOnClickListener {
                            val url = state.data?.viber?.url ?: return@setOnClickListener
                            requireActivity().startViber(url)
                        }

                        binding.telegram.setOnClickListener {
                            val url = state.data?.telegram?.url ?: return@setOnClickListener
                            requireActivity().startTelegram(url)
                        }

                        binding.telegram.setOnClickListener {
                            val url = state.data?.telegram?.url ?: return@setOnClickListener
                            requireActivity().startTelegram(url)
                        }

                        binding.chat.setOnClickListener {
                            val url = state.data?.chat?.url ?: return@setOnClickListener
                            requireActivity().startJivo(url)
                        }

                        binding.imageCall.setOnClickListener {
                            val url = state.data?.phone?.url ?: return@setOnClickListener
                            val intent =
                                Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", url, null))
                            startActivity(intent)
                        }

                    }
                }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable {  }

    private fun countDownStart(time: String) {
        runnable = object : Runnable {
            override fun run() {
                handler.postDelayed(this, 1000)
                try {
                    val dateFormat = SimpleDateFormat(
                        "dd.MM.yyyy HH:mm:ss",
                        Locale.getDefault()
                    )
                    // Please here set your event date//YYYY-MM-DD
                    val futureDate = dateFormat.parse(time)
                    val currentDate = Date()
                    if (!currentDate.after(futureDate)) {
                        binding.linearTimeData.visibility = View.VISIBLE
                        var diff = (futureDate.time - currentDate.time)
                        val days = diff / (24 * 60 * 60 * 1000)
                        diff -= days * (24 * 60 * 60 * 1000)
                        val hours = diff / (60 * 60 * 1000)
                        diff -= hours * (60 * 60 * 1000)
                        val minutes = diff / (60 * 1000)
                        diff -= minutes * (60 * 1000)
                        val seconds = diff / 1000
                        binding.txtDays.text = String.format("%02d", days)
                        binding.txtHours.text = String.format("%02d", hours)
                        binding.txtMinute.text = String.format("%02d", minutes)
                        binding.txtSecond.text = String.format("%02d", seconds)
                    } else {
                        lifecycleScope.launchWhenStarted {
                            siteStateManager.requestSiteState()
                        }
                        binding.linearTimeData.visibility = View.GONE
                        handler.removeCallbacks(runnable)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        handler.postDelayed(runnable, (1 * 1000).toLong())
    }

}