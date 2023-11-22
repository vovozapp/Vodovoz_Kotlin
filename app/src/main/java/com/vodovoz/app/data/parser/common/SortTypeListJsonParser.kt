package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.data.model.common.SortTypeList
import org.json.JSONArray
import org.json.JSONObject

object SortTypeListJsonParser {

    fun JSONObject.parseSortTypeList(): SortTypeList {
        return SortTypeList(
            name = safeString("NAMEGLAV"),
            sortTypeList = if (has("DANNIESORT")) {
                getJSONArray("DANNIESORT").parseSortTypeList()
            } else {
                listOf()
            }
        )
    }

    fun JSONArray.parseSortTypeList(): List<SortType> = mutableListOf<SortType>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseSortType())
        }
    }

    fun JSONObject.parseSortType(): SortType {
        return SortType(
            sortName = safeString("NAME"),
            value = safeString("ZNACHIE"),
            orientation = safeString("SORT")
        )
    }
}