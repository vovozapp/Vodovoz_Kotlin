package com.vodovoz.app.ui.components.fragment.about_services

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.DialogFragmentAboutServicesBinding
import com.vodovoz.app.ui.components.adapter.ServicesDetailAdapter
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.ServiceDiffUtilCallback
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class AboutServicesDialogFragment : ViewStateBaseDialogFragment() {

    private lateinit var binding: DialogFragmentAboutServicesBinding
    private lateinit var viewModel: AboutServicesViewModel

    private val compositeDisposable = CompositeDisposable()
    private val onServiceClickSubject: PublishSubject<String> = PublishSubject.create()
    private val servicesDetailAdapter = ServicesDetailAdapter(onServiceClickSubject = onServiceClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        initViewModel()
        subscribeSubjects()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[AboutServicesViewModel::class.java]
        viewModel.updateData()
    }

    private fun subscribeSubjects() {
        onServiceClickSubject.subscribeBy { type ->

        }.addTo(compositeDisposable)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = DialogFragmentAboutServicesBinding.inflate(
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
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initServicesRecycler() {
        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.servicesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.servicesRecycler.adapter = servicesDetailAdapter
        binding.servicesRecycler.addItemDecoration(
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
            binding.detail.text = HtmlCompat.fromHtml(aboutServicesBundleUI.detail!!, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            binding.toolbar.title = aboutServicesBundleUI.title

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