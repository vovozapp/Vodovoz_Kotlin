package com.vodovoz.app.data.paging.comments

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vodovoz.app.data.model.common.CommentEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.comment.AllCommentsByProductResponseJsonParser.parseAllCommentsByProductResponse
import com.vodovoz.app.data.remote.RemoteDataSource
import retrofit2.HttpException

class CommentsPagingSource(
    private val productId: Long,
    private val remoteDataSource: RemoteDataSource
) : PagingSource<Int, CommentEntity>() {

    override fun getRefreshKey(state: PagingState<Int, CommentEntity>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentEntity> {
        try {
            val page = params.key ?: 1
            val response = remoteDataSource.fetchAllCommentsByProduct(
                productId = productId,
                page = page
            )

            if (!response.isSuccessful) return LoadResult.Error(HttpException(response))

            return when(val parsedResponse = response.body()!!.parseAllCommentsByProductResponse()) {
                is ResponseEntity.Success -> {
                    val commentEntityList = parsedResponse.data
                    val nextPageNumber = if (commentEntityList.isEmpty()) null else page + 1
                    val prevPageNumber = if (page > 1) page - 1 else null
                    LoadResult.Page(commentEntityList, prevPageNumber, nextPageNumber)
                }
                is ResponseEntity.Hide -> LoadResult.Error(Exception("Hide content"))
                is ResponseEntity.Error -> LoadResult.Error(Exception(parsedResponse.errorMessage))
            }
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}