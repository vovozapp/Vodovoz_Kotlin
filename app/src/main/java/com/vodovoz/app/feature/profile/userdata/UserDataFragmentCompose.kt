package com.vodovoz.app.feature.profile.userdata

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.MediaColumns
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.media.ImagePickerFragment
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


enum class Gender1(
    val genderName: String,
) {
    MALE("Мужской"),
    FEMALE("Женский")
}

@AndroidEntryPoint
class UserDataFragment : Fragment() {

    private val viewModel: UserDataFlowViewModel by viewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()


    @Inject
    lateinit var permissionsControllerFactory: PermissionsController.Factory
    private val permissionsController by lazy { permissionsControllerFactory.create(requireActivity()) }

    @Inject
    lateinit var tabManager: TabManager

    override fun onStart() {
        super.onStart()
        tabManager.changeTabVisibility(false)
    }

    override fun onStop() {
        super.onStop()
        tabManager.changeTabVisibility(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchUserData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState by rememberUpdatedState(pagingState.data)
                    val snackbarHostState = remember { SnackbarHostState() }

                    when (viewState.uiState) {
                        UserDataFlowViewModel.UserDataUiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.fetchUserData() }
                        }

                        UserDataFlowViewModel.UserDataUiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        UserDataFlowViewModel.UserDataUiState.Success -> {
                            UserDataScreen(
                                viewModel = viewModel,
                                viewState = viewState,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }


                    LifecycleEffect {
                        viewModel.observeEvent().collect { event ->
                            when (event) {
                                UserDataFlowViewModel.UserDataEvents.Logout -> {

                                }

                                is UserDataFlowViewModel.UserDataEvents.NavigateToGenderChoose -> {

                                }

                                UserDataFlowViewModel.UserDataEvents.ShowDatePicker -> {

                                }

                                UserDataFlowViewModel.UserDataEvents.UpdateProfile -> {
                                    profileViewModel.fetchProfileDetails()
                                }

                                is UserDataFlowViewModel.UserDataEvents.UpdateUserDataEvent -> {

                                }

                                UserDataFlowViewModel.UserDataEvents.GoBack -> {
                                    findNavController().popBackStack()
                                }

                                is UserDataFlowViewModel.UserDataEvents.ShowSnackbar -> {
                                    launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        snackbarHostState.showSnackbar(event.message)
                                    }
                                }

                                UserDataFlowViewModel.UserDataEvents.OpenImagePicker -> {
                                    findNavController().navigate(
                                        R.id.imagePickerFragment,
                                        bundleOf(ImagePickerFragment.IMAGE_PICKER_RECEIVER to ImagePickerFragment.AVATAR)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}