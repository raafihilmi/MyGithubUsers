package com.bumantra.mygithubusers.ui.main

import androidx.lifecycle.ViewModel
import com.bumantra.mygithubusers.data.FavoriteUserRepository
import com.bumantra.mygithubusers.data.local.entity.FavoriteUser

class MainViewModel(private val userRepository: FavoriteUserRepository) : ViewModel() {

    fun getUser() = userRepository.getUser()

    fun getAllFavoriteUser() = userRepository.getAllFavoriteUser()

    fun saveUser(user: FavoriteUser) = userRepository.setUserFavorite(user, true)

    fun deleteUser(user: FavoriteUser) = userRepository.setUserFavorite(user, false)

    fun getUserDetail(username: String) = userRepository.getUserDetail(username)

    fun getFollowers(username: String) = userRepository.getFollowers(username)

    fun getFollowing(username: String) = userRepository.getFollowing(username)

    fun setUser(newUser: String) = userRepository.setUser(newUser)
}