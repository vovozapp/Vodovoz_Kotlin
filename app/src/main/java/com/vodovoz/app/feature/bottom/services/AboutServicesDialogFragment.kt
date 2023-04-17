package com.vodovoz.app.feature.bottom.services

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentAboutServicesFlowBinding
import com.vodovoz.app.feature.bottom.services.adapter.ServicesClickListener
import com.vodovoz.app.ui.model.ServiceUI
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutServicesDialogFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_about_services_flow

    private val viewModel: AboutServicesFlowViewModel by viewModels()

    private val binding: FragmentAboutServicesFlowBinding by viewBinding {
        FragmentAboutServicesFlowBinding.bind(
            contentView
        )
    }

    private val servicesController by lazy {
        ServicesController(getServicesClickListener(), requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        servicesController.bind(binding.rvServices)
        bindErrorRefresh { viewModel.refreshSorted() }
        observeUiState()
        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeEvent()
                .collect {
                    when (it) {
                        is AboutServicesFlowViewModel.AboutServicesEvents.NavigateToDetails -> {
                            findNavController().navigate(
                                AboutServicesDialogFragmentDirections.actionToServiceDetailFragment(
                                    it.typeList.toTypedArray(),
                                    it.type
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->
                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    val detail = state.data.item?.detail ?: ""
                    val title =
                        state.data.item?.title ?: resources.getString(R.string.services_title)

                    binding.tvDetails.text = detail.fromHtml()

                    initToolbar(title)

                    servicesController.submitList(state.data.item?.serviceUIList ?: emptyList())

                    showError(state.error)

                }
        }
    }

    private fun getServicesClickListener(): ServicesClickListener {
        return object : ServicesClickListener {
            override fun onItemClick(item: ServiceUI) {
                viewModel.navigateToDetails(item.type)
            }
        }
    }

}

/*
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

    @RequiresApi(Build.VERSION_CODES.N)
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

    @RequiresApi(Build.VERSION_CODES.N)
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
            binding.tvDetails.text =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(aboutServicesBundleUI.detail, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    HtmlCompat.fromHtml(aboutServicesBundleUI.detail!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
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

}*/
