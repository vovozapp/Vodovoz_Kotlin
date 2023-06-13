package com.vodovoz.app.data.parser.common

import org.json.JSONObject

fun JSONObject.safeInt(name: String) = when(has(name)) {
    true -> when(isNull(name)) {
        true -> 0
        false -> getInt(name)
    }
    false -> 0
}

fun JSONObject.safeDouble(name: String) = when(has(name)) {
    true -> when(isNull(name)) {
        true -> 0.0
        false -> getDouble(name)
    }
    false -> 0.0
}

fun JSONObject.safeString(name: String) =  when(has(name)) {
    true -> when(isNull(name)) {
        true -> ""
        false -> getString(name)
    }
    false -> ""
}

fun JSONObject.safeStringConvertToBoolean(name: String) =  when(has(name)) {
    true -> when(isNull(name)) {
        true -> false
        false -> when(getString(name)) {
            "Y" -> true
            "N" -> false
            else -> false
        }
    }
    false -> false
}

fun JSONObject.safeLong(name: String) = when(has(name)) {
    true -> when(isNull(name)) {
        true -> 0L
        false -> getLong(name)
    }
    false -> 0L
}