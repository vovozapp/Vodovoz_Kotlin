package com.vodovoz.app.feature.bottom.services.adapter

import com.vodovoz.app.feature.bottom.services.newservs.model.ServiceNew
import com.vodovoz.app.ui.model.ServiceUI

interface ServicesClickListener {

    fun onItemClick(item: ServiceUI) = Unit
    fun onItemClick(item: ServiceNew) = Unit
}