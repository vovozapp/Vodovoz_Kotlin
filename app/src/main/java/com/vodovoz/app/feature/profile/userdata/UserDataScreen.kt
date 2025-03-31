package com.vodovoz.app.feature.profile.userdata

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.snackbar.VodovozSnackbar
import com.vodovoz.app.design_system.composables.snackbar.VodovozSnackbarHost
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.profile.userdata.composables.UserDataBody

@Composable
fun UserDataScreen(
    viewModel: UserDataFlowViewModel,
    viewState: UserDataFlowViewModel.UserDataState,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            VodovozTopBar(
                onBack = { viewModel.navigateBack() },
                title = viewState.title,
                actionPainter = painterResource(id = R.drawable.ic_logout),
                onActionClick = {

                }
            )
        },
        snackbarHost = {
            VodovozSnackbarHost(hostState = snackbarHostState)
        },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { paddingValues ->
        UserDataBody(
            modifier = Modifier.padding(paddingValues).consumeWindowInsets(paddingValues),
            fields = viewState.fields,
            photoTitle = viewState.photoTitle,
            photo = viewState.photo,
            photoDescription = viewState.photoDescription,
            buttonEnabled = viewState.buttonEnabled,
            onFieldValueChange = { field, newValue ->
                viewModel.changeFieldValue(field, newValue)
            },
            onSaveDataClick = {
                viewModel.updateUserData()
            },
            onDeleteAccountClick = {
                viewModel.deleteAccount()
            },
            onAvatarClick = {
                viewModel.chooseImage()
            }
        )
    }
}