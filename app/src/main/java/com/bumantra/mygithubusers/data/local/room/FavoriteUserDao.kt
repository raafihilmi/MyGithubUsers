package com.bumantra.mygithubusers.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bumantra.mygithubusers.data.local.entity.FavoriteUser

@Dao
interface FavoriteUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: List<FavoriteUser>)

    @Update
    fun update(user: FavoriteUser)

    @Query("SELECT * from favorite")
    fun getUsers(): LiveData<List<FavoriteUser>>

    @Query("SELECT * FROM favorite where isFavorite = 1")
    fun getAllFavoriteUser(): LiveData<List<FavoriteUser>>

    @Query("DELETE FROM favorite WHERE isFavorite = 0")
    fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM favorite WHERE login = :login AND isFavorite = 1)")
    fun isFavorites(login: String): Boolean
}