package com.vodovoz.app.ui.fragment.about_services

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentAboutServicesBinding
import com.vodovoz.app.ui.adapter.ServicesDetailAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.diffUtils.ServiceDiffUtilCallback
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

@AndroidEntryPoint
class AboutServicesDialogFragment : ViewStateBaseDialogFragment() {

    private lateinit var binding: FragmentAboutServicesBinding
    private val viewModel: AboutServicesViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()
    private val onServiceClickSubject: PublishSubject<String> = PublishSubject.create()
    private val servicesDetailAdapter = ServicesDetailAdapter(onServiceClickSubject = onServiceClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        viewModel.updateData()
        subscribeSubjects()
    }

    private fun subscribeSubjects() {
        onServiceClickSubject.subscribeBy { type ->
            requireParentFragment().findNavController().navigate(AboutServicesDialogFragmentDirections.actionToServiceDetailFragment(
                viewModel.serviceTypeList.toTypedArray(),
                type
            ))
        }.addTo(compositeDisposable)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentAboutServicesBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun update() { viewModel.updateData() }

    override fun initView() {
        initAppBar()
        initServicesRecycler()
        observeViewModel()
    }

    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = resources.getString(R.string.services_title)
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initServicesRecycler() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvServices.layoutManager = LinearLayoutManager(requireContext())
        binding.rvServices.adapter = servicesDetailAdapter
        binding.rvServices.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        if (parent.getChildAdapterPosition(view) == 0) {
                            top = space
                        }
                        bottom = space
                        left = space
                        right = space
                    }
                }
            }
        )
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.aboutServicesBundleLD.observe(viewLifecycleOwner) { aboutServicesBundleUI ->
            binding.tvDetails.text = HtmlCompat.fromHtml(aboutServicesBundleUI.detail!!, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            binding.incAppBar.tvTitle.text = aboutServicesBundleUI.title

            val diffUtil = ServiceDiffUtilCallback(
                oldList = servicesDetailAdapter.serviceUIList,
                newList = aboutServicesBundleUI.serviceUIList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                servicesDetailAdapter.serviceUIList = aboutServicesBundleUI.serviceUIList
                diffResult.dispatchUpdatesTo(servicesDetailAdapter)
            }
        }
    }

}