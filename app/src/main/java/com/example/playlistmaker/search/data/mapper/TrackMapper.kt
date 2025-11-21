package com.example.playlistmaker.search.data.mapper

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.models.Track

object TrackMapper {
    fun mapDtoToDomain(dto: TrackDto): Track {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100,
            collectionName = dto.collectionName,
            releaseDate = dto.releaseDate,
            primaryGenreName = dto.primaryGenreName ?: "",
            country = dto.country ?: "",
            previewUrl = dto.previewUrl ?: ""
        )
    }

    fun mapDomainToDto(domain: Track): TrackDto {
        return TrackDto(
            trackId = domain.trackId,
            trackName = domain.trackName,
            artistName = domain.artistName,
            trackTimeMillis = domain.trackTimeMillis,
            artworkUrl100 = domain.artworkUrl100,
            collectionName = domain.collectionName,
            releaseDate = domain.releaseDate,
            primaryGenreName = domain.primaryGenreName,
            country = domain.country,
            previewUrl = domain.previewUrl
        )
    }

    fun mapDtoListToDomainList(dtoList: List<TrackDto>): List<Track> {
        return dtoList.map { mapDtoToDomain(it) }
    }

    fun mapDomainListToDtoList(domainList: List<Track>): List<TrackDto> {
        return domainList.map { mapDomainToDto(it) }
    }

    fun getHighResArtworkUrl(artworkUrl100: String): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }
}
