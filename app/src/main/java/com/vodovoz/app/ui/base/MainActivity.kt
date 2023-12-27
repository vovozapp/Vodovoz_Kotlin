package com.vodovoz.app.ui.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.messaging.RemoteMessage
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.ActivityMainBinding
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.enableFullScreen
import com.vodovoz.app.util.extensions.snack
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    @Inject
    lateinit var siteStateManager: SiteStateManager

    private val viewModel: SplashFileViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        super.onCreate(savedInstanceState)
        enableFullScreen()
        MapKitFactory.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                siteStateManager.requestSiteState()
            }
        }

        observeRatingSnackbar()

        handleIntent(intent)
        handlePushIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleIntent(intent)
        handlePushIntent(intent)
    }

    private fun handlePushIntent(intent: Intent) {
        val data = intent.extras ?: return
        val remoteMessage = RemoteMessage(data)
        debugLog { "handlePushIntent $remoteMessage" }

        val jsonData = if (remoteMessage.data.isNotEmpty()) {
            JSONObject(remoteMessage.data.toString())
        } else {
            null
        }

        debugLog { "jsonData $jsonData" }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (jsonData != null) siteStateManager.savePushData(jsonData)
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        // val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        val path = appLinkData?.lastPathSegment

        lifecycleScope.launch {
            siteStateManager.saveDeepLinkPath(path)
        }

    }

    private fun observeRatingSnackbar() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                ratingProductManager
                    .observeRatingSnackbar()
                    .collect {
                        this@MainActivity.snack(it)
                    }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}