package com.vodovoz.app.common.search

import com.vodovoz.app.common.datastore.DataStoreRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchManager @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) {

    companion object {
        private const val SEARCH_HISTORY = "SEARCH_HISTORY"
    }

    fun fetchSearchHistoryFlow() =
        dataStoreRepository.getStringFlow(SEARCH_HISTORY).map { queries ->
            parseSearchHistoryStr(queries ?: "")
        }

    fun clearSearchHistory() {
        dataStoreRepository.remove(SEARCH_HISTORY)
    }

    fun addQueryToHistory(query: String) {
        if (query.isNotEmpty()) {
            val queryList = fetchSearchHistory()
            val cont = queryList.find { it == query }
            if (cont == null) {
                dataStoreRepository.putString(SEARCH_HISTORY, buildSearchHistoryStr(listOf(query) + queryList))
            }
        }
    }

    fun removeQueryFromHistory(query: String){
        val queryList = fetchSearchHistory().toMutableList().apply { remove(query) }
        dataStoreRepository.putString(SEARCH_HISTORY, buildSearchHistoryStr(queryList))
    }

    fun fetchSearchHistory() =
        parseSearchHistoryStr(dataStoreRepository.getString(SEARCH_HISTORY) ?: "")

    private fun parseSearchHistoryStr(searchHistoryStr: String): List<String> {
        val queryList = searchHistoryStr.split(",").toMutableList()
        return queryList.filter { it.isNotEmpty() }
    }

    private fun buildSearchHistoryStr(queryList: List<String>) = StringBuilder().apply {
        queryList.forEach { query ->
            append(query).append(",")
        }
    }.toString()
}