package com.vodovoz.app.ui.base

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.google.android.youtube.player.internal.v
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ActivityMainBinding

import com.vodovoz.app.ui.fragment.main.MainFragment
import com.vodovoz.app.ui.view.CustomSnackbar
import com.vodovoz.app.util.Keys
import com.yandex.mapkit.MapKitFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(Keys.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }

        showMainFragment()
    }

    private fun showMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcvMainContainer, MainFragment())
            .commit()
    }

}