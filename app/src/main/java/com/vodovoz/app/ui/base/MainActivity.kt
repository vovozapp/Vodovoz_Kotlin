package com.vodovoz.app.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ActivityMainBinding
import com.vodovoz.app.ui.fragment.main.MainFragment
import com.vodovoz.app.ui.fragment.splash.SplashFragment
import com.vodovoz.app.util.Keys
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }

        showMainFragment()
    }

    private fun showMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcvMainContainer, SplashFragment())
            .commit()
    }

}