package com.vodovoz.app.feature.profile.adapter

interface ProfileFlowClickListener {

    fun onHeaderClick()
    fun logout()

    fun onAddressesClick()
    fun onUrlClick()

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

}