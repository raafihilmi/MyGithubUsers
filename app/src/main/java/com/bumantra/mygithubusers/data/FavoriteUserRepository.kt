package com.bumantra.mygithubusers.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.bumantra.mygithubusers.data.local.entity.FavoriteUser
import com.bumantra.mygithubusers.data.local.room.FavoriteUserDao
import com.bumantra.mygithubusers.data.remote.response.DetailResponse
import com.bumantra.mygithubusers.data.remote.response.GithubResponse
import com.bumantra.mygithubusers.data.remote.response.ItemsItem
import com.bumantra.mygithubusers.data.remote.retrofit.ApiService
import com.bumantra.mygithubusers.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteUserRepository private constructor(
    private val apiService: ApiService,
    private val favoriteDao: FavoriteUserDao,
    private val appExecutor: AppExecutors
) {
    val result = MediatorLiveData<Result<List<FavoriteUser>?>>()
    private val _userQuery = MutableLiveData<String>()

    fun getUser(): LiveData<Result<List<FavoriteUser>?>> {
        result.value = Result.Loading
        val client = apiService.getUsers(_userQuery.value ?: USER)
        client.enqueue(object : Callback<GithubResponse> {
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ) {
                if (response.isSuccessful) {
                    val users = response.body()?.items
                    val usersList = users?.map { item ->
                        FavoriteUser(
                            item.login,
                            item.avatarUrl,
                            item.isFavorite
                        )
                    }
                    result.value = Result.Success(usersList)

                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }

        })
        return result
    }

    fun getUserDetail(username: String): LiveData<Result<DetailResponse?>> {
        val result = MutableLiveData<Result<DetailResponse?>>()
        result.value = Result.Loading

        val client = apiService.getDetailUser(username)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                if (response.isSuccessful) {
                    val users = response.body()
                    val usersList = ArrayList<FavoriteUser>()
                    if (users != null) {
                        appExecutor.diskIO.execute {
                            val isFavorite = favoriteDao.isFavorites(users.login)
                            val user = FavoriteUser(
                                users.login,
                                users.avatarUrl,
                                isFavorite,
                            )
                            usersList.add(user)

                            favoriteDao.deleteAll()
                            favoriteDao.insert(usersList)
                        }
                    }
                    result.value = Result.Success(response.body())
                    Log.d("Data diterima", "onResponse: $result.value")
                } else {
                    result.value = Result.Error("Gagal menampilkan detail user, silakan coba lagi")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })

        return result
    }

    fun getFollowers(username: String): LiveData<Result<List<ItemsItem>?>> {
        val result = MediatorLiveData<Result<List<ItemsItem>?>>()
        result.value = Result.Loading

        val client = apiService.getFollowers(username)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                if (response.isSuccessful) {
                    val users = response.body()
                    result.value = Result.Success(users)

                } else {
                    result.value =
                        Result.Error("Gagal menampilkan list Followers, silakan coba lagi")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        return result
    }

    fun getFollowing(username: String): LiveData<Result<List<ItemsItem>?>> {
        val result = MediatorLiveData<Result<List<ItemsItem>?>>()
        result.value = Result.Loading

        val client = apiService.getFollowing(username)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                if (response.isSuccessful) {
                    val users = response.body()
                    result.value = Result.Success(users)
                } else {
                    result.value =
                        Result.Error("Gagal menampilkan list Following, silakan coba lagi")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                result.value =
                    Result.Error("Gagal terhubung, silakan periksa koneksi internet Anda")
            }
        })

        return result
    }

    fun setUser(newUser: String) {
        _userQuery.value = newUser
        getUser()
    }

    fun getAllFavoriteUser(): LiveData<List<FavoriteUser>> {
        return favoriteDao.getAllFavoriteUser()
    }

    fun setUserFavorite(user: FavoriteUser, favoriteState: Boolean) {
        appExecutor.diskIO.execute {
            user.isFavorite = favoriteState
            favoriteDao.update(user)
        }
    }

    companion object {
        @Volatile
        private var instance: FavoriteUserRepository? = null
        fun getInstance(
            apiService: ApiService,
            favoriteDao: FavoriteUserDao,
            appExecutor: AppExecutors
        ): FavoriteUserRepository =
            instance ?: synchronized(this) {
                instance ?: FavoriteUserRepository(apiService, favoriteDao, appExecutor)
            }.also { instance = it }

        private const val USER = "raafi"
    }

}