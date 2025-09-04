package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TrackAdapter(
    private val items: List<Track>,
    private val onTrackClick: (Track) -> Unit = {}
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder( holder: TrackViewHolder, position: Int ) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            onTrackClick(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerView: TextView = itemView.findViewById(R.id.tvTitle)
        val infoView: TextView = itemView.findViewById(R.id.tvSubtitle)
        val durationView: TextView = itemView.findViewById(R.id.tvDuration)
        val imageView: ImageView = itemView.findViewById(R.id.ivArtwork)

        fun bind(track: Track) {
            headerView.text = track.trackName
            infoView.text = track.artistName
            durationView.text = track.trackTime
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .apply(RequestOptions().transform(RoundedCorners(2.toPx(itemView))))
                .placeholder(R.drawable.ic_placeholder_45)
                .error(R.drawable.ic_placeholder_45)
                .into(imageView)
        }
        fun Int.toPx(view: View): Int {
            return (this * view.resources.displayMetrics.density).toInt()
        }
    }


}