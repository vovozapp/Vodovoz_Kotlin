package com.vodovoz.app.feature.productdetail.sendcomment

import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.common.media.ImagePickerFragment
import com.vodovoz.app.common.media.MediaManager
import com.vodovoz.app.databinding.BsSendCommentBinding
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SendCommentAboutProductBottomDialog : BaseBottomSheetFragment() {

    companion object {
        fun newInstance(
            productId: Long,
            rate: Int,
        ) = SendCommentAboutProductBottomDialog().apply {
            arguments = bundleOf("productId" to productId, "rate" to rate)
        }
    }


    override fun layout(): Int = R.layout.bs_send_comment

    private val binding: BsSendCommentBinding by viewBinding { BsSendCommentBinding.bind(contentView) }

    private val viewModel: SendCommentAboutProductFlowViewModel by viewModels()

    private val args: SendCommentAboutProductBottomDialogArgs by navArgs()

    @Inject
    lateinit var siteStateManager: SiteStateManager

    @Inject
    lateinit var mediaManager: MediaManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        debugLog { "init onViewCreated $this" }

        observeSiteState()
        observeMediaManager()

        if (args.rate != -4) {
            binding.rbRating.rating = args.rate.toFloat()
        }

        initHeader()
        initSendButton()
        observeSendResult()
        initDialog()
        binding.etComment.doAfterTextChanged {
            binding.errorTv.isVisible = false
        }

        binding.rbRating.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, _, _ ->
                binding.errorTv.isVisible = false
            }

        binding.images.plusImagePreview.setOnClickListener {
            mediaManager.saveCommentData(
                MediaManager.CommentData(
                    args.productId,
                    binding.rbRating.rating.toInt(),
                    binding.etComment.text.toString()
                )
            )
            findNavController().navigate(
                SendCommentAboutProductBottomDialogDirections.actionToImagePickerFragment(
                    ImagePickerFragment.CREATE
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        debugLog { "init onDestroy $this" }
    }

    private fun observeMediaManager() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mediaManager
                    .observePublicationImage()
                    .collect { list ->
                        debugLog { "list ${list?.size}" }
                        if (list != null) {
                            if (list.size >= 5) {
                                binding.images.plusImagePreview.visibility = View.GONE
                            }
                            list.forEachIndexed { index, file ->
                                when (index) {
                                    0 -> {
                                        setImage(file.path, binding.images.previewImage1)
                                        if (list.size == 1) {
                                            binding.images.previewImage2.visibility = View.GONE
                                            binding.images.previewImage3.visibility = View.GONE
                                            binding.images.previewImage4.visibility = View.GONE
                                            binding.images.previewImage5.visibility = View.GONE
                                        }
                                    }
                                    1 -> {
                                        setImage(file.path, binding.images.previewImage2)
                                        if (list.size == 2) {
                                            binding.images.previewImage3.visibility = View.GONE
                                            binding.images.previewImage4.visibility = View.GONE
                                            binding.images.previewImage5.visibility = View.GONE
                                        }
                                    }
                                    2 -> {
                                        setImage(file.path, binding.images.previewImage3)
                                        if (list.size == 3) {
                                            binding.images.previewImage4.visibility = View.GONE
                                            binding.images.previewImage5.visibility = View.GONE
                                        }
                                    }
                                    3 -> {
                                        setImage(file.path, binding.images.previewImage4)
                                        if (list.size == 4) {
                                            binding.images.previewImage5.visibility = View.GONE
                                        }
                                    }
                                    4 -> setImage(file.path, binding.images.previewImage5)
                                }
                            }

                            // mediaManager.removePublicationImage() //todo
                            // mediaManager.removeCommentData() //todo
                        }
                    }
            }
        }
    }

    private fun setImage(uri: String, target: ShapeableImageView) {
        target.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(uri)
            .placeholder(R.drawable.placeholderimageproduits)
            .into(target)
    }

    private fun observeSiteState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                siteStateManager
                    .observeSiteState()
                    .collect {
                        val showComment = it?.showComments ?: false
                        binding.images.root.isVisible = showComment
                    }
            }
        }
    }

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun observeSendResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeSendCommentResult()
                    .collect {
                        if (it) {
                            requireActivity().snack("Отзыв успешно добавлен!")
                            dialog?.cancel()
                        } else {
                            requireActivity().snack("Ошибка, попробуйте снова.")
                        }
                    }
            }
        }
    }

    private fun initHeader() {
        binding.incHeader.imgClose.setOnClickListener { dialog?.dismiss() }
        binding.incHeader.tvTitle.text = getString(R.string.new_comment_title)
    }

    private fun initSendButton() {
        binding.btnSend.setOnClickListener {

            if (binding.rbRating.rating.toInt() == 0) {
                binding.errorTv.text = "Поставьте оценку от 1 до 5"
                binding.errorTv.isVisible = true
                return@setOnClickListener
            }

            if (binding.etComment.text.toString().length < FieldValidationsSettings.MIN_COMMENT_LENGTH) {
                binding.errorTv.text =
                    buildString {
                        append("Длина отзыва должа быть не менее ")
                        append(FieldValidationsSettings.MIN_COMMENT_LENGTH)
                        append(" символов")
                    }
                binding.errorTv.isVisible = true
                return@setOnClickListener
            }

            viewModel.sendCommentAboutProduct(
                comment = binding.etComment.text.toString(),
                rating = binding.rbRating.rating.toInt()
            )

        }
    }

}
