package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.profile.CHAT_MENU_DTO
import com.vodovoz.app.data.vodovoz_service.model.profile.DENIGI_DTO
import com.vodovoz.app.data.vodovoz_service.model.profile.DENIGI_TEXT_OKNO_DTO
import com.vodovoz.app.data.vodovoz_service.model.profile.PROFILE_BLOCK_DTO
import com.vodovoz.app.data.vodovoz_service.model.profile.PROFILE_MENO_OKNO_DTO
import com.vodovoz.app.data.vodovoz_service.model.profile.PROFILE_MINI_MENU_DTO
import com.vodovoz.app.data.vodovoz_service.model.profile.PROFILE_NORMAL_MENU_DTO
import com.vodovoz.app.data.vodovoz_service.model.profile.PROFIL_DTO
import com.vodovoz.app.data.vodovoz_service.model.profile.ProfileDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.profile.TEXT_KNOPKA_DTO
import com.vodovoz.app.domain.general.model.ProfileCardModel
import com.vodovoz.app.domain.general.model.ProfileChatItemModel
import com.vodovoz.app.domain.general.model.ProfileChatsPopupWindowModel
import com.vodovoz.app.domain.general.model.ProfileDetailsModel
import com.vodovoz.app.domain.general.model.ProfileMenuItemModel
import com.vodovoz.app.domain.general.model.ProfileWalletItemModel
import com.vodovoz.app.domain.general.model.ProfileWalletPopupWindowModel
import com.vodovoz.app.domain.general.model.SectionModel
import com.vodovoz.app.domain.general.model.TextButtonModel
import com.vodovoz.app.domain.general.model.UserInfoBlockModel


fun ProfileDetailsDTO.toDomain(): ProfileDetailsModel {
    return ProfileDetailsModel(
        userInfoBlock = PROFIL?.toDomain()
            ?: throw IllegalArgumentException("UserInfoBlock can't be null"),
        cards = BLOCK?.map { it.toDomain() } ?: emptyList(),
        walletItems = DENIGI?.map { it.toDomain() } ?: emptyList(),
        banners = BANNER?.mapToDomain() ?: emptyList(),
        smallMenu = MENU?.MINI?.map { it.toDomain() } ?: emptyList(),
        normalMenu = MENU?.NORMAL?.map { it.toDomain() } ?: emptyList(),
        sectionProducts = TOVARY?.toDomain() ?: SectionModel.empty()
    )
}

fun DENIGI_DTO.toDomain(): ProfileWalletItemModel {
    return ProfileWalletItemModel(
        background = BACKGROUND ?: "",
        titleColor = ZAGALOVOK?.TEXTCOLOR ?: "",
        title = ZAGALOVOK?.TITLE ?: "",
        description = OPISANIE?.TITLE ?: "",
        descriptionColor = OPISANIE?.TEXTCOLOR ?: "",
        imageUrl = IMAGE?.toFullUrl() ?: "",
        id = ID ?: "",
        popupWindow = TEXT_OKNO?.toDomain()
    )
}

fun DENIGI_TEXT_OKNO_DTO.toDomain(): ProfileWalletPopupWindowModel {
    return ProfileWalletPopupWindowModel(
        title = TITLE ?: "",
        text = TEXT ?: ""
    )
}


fun PROFILE_BLOCK_DTO.toDomain(): ProfileCardModel {
    return ProfileCardModel(
        title = ZAGALOVOK?.TITLE ?: "",
        titleColor = ZAGALOVOK?.TEXTCOLOR ?: "",
        description = OPISANIE?.TITLE ?: "",
        descriptionColor = OPISANIE?.TEXTCOLOR ?: "",
        imageUrl = IMAGE?.toFullUrl() ?: "",
        id = ID ?: ""
    )
}


fun PROFIL_DTO.toDomain(): UserInfoBlockModel {
    return UserInfoBlockModel(
        phoneNumber = this.FIO ?: "",
        fullName = this.FIO ?: "",
        imageUrl = this.IMAGE?.toFullUrl() ?: "",
        textButton = this.TEXT_KNOPKA?.toDomain() ?: TextButtonModel.Empty
    )
}


fun TEXT_KNOPKA_DTO.toDomain(): TextButtonModel {
    return TextButtonModel(
        text = this.TITLE ?: "",
        textColor = this.TEXTCOLOR ?: ""
    )
}


fun PROFILE_MINI_MENU_DTO.toDomain(): ProfileMenuItemModel {
    return ProfileMenuItemModel(
        text = this.TEXT ?: "",
        imageUrl = this.IMAGE?.toFullUrl() ?: "",
        id = this.ID ?: "",
        description = "",
        popupWindow = null
    )
}

fun PROFILE_NORMAL_MENU_DTO.toDomain(): ProfileMenuItemModel {
    return ProfileMenuItemModel(
        text = this.TEXT ?: "",
        imageUrl = this.IMAGE?.toFullUrl() ?: "",
        id = this.ID ?: "",
        description = this.OPISANIE ?: "",
        popupWindow = this.TEXT_OKNO?.toDomain()
    )
}


fun PROFILE_MENO_OKNO_DTO.toDomain(): ProfileChatsPopupWindowModel {
    return ProfileChatsPopupWindowModel(
        title = this.ID?.TITLE ?: "",
        description = this.ID?.OPISANIE ?: "",
        menu = this.ID?.MENU?.mapNotNull { it?.toDomain() } ?: emptyList()
    )
}


fun CHAT_MENU_DTO.toDomain(): ProfileChatItemModel {
    return ProfileChatItemModel(
        name = this.TEXT ?: "",
        imageUrl = this.IMAGE ?: "",
        transitionData = this.CHATDAN ?: ""
    )
}


