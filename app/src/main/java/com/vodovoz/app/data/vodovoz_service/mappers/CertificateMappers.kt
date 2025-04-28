package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.CertificateActivationDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.CertificateButtonDTO
import com.vodovoz.app.data.vodovoz_service.model.CertificateFieldDTO
import com.vodovoz.app.data.vodovoz_service.model.certificate.BUY_CERTIFICATE_OPLATA_VID_DTO
import com.vodovoz.app.data.vodovoz_service.model.certificate.BUY_CERTIFICATE_TAB_DTO
import com.vodovoz.app.data.vodovoz_service.model.certificate.BuyCertificateDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.certificate.FAQ_DTO
import com.vodovoz.app.data.vodovoz_service.model.certificate.FAQ_ITEM_DTO
import com.vodovoz.app.data.vodovoz_service.model.certificate.SERTIFICAT_VID_DTO
import com.vodovoz.app.domain.general.model.CertificateActivationDetailsModel
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.FieldModel
import com.vodovoz.app.domain.general.model.certificate.BuyCertificateCodesModel
import com.vodovoz.app.domain.general.model.certificate.BuyCertificateDetailsModel
import com.vodovoz.app.domain.general.model.certificate.BuyCertificateTabModel
import com.vodovoz.app.domain.general.model.certificate.CertificateModel
import com.vodovoz.app.domain.general.model.certificate.FAQItemModel
import com.vodovoz.app.domain.general.model.certificate.FAQModel
import com.vodovoz.app.domain.general.model.certificate.PaymentTypeModel

fun CertificateActivationDetailsDTO.toDomain(): CertificateActivationDetailsModel {
    return CertificateActivationDetailsModel(
        title = title ?: "",
        field = field?.toDomain()
            ?: throw IllegalArgumentException("CertificateActivationDetailsDTO field can't be null"),
        textHtml = text ?: "",
        textUnderButtonHtml = textUnderButton ?: "",
        button = button?.toDomain()
            ?: throw IllegalArgumentException("CertificateActivationDetailsDTO button can't be null")
    )
}

fun CertificateFieldDTO.toDomain(): FieldModel? {
    return FieldModel(
        id = CODE ?: return null,
        label = "",
        value = "",
        isRequired = OBYZATELNO == "Y",
        valueType = POLE ?: "text",
        hint = TEXT_V_POLE ?: "",
        readOnly = false,
        supportingText = ""
    )
}

fun CertificateButtonDTO.toDomain(): ColorfulButtonModel {
    return ColorfulButtonModel(
        name = TITLE ?: "",
        backgroundColor = BACKGROUND ?: "",
        textColor = "",
    )
}

fun BuyCertificateDetailsDTO.toDomain(): BuyCertificateDetailsModel {
    return BuyCertificateDetailsModel(
        codes = BuyCertificateCodesModel(
            certificates = SERTIFICAT?.CODE
                ?: throw IllegalArgumentException("Certificate code is missing in SERTIFICAT"),
            tabs = DANNYE?.firstOrNull { it.CODE != null }?.CODE
                ?: throw IllegalArgumentException("No tab with a code found in DANNYE"),
            payment = OPLATA?.CODE
                ?: throw IllegalArgumentException("Payment code is missing in OPLATA")
        ),
        title = TITLE ?: "",
        certificatesTitle = SERTIFICAT.NAME
            ?: throw IllegalArgumentException("Certificate title is missing in SERTIFICAT"),
        certificates = SERTIFICAT.VID?.mapToDomain()
            ?: throw IllegalArgumentException("Failed to map certificate types (VID)"),
        tabs = DANNYE.mapToDomain(),
        button = KNOPKA?.toDomain()
            ?: throw IllegalArgumentException("Button (KNOPKA) is not provided"),
        paymentTitle = OPLATA.NAME ?: "",
        paymentTypes = OPLATA.DATA?.mapToDomain()
            ?: throw IllegalArgumentException("Payment types data (OPLATA.DATA) is missing"),
        faq = FAQ?.toDomain() ?: throw IllegalArgumentException("FAQ data is missing"),
    )
}

fun FAQ_DTO.toDomain(): FAQModel {
    return FAQModel(
        name = NAME ?: "",
        image = KARTINKA?.toFullUrl() ?: "",
        items = DATA?.mapToDomain() ?: emptyList()
    )
}

@JvmName("mapToFAQItemModelList")
fun List<FAQ_ITEM_DTO>.mapToDomain(): List<FAQItemModel> {
    return mapNotNull { it.toDomain() }
}

fun FAQ_ITEM_DTO.toDomain(): FAQItemModel? {
    return FAQItemModel(
        name = NAME ?: return null,
        description = OPISANIE ?: return null
    )
}

fun SERTIFICAT_VID_DTO.toDomain(): CertificateModel? {
    return CertificateModel(
        id = ID ?: return null,
        name = NAME ?: return null,
        image = PICTURE?.toFullUrl() ?: ""
    )
}

fun List<SERTIFICAT_VID_DTO>.mapToDomain(): List<CertificateModel> {
    return mapNotNull { it.toDomain() }
}

fun BUY_CERTIFICATE_TAB_DTO.toDomain(): BuyCertificateTabModel? {
    return BuyCertificateTabModel(
        id = TIP ?: return null,
        name = NAME ?: return null,
        fields = DATA?.mapToDomain() ?: emptyList()
    )
}

@JvmName("mapToBuyCertificateTabModelList")
fun List<BUY_CERTIFICATE_TAB_DTO>.mapToDomain(): List<BuyCertificateTabModel> {
    val mapped = mapNotNull { it.toDomain() }
    return mapped.takeIf { it.isNotEmpty() }
        ?: throw IllegalArgumentException("Failed to map any BuyCertificateTabModel from BUY_CERTIFICATE_TAB_DTO list")
}

@JvmName("mapToPaymentTypeModelList")
fun List<BUY_CERTIFICATE_OPLATA_VID_DTO>.mapToDomain(): List<PaymentTypeModel> {
    val mapped = mapNotNull { it.toDomain() }
    return mapped.takeIf { it.isNotEmpty() }
        ?: throw IllegalArgumentException("Failed to map any PaymentTypeModel from BUY_CERTIFICATE_OPLATA_VID_DTO list")
}


fun BUY_CERTIFICATE_OPLATA_VID_DTO.toDomain(): PaymentTypeModel? {
    return PaymentTypeModel(
        id = VALUE ?: return null,
        image = KARTINKA?.toFullUrl() ?: "",
        name = TEXT ?: return null
    )
}