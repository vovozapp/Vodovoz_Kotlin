package com.vodovoz.app.ui.fragment.questionnaires

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentQuestionnairesBinding
import com.vodovoz.app.ui.adapter.QuestionnaireTypesAdapter
import com.vodovoz.app.ui.adapter.QuestionsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

@AndroidEntryPoint
class QuestionnairesFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentQuestionnairesBinding
    private val viewModel: QuestionnairesViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()
    private val onQuestionnaireTypeClickSubject: PublishSubject<String> = PublishSubject.create()
    private val questionnaireTypesAdapter = QuestionnaireTypesAdapter(onQuestionnaireTypeClickSubject)
    private val questionsAdapter = QuestionsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchQuestionnaireTypes()
        subscribeSubjects()
    }

    private fun subscribeSubjects() {
        onQuestionnaireTypeClickSubject.subscribeBy { type ->
            viewModel.fetchQuestionnaireByType(type)
        }.addTo(compositeDisposable)
    }

    override fun update() {
        when(viewModel.isTryToGetQuestionnaire) {
            true -> viewModel.fetchQuestionnaireByType()
            false -> viewModel.fetchQuestionnaireTypes()
        }
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentQuestionnairesBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

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
        viewModel.viewStateLD.observe(viewLifecycleOwner) { viewState ->
            when(viewState) {
                is ViewState.Error -> onStateError(viewState.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.questionnaireTypeListLD.observe(viewLifecycleOwner) { questionnaireTypesUIList ->
            binding.questionnairesContainer.visibility = View.VISIBLE
            binding.questionsContainer.visibility = View.INVISIBLE
            questionnaireTypesAdapter.questionnaireTypeList = questionnaireTypesUIList
            questionnaireTypesAdapter.notifyDataSetChanged()
        }

        viewModel.questionUIListLD.observe(viewLifecycleOwner) { questionUIList ->
            binding.questionnairesContainer.visibility = View.INVISIBLE
            binding.questionsContainer.visibility = View.VISIBLE
            questionsAdapter.questionUIList = questionUIList
            questionsAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}