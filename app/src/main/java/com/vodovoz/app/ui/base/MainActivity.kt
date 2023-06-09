package com.vodovoz.app.ui.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.RemoteMessage
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.ActivityMainBinding
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.util.extensions.debugLog
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }

        lifecycleScope.launchWhenStarted {
            siteStateManager.requestSiteState()
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

        lifecycleScope.launchWhenStarted {
            if (jsonData != null) siteStateManager.savePushData(jsonData)
        }
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        val path = appLinkData?.lastPathSegment

        lifecycleScope.launch {
            siteStateManager.saveDeepLinkPath(path)
        }

    }

    private fun observeRatingSnackbar() {
        lifecycleScope.launchWhenStarted {
            ratingProductManager
                .observeRatingSnackbar()
                .collect {
                    this@MainActivity.snack(it)
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}