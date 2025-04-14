package com.vodovoz.app.util.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri


fun Context.dialPhoneNumber(phoneNumber: String) {
    try {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(dialIntent)
    } catch (_: Exception) {
    }

}

fun Context.startWhatsUp(phone: String) {
    try {
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=${phone}")
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(sendIntent)
    } catch (_: Exception) {
    }
}

fun Context.startWhatsUpWithUri(uriString: String) {
    try {
        val uri = Uri.parse(uriString)
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(sendIntent)
    } catch (_: Exception) { }
}

fun Context.startViber(phone: String) {
    val viberPackageName = "com.viber.voip"

    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(phone)
            )
        )
    } catch (ex: ActivityNotFoundException) {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$viberPackageName")
                )
            )
        } catch (exe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=+$viberPackageName")
                )
            )
        }
    }
}

fun Context.startTelegram(phone: String) {
    val telegramPackageName = "org.telegram.messenger"

    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(phone))
        startActivity(intent)
    } catch (ex: ActivityNotFoundException) {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$telegramPackageName")
                )
            )
        } catch (exe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=+$telegramPackageName")
                )
            )
        }
    }
}

fun Context.startJivo(url: String) {
    val uri = Uri.parse(url)
    val likeIng = Intent(Intent.ACTION_VIEW, uri)
    try {
        startActivity(likeIng)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )
    }
}