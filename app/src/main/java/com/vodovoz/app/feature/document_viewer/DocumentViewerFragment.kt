package com.vodovoz.app.feature.document_viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.design_system.model.DocumentUi
import com.vodovoz.app.feature.document_viewer.model.DocumentViewerEvent
import com.vodovoz.app.util.extensions.disableFullScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DocumentViewerFragment : Fragment() {

    internal val viewModel: DocumentViewerViewModel by viewModels()

    @Inject
    lateinit var tabManager: TabManager

    override fun onStop() {
        super.onStop()
        requireActivity().disableFullScreen()
        tabManager.changeTabVisibility(true)
    }

    override fun onStart() {
        super.onStart()
        val colorWhite = Color.White.hashCode()
        requireActivity().enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(colorWhite, colorWhite),
            statusBarStyle = SystemBarStyle.auto(colorWhite, colorWhite)
        )
        tabManager.changeTabVisibility(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val doc = arguments?.getParcelable<DocumentUi>("documentId")
        doc?.let {
            viewModel.setDocument(doc)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                val viewState by viewModel.state.collectAsStateWithLifecycle()

                VodovozTheme {


                    DocumentViewerScreen(
                        viewModel = viewModel,
                        viewState = viewState
                    )
                }

                LifecycleEffect {
                    viewModel.events.collect { event ->
                        when (event) {
                            DocumentViewerEvent.GoBack -> {
                                findNavController().popBackStack()
                            }
                        }
                    }
                }

            }


        }
    }
}