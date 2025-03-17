package com.vodovoz.app.domain.general.respository

import androidx.paging.PagingData
import com.vodovoz.app.domain.general.model.BannerModel
import com.vodovoz.app.domain.general.model.CatalogDetailsModel
import com.vodovoz.app.domain.general.model.CertificateActivationDetailsModel
import com.vodovoz.app.domain.general.model.CommentModel
import com.vodovoz.app.domain.general.model.FieldModel
import com.vodovoz.app.domain.general.model.OrderWithMenuModel
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import com.vodovoz.app.domain.general.model.PopupWindowInfoModel
import com.vodovoz.app.domain.general.model.PreOrderSectionModel
import com.vodovoz.app.domain.general.model.ProductCommentsInfoModel
import com.vodovoz.app.domain.general.model.ProductDetailsScreenModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.ProductsSectionModel
import com.vodovoz.app.domain.general.model.ProductsTitle
import com.vodovoz.app.domain.general.model.PromotionDetailsModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.PromotionsSectionModel
import com.vodovoz.app.domain.general.model.SearchRecommendationsModel
import com.vodovoz.app.domain.general.model.SectionModel
import com.vodovoz.app.domain.general.model.SiteStateModel
import com.vodovoz.app.domain.general.model.SortModel
import com.vodovoz.app.domain.general.model.StoryModel
import com.vodovoz.app.domain.general.model.TopAndBottomSectionsModel
import com.vodovoz.app.domain.general.model.UnratedProductsSectionModel
import com.vodovoz.app.feature.preorder.model.FieldUi
import kotlinx.coroutines.flow.Flow

interface VodovozServiceRepository {

    fun getCertificateActivationDetails(): Flow<Result<CertificateActivationDetailsModel>>

    fun activateCertificate(field: FieldUi): Flow<Result<String>>

    fun getRegisterFields(): Flow<Result<SectionModel<FieldModel>>>

    fun register(fields: List<FieldModel>): Flow<Result<Long>>

    fun loginByEmail(
        email: String,
        password: String,
    ): Flow<Result<String>>

    fun getCatalogDetails(): Flow<Result<CatalogDetailsModel>>

    fun getCategoryProducts(categoryId: Long): Flow<Result<ProductsSectionModel>>

    fun getCategoryProductsPaged(categoryId: Long, sort: SortModel): Flow<PagingData<ProductModel>>

    fun getSearchProductsPaged(
        query: String,
        categoryId: Int = -1,
        sort: SortModel = SortModel.Empty,
    ): Flow<PagingData<ProductModel>>

    fun getSearchProducts(query: String): Flow<Result<ProductsSectionModel>>

    fun getSearchRecommendations(): Flow<Result<SearchRecommendationsModel>>

    fun getMiniSearchRecommendations(query: String): Flow<Result<SearchRecommendationsModel>>

    fun getSiteState(): Flow<Result<SiteStateModel>>

    fun getPreorderFields(productId: Long): Flow<Result<PreOrderSectionModel>>

    fun sendPreorder(productId: Long, fields: List<FieldModel>): Flow<Result<String>>

    fun getUnratedProductsDetails(): Flow<Result<UnratedProductsSectionModel>>

    fun getFavoriteProducts(
        productsIds: String = ""
    ): Flow<Result<ProductsSectionModel>>

    fun getFavoriteProductsPaged(
        categoryId: Int = -1,
        sort: SortModel = SortModel.Empty,
        productsIds: String = ""
    ): Flow<PagingData<ProductModel>>

    suspend fun addFavoriteProducts(
        productsIds: String,
    ): Flow<Result<ProductsSectionModel>>

    suspend fun addProductToFavorites(
        productId: Long,
    ): Flow<Result<String>>

    suspend fun removeProductFromFavorites(
        productId: Long,
    ): Flow<Result<String>>

    suspend fun addProductToCart(
        productId: Long,
        quantity: Int,
    ): Flow<Result<String>>

    suspend fun addMultipleProductsToCart(
        productIdsWithQuantity: String,
    ): Flow<Result<String>>

    suspend fun removeProductFromCart(
        productId: Long,
    ): Flow<Result<String>>


    suspend fun updateProductInCart(
        productId: Long,
        quantity: Int,
    ): Flow<Result<String>>

    suspend fun clearCart(): Flow<Result<String>>

    fun getProductAnalogs(
        productId: Long,
        sort: SortModel,
    ): Flow<Result<ProductsSectionModel>>

    fun getProductCommentsInfo(
        productId: Long,
    ): Flow<Result<ProductCommentsInfoModel>>

    fun getProductCommentsPaged(
        productId: Long,
        sort: SortModel = SortModel.Empty,
    ): Flow<PagingData<CommentModel>>

    fun getProductDetails(productId: Long): Flow<Result<ProductDetailsScreenModel>>

    fun getPopupWindowInfo(): Flow<Result<PopupWindowInfoModel>>

    fun getStories(): Flow<Result<List<StoryModel>>>

    fun getBanners(): Flow<Result<List<BannerModel>>>

    fun getPromotions(): Flow<Result<PromotionsSectionModel>>

    fun getPromotionDetails(
        promotionId: Int,
    ): Flow<Result<Pair<ProductsTitle, PromotionDetailsModel>>>

    fun getPromotionDetailsProductsPaged(
        promotionId: Int,
        limit: Int = 5,
    ): Flow<PagingData<ProductModel>>

    fun getPromotionsWithSections(): Flow<Result<PromotionsSectionModel>>

    fun getPromotionsPaged(
        limit: Int = 10,
    ): Flow<PagingData<PromotionModel>>

    fun getOrderMenu(
        userId: Long? = null,
    ): Flow<Result<OrderWithMenuModel>>

    fun getPopularCategories(): Flow<Result<SectionModel<PopularCategoryModel>>>

    fun getNewProducts(): Flow<Result<SectionModel<ProductModel>>>

    fun getAllNewProducts(): Flow<Result<ProductsSectionModel>>

    fun getAllNewProductsPaged(
        categoryId: Int = -1,
        sort: SortModel = SortModel.Empty,
    ): Flow<PagingData<ProductModel>>

    fun getHurryUpBuyProducts(): Flow<Result<SectionModel<ProductModel>>>

    suspend fun getAllHurryUpBuyProducts(): Flow<Result<ProductsSectionModel>>

    fun getAllHurryUpBuyProductsPaged(
        categoryId: Int = -1,
        sort: SortModel = SortModel.Empty,
    ): Flow<PagingData<ProductModel>>

    fun getSuperTop(): Flow<Result<TopAndBottomSectionsModel>>

    fun getAllSuperTop(
        id: Int,
    ): Flow<Result<ProductsSectionModel>>

    fun getAllSuperTopPaged(
        id: Int,
        categoryId: Int = -1,
        sort: SortModel = SortModel.Empty,
    ): Flow<PagingData<ProductModel>>

    fun getViewedProducts(): Flow<Result<SectionModel<ProductModel>>>

}