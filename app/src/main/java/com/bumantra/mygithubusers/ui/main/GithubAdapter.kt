package com.bumantra.mygithubusers.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumantra.mygithubusers.data.local.entity.FavoriteUser
import com.bumantra.mygithubusers.databinding.ItemUsersBinding
import com.bumantra.mygithubusers.ui.detail.DetailUserActivity
import com.bumptech.glide.Glide

class GithubAdapter :
    ListAdapter<FavoriteUser, GithubAdapter.GithubViewHolder>(DIFF_CALLBACK) {

    class GithubViewHolder(var binding: ItemUsersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: FavoriteUser) {
            binding.tvUsername.text = user.login
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GithubViewHolder {
        val binding = ItemUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GithubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GithubViewHolder, position: Int) {
        val user = getItem(position)
        holder.binding.imageView.loadImage(user.avatarUrl)
        holder.bind(user)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailUserActivity::class.java)
            intent.putExtra("username", user.login)
            intent.putExtra("isFavorite", user.isFavorite)
            holder.itemView.context.startActivity(intent)
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<FavoriteUser> =
            object : DiffUtil.ItemCallback<FavoriteUser>() {
                override fun areItemsTheSame(
                    oldItem: FavoriteUser,
                    newItem: FavoriteUser
                ): Boolean {
                    return oldItem.login == newItem.login
                }

                override fun areContentsTheSame(
                    oldItem: FavoriteUser,
                    newItem: FavoriteUser
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    private fun ImageView.loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .into(this)
    }

}