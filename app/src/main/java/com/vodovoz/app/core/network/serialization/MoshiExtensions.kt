package com.vodovoz.app.core.network.serialization

import com.squareup.moshi.Moshi
import java.lang.reflect.Type

fun <T : Any> Moshi.fromJson(json: String, type: Type): T {
    val adapter = adapter<T>(type)
    return adapter.fromJson(json)!!
}

inline fun <reified T : Any> Moshi.fromJson(json: String): T {
    val adapter = adapter(T::class.java)
    return adapter.fromJson(json)!!
}