package com.vodovoz.app.util.extensions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri

fun Activity.startWhatsUp(phone: String) {
    val uri = Uri.parse("https://api.whatsapp.com/send?phone=${phone}")
    val sendIntent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(sendIntent)
}

fun Activity.startWhatsUpWithUri(uriString: String) {
    val uri = Uri.parse(uriString)
    val sendIntent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(sendIntent)
}

fun Activity.startViber(phone: String) {
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

fun Activity.startTelegram(phone: String) {
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

fun Activity.startJivo(url: String) {
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