package com.vodovoz.app.feature.blockapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.databinding.FragmentBlockAppBinding
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.util.extensions.addOnBackPressedCallback
import com.vodovoz.app.util.extensions.disableFullScreen
import com.vodovoz.app.util.extensions.fromHtml
import com.vodovoz.app.util.extensions.startJivo
import com.vodovoz.app.util.extensions.startTelegram
import com.vodovoz.app.util.extensions.startViber
import com.vodovoz.app.util.extensions.startWhatsUpWithUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class BlockAppFragment : BaseFragment() {

    override fun layout(): Int {
        return R.layout.fragment_block_app
    }

    internal val binding: FragmentBlockAppBinding by viewBinding {
        FragmentBlockAppBinding.bind(contentView)
    }

    @Inject
    lateinit var siteStateManager: SiteStateManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().disableFullScreen()
        observeSiteState()
        addOnBackPressedCallback { }
    }

    private fun observeSiteState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                siteStateManager.siteStateFlow.collectLatest { siteState ->
                    if (siteState != null) {

                        if (siteState.isActive) {
                            findNavController().navigate(R.id.splashFragment)
                        }

                        val time = siteState.data?.time
                        if (time.isNullOrEmpty()) {
                            binding.linearTimeData.isVisible = false
                        } else {
                            binding.linearTimeData.isVisible = true
                            countDownStart(siteState.data.time)
                        }


                        Glide.with(requireContext())
                            .load(siteState.data?.logo?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.imageBlockApp)


                        initToolbar(showNavBtn = false, titleText = siteState.data?.title ?: "")

                        binding.txtBlockApp.text = if (siteState.data?.desc == null) {
                            binding.txtBlockApp.isVisible = false
                            ""
                        } else {
                            binding.txtBlockApp.isVisible = true
                            siteState.data.desc.fromHtml()
                        }

                        Glide.with(requireContext())
                            .load(siteState.data?.whatsUp?.image?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.whatsUp)

                        Glide.with(requireContext())
                            .load(siteState.data?.viber?.image?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.viber)

                        Glide.with(requireContext())
                            .load(siteState.data?.telegram?.image?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.telegram)

                        Glide.with(requireContext())
                            .load(siteState.data?.chat?.image?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.chat)

                        Glide.with(requireContext())
                            .load(siteState.data?.phone?.image?.parseImagePath())
                            .placeholder(R.drawable.placeholderimageproduits)
                            .error(R.drawable.placeholderimageproduits)
                            .into(binding.imageCall)

                        binding.whatsUp.setOnClickListener {
                            val url = siteState.data?.whatsUp?.url ?: return@setOnClickListener
                            requireActivity().startWhatsUpWithUri(url)
                        }

                        binding.viber.setOnClickListener {
                            val url = siteState.data?.viber?.url ?: return@setOnClickListener
                            requireActivity().startViber(url)
                        }

                        binding.telegram.setOnClickListener {
                            val url = siteState.data?.telegram?.url ?: return@setOnClickListener
                            requireActivity().startTelegram(url)
                        }

                        binding.telegram.setOnClickListener {
                            val url = siteState.data?.telegram?.url ?: return@setOnClickListener
                            requireActivity().startTelegram(url)
                        }

                        binding.chat.setOnClickListener {
                            val url = siteState.data?.chat?.url ?: return@setOnClickListener
                            requireActivity().startJivo(url)
                        }

                        binding.imageCall.setOnClickListener {
                            val url = siteState.data?.phone?.url ?: return@setOnClickListener
                            val intent =
                                Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", url, null))
                            startActivity(intent)
                        }

                    } else {
                        showError(ErrorState.Error())
                        bindErrorRefresh {
                            lifecycleScope.launch {
                                if (siteStateManager.siteActive()) {
                                    findNavController().navigate(R.id.splashFragment)
                                }
                            }
                        }
                    }
                    
                }
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable { }

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
                    val futureDate = dateFormat.parse(time) ?: return
                    val currentDate = Date()
                    if (!currentDate.after(futureDate)) {
                        binding.linearTimeData.visibility = View.VISIBLE
                        val diffMillis = (futureDate.time - currentDate.time).milliseconds

                        val days = diffMillis.inWholeDays
                        val hours = diffMillis.inWholeHours
                        val minutes = diffMillis.inWholeMinutes
                        val seconds = diffMillis.inWholeSeconds

                        val locale = Locale.getDefault()
                        binding.txtDays.text = String.format(locale, "%02d", days)
                        binding.txtHours.text = String.format(locale, "%02d", hours)
                        binding.txtMinute.text = String.format(locale, "%02d", minutes)
                        binding.txtSecond.text = String.format(locale, "%02d", seconds)
                    } else {
                        lifecycleScope.launch {
                            repeatOnLifecycle(Lifecycle.State.STARTED) {
                                siteStateManager.requestSiteState()
                            }
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