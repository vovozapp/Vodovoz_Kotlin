package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.PromotionDetailEntity
import com.vodovoz.app.data.parser.response.promotion.PromotionDetailResponseJsonParser
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.PromotionMapper.mapToUI
import com.vodovoz.app.ui.model.PromotionDetailUI

object PromotionDetailMapper {

    fun PromotionDetailEntity.mapToUI() = PromotionDetailUI(
        id = id,
        name = name,
        detailText = detailText,
        detailPicture = detailPicture.parseImagePath(),
        statusColor = statusColor,
        status = status,
        timeLeft = timeLeft,
        forYouCategoryDetailUI = forYouCategoryDetailEntity.mapToUI(),
        promotionCategoryDetailUI = promotionCategoryDetailEntity?.mapToUI()
    )

    fun PromotionDetailResponseJsonParser.PromotionDetailEntityError.mapToUI() = PromotionDetailResponseJsonParser.PromotionDetailErrorUI(
        title = title,
        message = message,
        bottomMessage = bottomMessage,
        promotionUIList = promotionEntityList.mapToUI()
    )

}