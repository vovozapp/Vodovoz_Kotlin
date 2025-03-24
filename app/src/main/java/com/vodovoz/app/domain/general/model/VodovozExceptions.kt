package com.vodovoz.app.domain.general.model


class RequestException(
    message: String = "",
    cause: Throwable? = null,
) : IllegalStateException(
    message, cause
)

class WebsiteErrorException(
    message: String = "",
    cause: Throwable? = null,
) : IllegalStateException(
    message, cause
)

class FavoritesNotFoundException(
    message: String = "",
    cause: Throwable? = null,
) : IllegalStateException(
    message, cause
)

class ValidationException(
    message: String = "",
    cause: Throwable? = null,
) : IllegalStateException(
    message, cause
)

class EmptyResultException(
    val htmlText: String = "",
    val data: ErrorDataModel = ErrorDataModel("", "", ""),
    message: String = "",
    cause: Throwable? = null,
) : IllegalStateException(
    message, cause
)

class UserNotRegisterException(
    message: String = "",
    cause: Throwable? = null,
) : IllegalStateException(
    message, cause
)

data class ErrorDataModel(
    val titleHtml: String,
    val descriptionHtml: String,
    val imageUrl: String,
)