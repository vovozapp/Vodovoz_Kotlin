package com.vodovoz.app.ui.components.fragment.all_comments_by_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.mapper.CommentMapper.mapToUI
import kotlinx.coroutines.flow.map

class AllCommentsByProductViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    var productId: Long? = null

    fun updateArgs(productId: Long) {
        this.productId = productId
        updateData()
    }

    fun updateData() = dataRepository
        .fetchAllCommentsByProduct(
            productId = productId!!
        ).map { pagingData ->
            pagingData.map { product -> product.mapToUI() }
        }.cachedIn(viewModelScope)

    fun isLogin() = dataRepository.isAlreadyLogin()

}