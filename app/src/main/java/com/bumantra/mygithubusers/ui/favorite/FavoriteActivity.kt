package com.bumantra.mygithubusers.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumantra.mygithubusers.databinding.ActivityFavoriteBinding
import com.bumantra.mygithubusers.ui.ViewModelFactory
import com.bumantra.mygithubusers.ui.main.GithubAdapter
import com.bumantra.mygithubusers.ui.main.MainViewModel

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: MainViewModel by viewModels { factory }
        val usersAdapter = GithubAdapter()

        viewModel.getAllFavoriteUser().observe(this) { favorite ->
            usersAdapter.submitList(favorite)
        }

        binding.rvFav.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }
        supportActionBar?.title = "Favorite User"
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
}