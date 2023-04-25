package com.vodovoz.app.ui.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.ActivityMainBinding
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.snack
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

        observeRatingSnackbar()

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleIntent(intent)
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

}