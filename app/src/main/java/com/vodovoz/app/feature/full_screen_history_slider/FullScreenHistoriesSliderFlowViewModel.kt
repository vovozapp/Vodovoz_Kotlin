package com.vodovoz.app.feature.full_screen_history_slider

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.design_system.model.StoryUi
import com.vodovoz.app.design_system.model.mapToUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.mapper.HistoryMapper.mapToUI
import com.vodovoz.app.ui.model.HistoryUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullScreenHistoriesSliderFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<FullScreenHistoriesSliderFlowViewModel.HistoriesSliderState, FullScreenHistoriesSliderFlowViewModel.HistoriesSliderEvents>(
    HistoriesSliderState()
) {

    private val startStory = savedState.get<Long>("startHistoryId") ?: 0L


    private fun loadStories() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                uiState = UiState.Loading
            )
        }

        vodovozServiceRepository.getStories().onEach { storiesResult ->
            val stories = storiesResult.getOrNull()
            if (stories != null) {

                val storyIndex =
                    stories.indexOf(
                        stories.firstOrNull { it.id == startStory } ?: run {
                            navigateBack()
                            return@onEach
                        }
                    )

                uiStateListener.updateData { s ->
                    s.copy(
                        stories = stories.mapToUi(),
                        currentStoryIndex = storyIndex,
                        currentPageIndex = 0,
                        uiState = UiState.Success,
                        timePassed = 0
                    )
                }
                startStory()
            } else {
                navigateBack()
            }

        }.collect()
    }

    private fun startStory() = viewModelScope.launch {
        fun systemMilliseconds(): Long {
            return System.nanoTime() / 1_000_000
        }

        uiStateListener.updateData { s ->
            s.copy(
                storyIsPlay = true
            )
        }

        while (state.data.storyIsPlay) {
            val startTime = systemMilliseconds()
            delay(35L)
            uiStateListener.updateData { s ->
                s.copy(
                    timePassed = s.timePassed + (systemMilliseconds() - startTime)
                )
            }

            val viewState = state.data

            if (viewState.timePassed >= viewState.currentStoryPage.durationMillis) {
                goNextStoryPage()
            }
        }
    }

    fun stopStory() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                storyIsPlay = false
            )
        }
    }

    fun resumeStory() = viewModelScope.launch {
        startStory()
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(HistoriesSliderEvents.GoBack)
    }

    fun updateData() {
        loadStories()
        viewModelScope.launch {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            flow {
                emit(
                    repository.fetchHistoriesSlider()
                )
            }
                .onEach {
//                    val response = it.parseHistoriesSliderResponse()
                    if (it is ResponseEntity.Success) {
                        it.data.mapToUI().let { data ->
                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    historyUIList = data
                                ),
                                loadingPage = false,
                                error = null
                            )
                        }
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }
                .catch {
                    debugLog { "fetch histories error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun goToProfile() {
        viewModelScope.launch {
            eventListener.emit(HistoriesSliderEvents.GoToProfile)
        }
    }

    fun changeStoryIndex(currentStoryPage: Int) = viewModelScope.launch {
        if (currentStoryPage == state.data.currentStoryIndex) return@launch

        uiStateListener.updateData { s ->
            s.copy(
                currentStoryIndex = currentStoryPage,
                currentPageIndex = 0,
                timePassed = 0L
            )
        }
    }

    fun goPreviousStoryPage() = viewModelScope.launch {
        val isFirstPage = dataState.currentPageIndex == 0
        val isFirstStory = dataState.currentStoryIndex == 0

        when {
            isFirstPage && isFirstStory -> {
                eventListener.emit(HistoriesSliderEvents.GoBack)
            }
            isFirstPage -> {
                val prevStoryIndex = dataState.currentStoryIndex - 1
                eventListener.emit(HistoriesSliderEvents.ChangePagerIndex(prevStoryIndex))
            }
            else -> {
                uiStateListener.updateData { state ->
                    state.copy(
                        currentPageIndex = dataState.currentPageIndex - 1,
                        timePassed = 0L
                    )
                }
            }
        }
    }

    fun goNextStoryPage() = viewModelScope.launch {
        val maxStoryPageIndex = dataState.currentStory.pages.size - 1
        val nextPageIndex = dataState.currentPageIndex + 1
        val isLastPage = nextPageIndex > maxStoryPageIndex
        val isLastStory = dataState.currentStoryIndex >= dataState.stories.lastIndex

        when {
            isLastPage && isLastStory -> {
                uiStateListener.updateData { state ->
                    state.copy(storyIsPlay = false)
                }
                eventListener.emit(HistoriesSliderEvents.GoBack)
            }
            isLastPage -> {
                eventListener.emit(
                    HistoriesSliderEvents.ChangePagerIndex(dataState.currentStoryIndex + 1)
                )
            }
            else -> {
                uiStateListener.updateData { state ->
                    state.copy(currentPageIndex = nextPageIndex, timePassed = 0L)
                }
            }
        }
    }


    sealed class HistoriesSliderEvents : Event {
        data object GoToProfile : HistoriesSliderEvents()

        data object GoBack : HistoriesSliderEvents()

        data class ChangePagerIndex(val newStoryIndex: Int) : HistoriesSliderEvents()
    }

    sealed class UiState {
        data object Loading : UiState()
        data object Success : UiState()
        data object NetworkError : UiState()
        data object Error : UiState()
    }

    data class HistoriesSliderState(
        val historyUIList: List<HistoryUI> = listOf(),
        val stories: List<StoryUi> = listOf(),
        val currentStoryIndex: Int = 0,
        val currentPageIndex: Int = 0,
        val uiState: UiState = UiState.Loading,
        val timePassed: Long = 0L,
        val storyIsPlay: Boolean = false,
    ) : State {

        val currentStory get() = stories[currentStoryIndex]
        val currentStoryPage get() = currentStory.pages[currentPageIndex]

    }
}