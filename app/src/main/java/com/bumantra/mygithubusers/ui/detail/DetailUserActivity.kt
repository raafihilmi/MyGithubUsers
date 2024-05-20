package com.bumantra.mygithubusers.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.bumantra.mygithubusers.R
import com.bumantra.mygithubusers.data.Result
import com.bumantra.mygithubusers.data.local.entity.FavoriteUser
import com.bumantra.mygithubusers.databinding.ActivityDetailUserBinding
import com.bumantra.mygithubusers.ui.ViewModelFactory
import com.bumantra.mygithubusers.ui.main.MainActivity
import com.bumantra.mygithubusers.ui.main.MainViewModel
import com.bumantra.mygithubusers.ui.main.SectionsPagerAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val detailViewModel by viewModels<MainViewModel> { factory }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val isFavorites = intent.getBooleanExtra("isFavorite", false)
        val username = intent.getStringExtra("username") ?: ""

        detailViewModel.getUserDetail(username).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.detailProgressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.detailProgressBar.visibility = View.GONE
                        val userData = result.data
                        val totalFollowers = userData?.followers.toString()
                        val totalFollowing = userData?.following.toString()
                        binding.tvName.text = userData?.name ?: userData?.login
                        binding.tvDescription.text = userData?.login
                        binding.tvFollowers.text = "$totalFollowers Followers"
                        binding.tvFollowing.text = "$totalFollowing Following"
                        Glide.with(this).load(userData?.avatarUrl).into(binding.imageView2)

                        val favorites = FavoriteUser(
                            userData?.login ?: "", userData?.avatarUrl ?: "",
                            userData?.isFavorite ?: false
                        )
                        val favoriteBtn = binding.iconFavorite

                        var isUserFavorite = isFavorites
                        favoriteBtn.setImageResource(if (isUserFavorite) R.drawable.fill_star else R.drawable.outline_star)

                        favoriteBtn.setOnClickListener {
                            if (!isUserFavorite) {
                                favoriteBtn.setImageResource(R.drawable.fill_star)
                                detailViewModel.saveUser(favorites)
                            } else {
                                favoriteBtn.setImageResource(R.drawable.outline_star)
                                detailViewModel.deleteUser(favorites)
                            }
                            isUserFavorite = !isUserFavorite
                        }
                    }

                    is Result.Error -> {
                        binding.detailProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Terjadi masalah " + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            val sectionsPagerAdapter = SectionsPagerAdapter(this)
            sectionsPagerAdapter.username = username
            binding.viewPager.adapter = sectionsPagerAdapter

            TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "Followers"
                        detailViewModel.getFollowers(username)
                    }

                    1 -> {
                        tab.text = "Following"
                        detailViewModel.getFollowing(username)
                    }
                }
            }.attach()

            supportActionBar?.elevation = 0f
        }
        supportActionBar?.title = "Detail User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val upIntent = NavUtils.getParentActivityIntent(this)
                upIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                if (upIntent != null) {
                    NavUtils.navigateUpTo(this, upIntent)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

}