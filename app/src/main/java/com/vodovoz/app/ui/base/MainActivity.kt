package com.vodovoz.app.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.ActivityMainBinding
import com.vodovoz.app.util.extensions.snack
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }

        observeRatingSnackbar()
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