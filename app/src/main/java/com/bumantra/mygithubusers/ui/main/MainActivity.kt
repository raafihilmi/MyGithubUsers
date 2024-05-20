package com.bumantra.mygithubusers.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumantra.mygithubusers.data.Result
import com.bumantra.mygithubusers.databinding.ActivityMainBinding
import com.bumantra.mygithubusers.ui.ViewModelFactory
import com.bumantra.mygithubusers.ui.favorite.FavoriteActivity
import com.bumantra.mygithubusers.ui.settings.SettingViewModel
import com.bumantra.mygithubusers.ui.settings.SettingViewModelFactory
import com.bumantra.mygithubusers.ui.settings.SwitchThemeActivity
import com.bumantra.mygithubusers.utils.SettingPreferences
import com.bumantra.mygithubusers.utils.dataStore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: MainViewModel by viewModels { factory }
        val usersAdapter = GithubAdapter()

        val pref = SettingPreferences.getInstance(application.dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]

        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.optionsBtn.setOnClickListener {
            val intent = Intent(this, SwitchThemeActivity::class.java)
            startActivity(intent)
        }

        binding.favoriteAction.setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    searchBar.text = searchView.text
                    viewModel.setUser(searchView.text.toString())
                    searchView.hide()
                    searchView.clearFocus()
                    false
                }
        }

        viewModel.getUser().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        usersAdapter.submitList(null)
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val userData = result.data
                        Log.d("LIST USER", "onCreate: $userData")
                        usersAdapter.submitList(userData)
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Terjadi masalah " + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.rvGithub.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
            setHasFixedSize(false)
        }

        supportActionBar?.hide()

    }
}