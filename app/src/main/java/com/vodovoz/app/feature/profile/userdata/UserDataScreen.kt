package com.vodovoz.app.feature.profile.userdata

import VodovozCalendarDialog
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.dialogs.VodovozDialog
import com.vodovoz.app.design_system.composables.snackbar.VodovozSnackbarHost
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.profile.userdata.composables.UserDataBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

@Composable
fun UserDataScreen(
    viewModel: UserDataFlowViewModel,
    viewState: UserDataFlowViewModel.UserDataState,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        topBar = {
            VodovozTopBar(
                onBack = { viewModel.navigateBack() },
                title = viewState.title,
                actionPainter = painterResource(id = R.drawable.ic_logout),
                onLogoutClick = {
                    viewModel.showLogoutDialog()
                }
            )
        },
        snackbarHost = {
            VodovozSnackbarHost(hostState = snackbarHostState)
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        UserDataBody(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
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
                viewModel.showDeleteAccountDialog()
            },
            onAvatarClick = {
                viewModel.chooseImage()
            },
            onFieldClick = { field ->
                viewModel.checkDatePicker(field)
            }
        )
    }


    if (viewState.showLogoutDialog) {
        VodovozDialog(
            title = stringResource(id = R.string.exit),
            description = stringResource(id = R.string.exit_confirmation),
            acceptButtonText = stringResource(id = R.string.exit).uppercase(),
            cancelButtonText = stringResource(id = R.string.cancel).uppercase(),
            onDismiss = {
                viewModel.closeLogoutDialog()
            },
            onAccept = {
                viewModel.logout()
            }
        )
    }

    if (viewState.showDeleteAccountDialog) {
        VodovozDialog(
            title = stringResource(id = R.string.delete_account),
            description = stringResource(id = R.string.delete_account_confirmation),
            acceptButtonText = stringResource(id = R.string.delete).uppercase(),
            cancelButtonText = stringResource(id = R.string.cancel).uppercase(),
            onDismiss = {
                viewModel.closeDeleteAccountDialog()
            },
            onAccept = {
                viewModel.deleteAccount()
            }
        )
    }

    if (viewState.showDatePicker) {
        val dateString = viewState.fields.firstOrNull { it.id == "data" }?.value ?: ""
        val date = runCatching {
            LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }.getOrNull()
        val today = LocalDate.now()

        VodovozCalendarDialog(
            initialDate = date ?: today,
            isSelectableDate = { currentDate ->
                currentDate < today
            },
            onDateSelected = { selectedDate ->
                viewModel.changeDate(selectedDate)
            },
            onDismiss = {
                viewModel.closeDatePicker()
            }
        )
    }
}
