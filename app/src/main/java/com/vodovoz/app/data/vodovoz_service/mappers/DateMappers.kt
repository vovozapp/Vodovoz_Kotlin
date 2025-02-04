package com.vodovoz.app.data.vodovoz_service.mappers

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun mapToZonedDateTime(dateTime: String): ZonedDateTime? {
    val promotionDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
    return try {
        LocalDateTime.parse(dateTime, promotionDateFormatter).atZone(ZoneId.of("Europe/Moscow"))
    } catch (e: DateTimeParseException) {
        null
    }
}
