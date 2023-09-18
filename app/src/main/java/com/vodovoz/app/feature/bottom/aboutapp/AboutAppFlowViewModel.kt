package com.vodovoz.app.feature.bottom.aboutapp

import androidx.lifecycle.ViewModel
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.feature.bottom.aboutapp.adapter.AboutApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AboutAppFlowViewModel @Inject constructor(
    private val accountManager: AccountManager,
) : ViewModel() {

    fun fetchUserId() = accountManager.fetchAccountId()

    private val aboutAppActionsList = listOf(
        AboutApp(
            id = 0,
            actionImgResId = R.drawable.png_share_app,
            actionNameResId = R.string.share_app_text
        ),
        AboutApp(
            id = 1,
            actionImgResId = R.drawable.png_ic_favorite,
            actionNameResId = R.string.rate_app_text
        ),
        AboutApp(
            id = 2,
            actionImgResId = R.drawable.png_letter,
            actionNameResId = R.string.write_to_developers
        )
    )

    fun observeAboutAppList() = MutableStateFlow(aboutAppActionsList).asStateFlow()
}