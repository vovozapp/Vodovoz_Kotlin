package com.vodovoz.app.domain.general.model


open class RequestException(
    message: String = "",
    cause: Throwable? = null,
    val errorData: ErrorDataModel? = null,
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
    errorData: ErrorDataModel? = null,
) : RequestException(
    message, cause, errorData
)

class EmptyResultException(
    val htmlText: String = "",
    message: String = "",
    errorData: ErrorDataModel? = null,
    cause: Throwable? = null,
) : RequestException(
    message, cause, errorData
)

class UserNotLoginException(
    message: String = "",
    cause: Throwable? = null,
    errorData: ErrorDataModel? = null,
) : RequestException(
    message, cause, errorData
)

data class ErrorDataModel(
    val title: String,
    val headerHtml: String,
    val descriptionHtml: String,
    val imageUrl: String,
    val button: ColorfulButtonModel? = null,
) {
    companion object {
        val Empty = ErrorDataModel(
            "", "", "", ""
        )
    }
}