package com.vodovoz.app.domain.general.model


open class RequestException(
    message: String = "",
    cause: Throwable? = null,
    val errorData: VodovozPlaceholderModel? = null,
) : IllegalStateException(
    message, cause
)

class WebsiteErrorException(
    message: String = "",
    cause: Throwable? = null,
) : RequestException(
    message, cause
)

class ValidationException(
    message: String = "",
    cause: Throwable? = null,
) : IllegalStateException(
    message, cause
)

class FavoritesNotFoundException(
    message: String = "",
    cause: Throwable? = null,
    errorData: VodovozPlaceholderModel? = null,
) : RequestException(
    message, cause, errorData
)

class EmptyResultException(
    message: String = "",
    errorData: VodovozPlaceholderModel? = null,
    cause: Throwable? = null,
) : RequestException(
    message, cause, errorData
)

class UserNotLoginException(
    message: String = "",
    cause: Throwable? = null,
    errorData: VodovozPlaceholderModel? = null,
) : RequestException(
    message, cause, errorData
)

data class VodovozPlaceholderModel(
    val title: String,
    val headerHtml: String,
    val descriptionHtml: String,
    val imageUrl: String,
    val button: ColorfulButtonModel? = null,
) {
    companion object {
        val Empty = VodovozPlaceholderModel(
            "", "", "", ""
        )
    }
}