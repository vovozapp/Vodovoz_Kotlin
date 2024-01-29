package com.vodovoz.app.feature.profile.adapter

interface ProfileFlowClickListener {

    fun onHeaderClick()
    fun logout()

    fun onAddressesClick()
    fun onUrlClick(url: String?)
    fun onUrlTwoClick(url: String?)

    fun onOrdersHistoryClick()
    fun onLastPurchasesClick()

    fun onRepairClick()
    fun onOzonClick()

    fun onQuestionnaireClick()
    fun onNotificationsClick()

    fun onAboutDeliveryClick()
    fun onAboutPaymentClick()

    fun onMyChatClick()
    fun onSafetyClick()
    fun onAboutAppClick()

    fun onNewWaterApp()
    fun onFetchDiscount()
    fun onActiveCertificate()

    fun onWhatsUpClick(phone: String?)
    fun onViberClick(phone: String?)
    fun onTelegramClick(phone: String?)

}