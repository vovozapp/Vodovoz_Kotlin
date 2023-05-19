package com.vodovoz.app.feature.bottom.services.detail.model

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ServicesDetailParser {

    fun ServiceDetailEntity.mapToUI() = ServiceDetailUI(
        id = this.dataEntity?.id,
        name = this.dataEntity?.name,
        description = this.dataEntity?.description,
        preview = this.dataEntity?.preview,
        blocksList = this.dataEntity?.blocksList?.mapToUI() ?: emptyList()
    )

    fun List<ServiceDetailBlockEntity>.mapToUI(): List<ServiceDetailBlockUI> = mutableListOf<ServiceDetailBlockUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun ServiceDetailBlockEntity.mapToUI() = ServiceDetailBlockUI(
        blockTitle = this.blockTitle,
        coef = this.coef,
        extProductId = this.extProductId,
        productList = this.productEntityList.mapToUI()
    )

    fun ResponseBody.parseServiceDetail(): ResponseEntity<ServiceDetailEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                ServiceDetailEntity(
                    dataEntity = responseJson.parseServiceDetailData()
                )
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseServiceDetailData() = ServiceDetailDataEntity(
        id = safeString("ID"),
        name = safeString("NAME"),
        description = safeString("DETAIL_TEXT"),
        preview = parseImagePathMapper(getString("PREVIEW_PICTURE")),
        blocksList = when(isNull("TOVARY")) {
            true -> listOf()
            false -> getJSONArray("TOVARY").parseServiceDetailBlockList()
        }
    )

    private fun JSONArray.parseServiceDetailBlockList(): List<ServiceDetailBlockEntity> = mutableListOf<ServiceDetailBlockEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseBlock())
        }
    }

    private fun JSONObject.parseBlock() = ServiceDetailBlockEntity(
        blockTitle = safeString("IDKNOPKI"),
        coef = safeInt("KOEFFICIENT"),
        extProductId = safeString("DOPTOVAR"),
        productEntityList = when(isNull("TOVARY")) {
            true -> listOf()
            false -> getJSONArray("TOVAR").parseProductEntityList()
        }
    )

    private fun parseImagePathMapper(string: String?): String {
        if (string == null) return ""
        return StringBuilder()
            .append("http://mvodovoz.tw1.ru/")
            .append(string.replace("\"", ""))
            .toString()
    }
}