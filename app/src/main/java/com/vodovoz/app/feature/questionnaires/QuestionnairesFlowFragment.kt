package com.vodovoz.app.feature.questionnaires

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentQuestionnairesBinding
import com.vodovoz.app.feature.questionnaires.adapter.QuestionnaireTypesFlowAdapter
import com.vodovoz.app.ui.adapter.QuestionsAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionnairesFlowFragment : BaseFragment() {

    override fun layout() = R.layout.fragment_questionnaires

    private val binding: FragmentQuestionnairesBinding by viewBinding {
        FragmentQuestionnairesBinding.bind(
            contentView
        )
    }

    private val viewModel: QuestionnairesFlowViewModel by viewModels()

    private val questionnaireTypesAdapter = QuestionnaireTypesFlowAdapter(){
        viewModel.fetchQuestionnaireByType(it)
    }
    private val questionsAdapter = QuestionsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchQuestionnaireTypes()
    }

    override fun update() {
        when(viewModel.isTryToGetQuestionnaire) {
            true -> viewModel.fetchQuestionnaireByType()
            false -> viewModel.fetchQuestionnaireTypes()
        }
    }

    override fun initView() {
        initAppBar()
        initQuestionnaireTypesRecycler()
        initQuestionsRecycler()
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

    private fun initQuestionnaireTypesRecycler() {
        binding.questionnaireTypesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.questionnaireTypesRecycler.adapter = questionnaireTypesAdapter
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.questionnaireTypesRecycler.addMarginDecoration { rect, view, parent, state ->
            with(rect) {
                if (parent.getChildAdapterPosition(view) == state.itemCount - 1) top = space/2
                left = space
                right = space
                bottom = space/2
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
            viewModel.observeUiState().collect{state->
                if (state.loadingPage) {
                    showLoaderWithBg(true)
                } else {
                    showLoaderWithBg(false)
                }

                val questionnaireTypesUIList = state.data.questionnaireTypeUIList
                if (questionnaireTypesUIList.isNotEmpty()) {
                    binding.questionnairesContainer.visibility = View.VISIBLE
                    binding.questionsContainer.visibility = View.INVISIBLE
                    questionnaireTypesAdapter.questionnaireTypeList = questionnaireTypesUIList
                    questionnaireTypesAdapter.notifyDataSetChanged()
                }

                val questionUIList = state.data.questionUIList
                if (questionUIList.isNotEmpty()) {
                    binding.questionnairesContainer.visibility = View.INVISIBLE
                    binding.questionsContainer.visibility = View.VISIBLE
                    questionsAdapter.questionUIList = questionUIList
                    questionsAdapter.notifyDataSetChanged()
                }

                showError(state.error)
            }
        }
    }
}