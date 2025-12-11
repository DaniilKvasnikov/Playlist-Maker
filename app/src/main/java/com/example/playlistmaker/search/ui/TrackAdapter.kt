package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.search.ui.models.TrackUI

class TrackAdapter(
    private val items: List<TrackUI>,
    private val onTrackClick: (TrackUI) -> Unit = {}
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder.from(parent)

    override fun onBindViewHolder( holder: TrackViewHolder, position: Int ) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            onTrackClick(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class TrackViewHolder(private val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: TrackUI) {
            binding.tvTitle.text = track.trackName
            binding.tvSubtitle.text = track.artistName
            binding.tvDuration.text = track.getFormattedTime()

            itemView.post {
                val containerWidth = binding.tvTitle.width
                val chevronWidth = binding.ivChevron2.width
                val durationWidth = binding.tvDuration.width
                val maxWidth = containerWidth - chevronWidth - durationWidth
                
                if (maxWidth > 0) {
                    binding.tvSubtitle.maxWidth = maxWidth
                }
            }
            
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .apply(RequestOptions().transform(RoundedCorners(ARTWORK_CORNER_RADIUS_DP.toPx(itemView))))
                .placeholder(R.drawable.ic_placeholder_45)
                .error(R.drawable.ic_placeholder_45)
                .into(binding.ivArtwork)
        }

        private fun Int.toPx(view: View): Int {
            return (this * view.resources.displayMetrics.density).toInt()
        }

        companion object {
            private const val ARTWORK_CORNER_RADIUS_DP = 2

            fun from(parent: ViewGroup): TrackViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTrackBinding.inflate(inflater, parent, false)
                return TrackViewHolder(binding)
            }
        }
    }
}