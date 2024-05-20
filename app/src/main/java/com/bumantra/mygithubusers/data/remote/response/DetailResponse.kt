package com.bumantra.mygithubusers.data.remote.response

import com.google.gson.annotations.SerializedName

data class DetailResponse(
	@field:SerializedName("login")
	val login: String,

	@field:SerializedName("followers")
	val followers: Int,

	@field:SerializedName("avatar_url")
	val avatarUrl: String,

	@field:SerializedName("following")
	val following: Int,

	@field:SerializedName("name")
	val name: String,

	var isFavorite: Boolean = false
)
