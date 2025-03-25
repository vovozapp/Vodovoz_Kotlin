package com.vodovoz.app.domain.general.model

data class ProfileDetailsModel(
    val userInfoBlock: UserInfoBlockModel,
    val cards: List<ProfileCardModel>,
    val walletItems: List<ProfileWalletItemModel>,
    val banners: List<BannerModel>,
    val smallMenu: List<ProfileMenuItemModel>,
    val normalMenu: List<ProfileMenuItemModel>,
    val sectionProducts: SectionModel<ProductModel>,
)

data class ProfileMenuItemModel(
    val text: String,
    val imageUrl: String,
    val id: String,
    val description: String,
    val popupWindow: ProfileChatsPopupWindowModel? = null,
)

data class ProfileChatsPopupWindowModel(
    val title: String,
    val description: String,
    val menu: List<ProfileChatItemModel>,
)

data class ProfileChatItemModel(
    val name: String,
    val imageUrl: String,
    val transitionData: String,
)

data class ProfileWalletItemModel(
    val background: String,
    val title: String,
    val titleColor: String,
    val description: String,
    val descriptionColor: String,
    val imageUrl: String,
    val id: String,
    val popupWindow: ProfileWalletPopupWindowModel? = null,
)

data class ProfileWalletPopupWindowModel(
    val title: String,
    val text: String,
)

data class ProfileCardModel(
    val title: String,
    val titleColor: String,
    val description: String,
    val descriptionColor: String,
    val imageUrl: String,
    val id: String,
)

data class UserInfoBlockModel(
    val phoneNumber: String,
    val fullName: String,
    val imageUrl: String,
    val textButton: TextButtonModel,
)


data class TextButtonModel(
    val text: String,
    val textColor: String,
) {
    companion object {
        val Empty = TextButtonModel("", "")
    }
}

