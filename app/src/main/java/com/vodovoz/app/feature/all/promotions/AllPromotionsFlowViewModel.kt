package com.vodovoz.app.feature.all.promotions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.design_system.model.AboutAdvertisingUi
import com.vodovoz.app.design_system.model.PromotionSectionUi
import com.vodovoz.app.design_system.model.PromotionUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.mapper.AllPromotionBundleMapper.mapToUI
import com.vodovoz.app.ui.model.PromotionFilterUI
import com.vodovoz.app.ui.model.custom.AllPromotionBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllPromotionsFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<AllPromotionsFlowViewModel.AllPromotionsState, AllPromotionsFlowViewModel.AllPromotionsEvent>(
    AllPromotionsState()
) {

    private var dataSource = savedState.get<AllPromotionsFragment.DataSource>("dataSource")
        ?: AllPromotionsFragment.DataSource.All


    private fun loadAllPromotions() = vodovozServiceRepository.getPromotionsWithSections()
        .onEach { promotionsWithSectionsModelResult ->

            promotionsWithSectionsModelResult.onSuccess { promotionsWithSectionsModel ->
                uiStateListener.updateData { s ->
                    val sections = promotionsWithSectionsModel.filters.toUi()
                    val promotions = vodovozServiceRepository.getPromotionsPaged()
                        .map { pagingData ->
                            pagingData.map { promotionModel -> promotionModel.toUi() }
                        }

                    s.copy(
                        sections = sections,
                        currentSection = sections.firstOrNull() ?: PromotionSectionUi.Empty,
                        pagedPromotions = promotions,
                        initialPagedPromotions = promotions
                    )
                }
            }


        }.launchIn(viewModelScope)

    //old method
    private fun fetchAllPromotions(filterChanged: Boolean = false) {
        viewModelScope.launch {
            val dataSource = dataSource
            flow {
                when (dataSource) {
                    is AllPromotionsFragment.DataSource.All -> emit(
                        repository.fetchAllPromotions(
                            filterId = state.data.selectedFilterUi.id
                        )
                    )

                    is AllPromotionsFragment.DataSource.ByBanner -> emit(
                        repository.fetchPromotionsByBanner(
                            categoryId = dataSource.categoryId
                        )
                    )
                }
            }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()

                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                promotionFilterUIList = mutableListOf(
                                    PromotionFilterUI(
                                        id = 0,
                                        name = "Все акции",
                                        code = ""
                                    )
                                ).apply {
                                    addAll(data.promotionFilterUIList.toMutableList())
                                },
                                allPromotionBundleUI = data,
                                scrollToTop = filterChanged
                            ),
                            loadingPage = false,
                            error = null
                        )

                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error(),
                                page = 1,
                                loadMore = false
                            )
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "fetch products by data source sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun updateBySelectedFilter(filterId: Long) {
        val filter = state.data.promotionFilterUIList.find { it.id == filterId } ?: return
        if (state.data.selectedFilterUi.id == filterId) return
        uiStateListener.value = state.copy(data = state.data.copy(selectedFilterUi = filter))
        fetchAllPromotions(true)
    }

    fun firstLoadSorted() {
        loadAllPromotions()
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            fetchAllPromotions()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchAllPromotions()
    }

    fun isLoginAlready() = accountManager.isAlreadyLogin()
    fun updateScrollToTop() {
        if (state.data.scrollToTop) {
            uiStateListener.value = state.copy(data = state.data.copy(scrollToTop = false))
        }
    }

    fun selectSection(section: PromotionSectionUi) = viewModelScope.launch {

        if (section == uiStateListener.value.data.currentSection) return@launch

        eventListener.emit(AllPromotionsEvent.ScrollTop)
        uiStateListener.updateData { s ->
            s.copy(
                currentSection = section,
                pagedPromotions = s.initialPagedPromotions.map { pagingData ->
                    pagingData.filter {
                        it.sectionId == section.id || section.id == 0
                    }
                }
            )
        }
    }

    fun closeAdvertisingBottomSheet() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                showAdvertisingBottomSheet = false
            )
        }
    }

    fun showAdvertisingBottomSheet(promotionUi: PromotionUi) = viewModelScope.launch {
        promotionUi.aboutAdvertisingUi?.let {
            uiStateListener.updateData { s ->
                s.copy(
                    currentAdvertising = promotionUi.aboutAdvertisingUi,
                    showAdvertisingBottomSheet = true
                )
            }
        }
    }

    fun navigateToPromotionDetails(promotion: PromotionUi) = viewModelScope.launch {
        eventListener.emit(AllPromotionsEvent.GoToProductDetails(promotionId = promotion.id.toLong()))
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(AllPromotionsEvent.GoBack)
    }

    data class AllPromotionsState(
        val promotionFilterUIList: List<PromotionFilterUI> = emptyList(),
        val allPromotionBundleUI: AllPromotionBundleUI? = null,
        val selectedFilterUi: PromotionFilterUI = PromotionFilterUI(
            id = 0,
            name = "Все акции",
            code = ""
        ),
        val scrollToTop: Boolean = false,
        val sections: List<PromotionSectionUi> = emptyList(),
        val currentSection: PromotionSectionUi = PromotionSectionUi.Empty,
        val pagedPromotions: Flow<PagingData<PromotionUi>> = emptyFlow(),
        val initialPagedPromotions: Flow<PagingData<PromotionUi>> = emptyFlow(),
        val showAdvertisingBottomSheet: Boolean = false,
        val currentAdvertising: AboutAdvertisingUi = AboutAdvertisingUi.Empty,
    ) : State

    sealed class AllPromotionsEvent() : Event {

        data object ScrollTop : AllPromotionsEvent()

        data class GoToProductDetails(
            val promotionId: Long,
        ) : AllPromotionsEvent()

        data object GoBack : AllPromotionsEvent()

    }
}