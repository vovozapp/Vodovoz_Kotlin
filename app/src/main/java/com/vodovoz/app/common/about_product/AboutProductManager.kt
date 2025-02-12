package com.vodovoz.app.common.about_product

import com.vodovoz.app.design_system.model.CharacteristicsBlockUi
import com.vodovoz.app.design_system.model.ContentBlockUi
import com.vodovoz.app.design_system.model.DocumentUi
import com.vodovoz.app.design_system.model.ProductDetailsTabUi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AboutProductManager @Inject constructor() {

    var tabs: List<ProductDetailsTabUi> = emptyList()
        private set
    var characteristicsBlock: ContentBlockUi<List<CharacteristicsBlockUi>> =
        ContentBlockUi("", emptyList(), "")
        private set
    var documentsBlock: ContentBlockUi<List<DocumentUi>> = ContentBlockUi("", emptyList(), "")
        private set
    var descriptionBlock: ContentBlockUi<String> = ContentBlockUi("", "", "")
        private set


    fun updateInfo(
        tabs: List<ProductDetailsTabUi>,
        characteristicBlockList: ContentBlockUi<List<CharacteristicsBlockUi>>,
        documents: ContentBlockUi<List<DocumentUi>>,
        fullDescription: ContentBlockUi<String>,
    ) {
        this.tabs = tabs
        this.characteristicsBlock = characteristicBlockList
        this.documentsBlock = documents
        this.descriptionBlock = fullDescription
    }


}