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