package com.bumantra.mygithubusers.ui.followers

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumantra.mygithubusers.data.Result
import com.bumantra.mygithubusers.data.local.entity.FavoriteUser
import com.bumantra.mygithubusers.databinding.FragmentFollowBinding
import com.bumantra.mygithubusers.ui.main.GithubAdapter
import com.bumantra.mygithubusers.ui.main.MainViewModel
import com.bumantra.mygithubusers.ui.ViewModelFactory

class FollowFragment : Fragment() {

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding
    private lateinit var username: String
    private var position: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FrameLayout? {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME) ?: ""
        }

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: MainViewModel by viewModels { factory }

        val usersAdapter = GithubAdapter()

        if (position == 1) {
            Log.d("CEK POSISI", "onViewCreated: true")
            viewModel.getFollowers(username).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding?.followProgressBar?.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding?.followProgressBar?.visibility = View.GONE
                            val userData = result.data
                            val favoriteUsersList = userData?.map { item ->
                                FavoriteUser(
                                    item.login,
                                    item.avatarUrl,
                                    item.isFavorite
                                )
                            }
                            usersAdapter.submitList(favoriteUsersList)
                        }

                        is Result.Error -> {
                            binding?.followProgressBar?.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "Terjadi masalah " + result.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else {
            Log.d("CEK POSISI", "onViewCreated: false")
            viewModel.getFollowing(username).observe(viewLifecycleOwner) {result ->
                if ( result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding?.followProgressBar?.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding?.followProgressBar?.visibility = View.GONE
                            val userData = result.data
                            val favoriteUsersList = userData?.map { item ->
                                FavoriteUser(
                                    item.login,
                                    item.avatarUrl,
                                    item.isFavorite
                                )
                            }
                            usersAdapter.submitList(favoriteUsersList)
                        }
                        is Result.Error -> {
                            binding?.followProgressBar?.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "Terjadi masalah " + result.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        binding?.rvFollow?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARG_USERNAME = "username"
        const val ARG_POSITION = "position"
    }
}

