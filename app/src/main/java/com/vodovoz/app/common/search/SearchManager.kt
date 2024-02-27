package com.vodovoz.app.common.search

import com.vodovoz.app.common.datastore.DataStoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchManager @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) {

    companion object {
        private const val SEARCH_HISTORY = "SEARCH_HISTORY"
    }


    fun clearSearchHistory() {
        dataStoreRepository.remove(SEARCH_HISTORY)
    }

    fun addQueryToHistory(query: String) {
        if (query.isNotEmpty()) {
            val queryList = fetchSearchHistory().toMutableList()
            val cont = queryList.find { it == query }
            if (cont == null) {
                queryList.add(query)
                dataStoreRepository.putString(SEARCH_HISTORY, buildSearchHistoryStr(queryList))
            }
        }
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