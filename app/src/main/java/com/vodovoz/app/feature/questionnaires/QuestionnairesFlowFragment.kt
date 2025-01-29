package com.vodovoz.app.feature.questionnaires

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.core.network.interceptor.ChangeUrlInterceptor
import com.vodovoz.app.databinding.FragmentQuestionnairesBinding
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.catalog.CatalogFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.feature.questionnaires.adapters.QuestionnaireTypesFlowAdapter
import com.vodovoz.app.feature.questionnaires.adapters.QuestionsAdapter
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import javax.inject.Inject


@AndroidEntryPoint
class QuestionnairesFlowFragment : BaseFragment() {

    override fun layout() = R.layout.fragment_questionnaires

    private val binding: FragmentQuestionnairesBinding by viewBinding {
        FragmentQuestionnairesBinding.bind(
            contentView
        )
    }

    private val viewModel: QuestionnairesFlowViewModel by viewModels()

    private val questionnaireTypesAdapter = QuestionnaireTypesFlowAdapter() {
        viewModel.fetchQuestionnaireByType(it)
    }
    private val questionsAdapter = QuestionsAdapter(
        onLinkClick = {
            findNavController().navigate(
                QuestionnairesFlowFragmentDirections.actionToWebViewFragment(
                    url = it.link,
                    title = it.name,
                )
            )
        }
    )

    @Inject
    lateinit var tabManager: TabManager
    private val homeViewModel: HomeFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()
    private val catalogFlowViewModel: CatalogFlowViewModel by activityViewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()

    @Inject
    lateinit var changeUrlInterceptor: ChangeUrlInterceptor

    @Inject
    lateinit var siteStateManager: SiteStateManager

    private var threeFingerTouchCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchQuestionnaireTypes()
    }

    override fun update() {
        when (viewModel.isTryToGetQuestionnaire) {
            true -> viewModel.fetchQuestionnaireByType()
            false -> viewModel.fetchQuestionnaireTypes()
        }
    }

    override fun initView() {
        initAppBar()
        initQuestionnaireTypesRecycler()
        initQuestionsRecycler()
        setBackDoor()
        observeViewModel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setBackDoor() {
        binding.questionnairesContainer.setOnTouchListener { _, event ->
            val handled = true
            val action = event.action and MotionEvent.ACTION_MASK
            val count = event.pointerCount
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                if (3 == count) {
                    threeFingerTouchCount++
                    if (threeFingerTouchCount == 3) {
                        threeFingerTouchCount = 0
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage("Выберете текущий путь к серверу")
                            .setNegativeButton("Рабочий") { dialog, _ ->
                                dialog.dismiss()
                                loadHomeFragmentWithNewServerURL("https://m.vodovoz.ru/")
                            }
                            .setPositiveButton("Тестовый") { dialog, _ ->
                                dialog.dismiss()
                                lifecycleScope.launch {
                                    siteStateManager.requestSiteState()
                                    siteStateManager.observeSiteState().collect{ state ->
                                        if (state != null) {
                                            val newLink = "${state.testSiteLink}/"
                                            loadHomeFragmentWithNewServerURL(newLink)
                                            Log.d("sgsderg", newLink)
                                        }
                                    }
                                }
                            }
                            .show()
                    }
                }
            }
            return@setOnTouchListener handled
        }
    }

    private fun loadHomeFragmentWithNewServerURL(serverUrl: String) {
//        ApiConfig.VODOVOZ_URL = serverUrl
        val url = serverUrl.toHttpUrlOrNull() ?: return
        ApiConfig.VODOVOZ_URL = serverUrl
        changeUrlInterceptor.setInterceptor(url.toString())
        lifecycleScope.launch {
            delay(1000)
            homeViewModel.refresh()
            cartFlowViewModel.refreshIdle()
            favoriteViewModel.refreshIdle()
            catalogFlowViewModel.refresh()
            profileViewModel.refresh()
            tabManager.selectTab(R.id.graph_home)
        }
    }

    private fun initAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            viewModel.clickBack()
        }
    }

    private fun initQuestionnaireTypesRecycler() {
        binding.questionnaireTypesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.questionnaireTypesRecycler.adapter = questionnaireTypesAdapter
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.questionnaireTypesRecycler.addMarginDecoration { rect, view, parent, state ->
            with(rect) {
                if (parent.getChildAdapterPosition(view) == state.itemCount - 1) top = space / 2
                left = space
                right = space
                bottom = space / 2
            }
        }
    }

    private fun initQuestionsRecycler() {
        binding.questionsContainer.setScrollElevation(binding.appBar)
        binding.questionsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.questionsRecycler.adapter = questionsAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.observeUiState().collect { state ->
                if (state.loadingPage) {
                    showLoaderWithBg(true)
                } else {
                    showLoaderWithBg(false)
                }

                val questionnaireTypesUIList = state.data.questionnaireTypeUIList
                if (questionnaireTypesUIList.isNotEmpty()) {
                    binding.questionnairesContainer.visibility = View.VISIBLE
                    binding.toolbar.title = "Анкета"
                    binding.questionsContainer.visibility = View.INVISIBLE
                    questionnaireTypesAdapter.questionnaireTypeList = questionnaireTypesUIList
                    questionnaireTypesAdapter.notifyDataSetChanged()
                }

                val questionUIList = state.data.questionUIList
                if (questionUIList.isNotEmpty()) {
                    binding.questionnairesContainer.visibility = View.INVISIBLE
                    binding.questionsContainer.visibility = View.VISIBLE
                    binding.toolbar.title = "Вопросы"
                    questionsAdapter.questionUIList = questionUIList
                    questionsAdapter.notifyDataSetChanged()
                }

                if (state.data.message.isNotEmpty()) {
                    binding.tvMessage.text = state.data.message
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.divider.visibility = View.VISIBLE
                } else {
                    binding.tvMessage.visibility = View.GONE
                    binding.divider.visibility = View.GONE
                }

                if (state.data.onBack) {
                    findNavController().popBackStack()
                }

                showError(state.error)
            }
        }
    }
}