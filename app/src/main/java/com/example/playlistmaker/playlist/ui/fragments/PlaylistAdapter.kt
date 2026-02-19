package com.example.playlistmaker.playlist.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.playlist.domain.models.Playlist
import java.io.File

class PlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun setPlaylists(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    inner class PlaylistViewHolder(
        private val binding: PlaylistItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.playlistTrackCount.text = getTrackCountText(playlist.trackCount)

            if (playlist.imagePath != null && File(playlist.imagePath).exists()) {
                Glide.with(itemView)
                    .load(File(playlist.imagePath))
                    .transform(CenterCrop(), RoundedCorners(8))
                    .into(binding.playlistCover)
                binding.playlistCover.setBackgroundResource(R.drawable.rounded_rectangle_list)
            } else {
                Glide.with(itemView)
                    .load(R.drawable.ic_placeholder_45)
                    .transform(CenterCrop(), RoundedCorners(8))
                    .into(binding.playlistCover)
                binding.playlistCover.background = null
            }

            itemView.setOnClickListener {
                onPlaylistClick(playlist)
            }
        }

        private fun getTrackCountText(count: Int): String {
            return itemView.resources.getQuantityString(R.plurals.track_count, count, count)
        }
    }
}
