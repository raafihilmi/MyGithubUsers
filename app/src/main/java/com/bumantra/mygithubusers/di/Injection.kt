package com.bumantra.mygithubusers.di

import android.content.Context
import com.bumantra.mygithubusers.data.FavoriteUserRepository
import com.bumantra.mygithubusers.data.local.room.FavoriteUserDatabase
import com.bumantra.mygithubusers.data.remote.retrofit.ApiConfig
import com.bumantra.mygithubusers.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): FavoriteUserRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteUserDatabase.getInstance(context)
        val dao = database.favoriteUserDao()
        val appExecutors = AppExecutors()
        return FavoriteUserRepository.getInstance(apiService, dao, appExecutors)
    }
}