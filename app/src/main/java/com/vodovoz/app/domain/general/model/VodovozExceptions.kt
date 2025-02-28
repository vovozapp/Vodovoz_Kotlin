package com.vodovoz.app.domain.general.model


class RequestException(
    message: String = "",
    cause: Throwable? = null
): IllegalStateException(
    message, cause
)

class WebsiteErrorException(
    message: String = "",
    cause: Throwable? = null
): IllegalStateException(
    message, cause
)

class FavoriteNotFoundException(
    message: String = "",
    cause: Throwable? = null
): IllegalStateException(
    message, cause
)

class ValidationException(
    message: String = "",
    cause: Throwable? = null
): IllegalStateException(
    message, cause
)

class EmptyResultException(
    val htmlText: String,
    message: String = "",
    cause: Throwable? = null
): IllegalStateException(
    message, cause
)

data class ErrorPlaceholder(
    val title: String,
    val text: String,
    val image: String
)