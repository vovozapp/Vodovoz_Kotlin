package com.vodovoz.app.common.jivochat

object JivoChatController {

    private var active: Boolean = false
    private var link: String = ""
    fun setParams(active: Boolean, link: String) {
        this.active = active
        this.link =  link
    }

    fun isActive(): Boolean   {
        return active
    }

    fun getLink(): String   {
        return link
    }
}