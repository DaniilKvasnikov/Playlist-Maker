package com.example.playlistmaker.player.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBottomSheetBinding
import com.example.playlistmaker.playlist.domain.models.Playlist
import java.io.File

class BottomSheetPlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<BottomSheetPlaylistAdapter.ViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun setPlaylists(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaylistItemBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    inner class ViewHolder(
        private val binding: PlaylistItemBottomSheetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.playlistTrackCount.text = getTrackCountText(playlist.trackCount)

            if (playlist.imagePath != null && File(playlist.imagePath).exists()) {
                Glide.with(itemView)
                    .load(File(playlist.imagePath))
                    .transform(CenterCrop(), RoundedCorners(2))
                    .into(binding.playlistCover)
            } else {
                Glide.with(itemView)
                    .load(R.drawable.ic_placeholder_45)
                    .transform(CenterCrop(), RoundedCorners(2))
                    .into(binding.playlistCover)
            }

            itemView.setOnClickListener {
                onPlaylistClick(playlist)
            }
        }

        private fun getTrackCountText(count: Int): String {
            val remainder10 = count % 10
            val remainder100 = count % 100

            return when {
                remainder100 in 11..14 -> "$count треков"
                remainder10 == 1 -> "$count трек"
                remainder10 in 2..4 -> "$count трека"
                else -> "$count треков"
            }
        }
    }
}
