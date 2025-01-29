package com.vodovoz.app.composable.paging_sources

//class ProductsPagingSource(
//    private val repository: MainRepository,
//    private val viewModel: ProductsListFlowViewModel,
//): PagingSource<Int, ProductEntityJC>() {
//
//    override fun getRefreshKey(state: PagingState<Int, ProductEntityJC>): Int = 1
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductEntityJC> {
//        val page = params.key?: 1
//
//        return kotlin.runCatching {
//            Log.d("categoryId", viewModel.categoryId.toString())
//            Log.d("sort", viewModel.state.data.sortType.value.toString())
//            Log.d("orientation", viewModel.state.data.sortType.orientation.toString())
//            Log.d("filter", viewModel.state.data.filterBundle.filterUIList.buildFilterQuery().toString())
//            Log.d("filterValue", viewModel.state.data.filterBundle.filterUIList.buildFilterValueQuery().toString())
//            Log.d("priceFrom", viewModel.state.data.filterBundle.filterPriceUI.minPrice.toString())
//            Log.d("priceTo", viewModel.state.data.filterBundle.filterPriceUI.maxPrice.toString())
//            Log.d("page", viewModel.state.page.toString())
//            Log.d("filterMap", viewModel.state.data.filterBundle.filterUIList.buildFilterRangeQuery().toString())
//            repository.fetchProductsByCategory(
//                categoryId = viewModel.categoryId, // ?: return@flow,
//                sort = viewModel.state.data.sortType.value,
//                orientation = viewModel.state.data.sortType.orientation,
//                filter = viewModel.state.data.filterBundle.filterUIList.buildFilterQuery(),
//                filterValue = viewModel.state.data.filterBundle.filterUIList.buildFilterValueQuery(),
//                priceFrom = viewModel.state.data.filterBundle.filterPriceUI.minPrice,
//                priceTo = viewModel.state.data.filterBundle.filterPriceUI.maxPrice,
//                page = viewModel.state.page,
//                filterMap = viewModel.state.data.filterBundle.filterUIList.buildFilterRangeQuery()
//            )
//        }.fold(
//            onSuccess = {
//                viewModel.productsList.value = it
//                Log.d("NAME", it.name.toString())
//                    LoadResult.Page(
//                    data = it.productList,
//                        prevKey = page - 1,
//                        nextKey = if (it.name.isNullOrEmpty()) null else page + 1
//                        ,
//                )
//            },
//            onFailure = {
//                Log.d("Characters", it.message.toString())
//                LoadResult.Error(it)
//            }
//        )
//    }
//
//    companion object{
//        fun getPager(
//            repository: MainRepository,
//            viewModel: ProductsListFlowViewModel
//        ) = Pager(
//            config = PagingConfig(20),
//            pagingSourceFactory = {ProductsPagingSource(repository, viewModel)}
//        )
//    }
//}



