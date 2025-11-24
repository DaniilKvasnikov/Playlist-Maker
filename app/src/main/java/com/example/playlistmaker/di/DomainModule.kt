package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.api.GetCurrentPositionUseCase
import com.example.playlistmaker.player.domain.api.IsPlayingUseCase
import com.example.playlistmaker.player.domain.api.PauseUseCase
import com.example.playlistmaker.player.domain.api.PlayUseCase
import com.example.playlistmaker.player.domain.api.PreparePlayerUseCase
import com.example.playlistmaker.player.domain.api.ReleasePlayerUseCase
import com.example.playlistmaker.player.domain.impl.GetCurrentPositionUseCaseImpl
import com.example.playlistmaker.player.domain.impl.IsPlayingUseCaseImpl
import com.example.playlistmaker.player.domain.impl.PauseUseCaseImpl
import com.example.playlistmaker.player.domain.impl.PlayUseCaseImpl
import com.example.playlistmaker.player.domain.impl.PreparePlayerUseCaseImpl
import com.example.playlistmaker.player.domain.impl.ReleasePlayerUseCaseImpl
import com.example.playlistmaker.search.domain.api.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.domain.api.GetSearchHistoryUseCase
import com.example.playlistmaker.search.domain.api.SaveTrackToHistoryUseCase
import com.example.playlistmaker.search.domain.api.SearchTracksUseCase
import com.example.playlistmaker.search.domain.impl.ClearSearchHistoryUseCaseImpl
import com.example.playlistmaker.search.domain.impl.GetSearchHistoryUseCaseImpl
import com.example.playlistmaker.search.domain.impl.SaveTrackToHistoryUseCaseImpl
import com.example.playlistmaker.search.domain.impl.SearchTracksUseCaseImpl
import com.example.playlistmaker.settings.domain.api.ApplyThemeUseCase
import com.example.playlistmaker.settings.domain.api.GetThemeSettingsUseCase
import com.example.playlistmaker.settings.domain.api.OpenSupportUseCase
import com.example.playlistmaker.settings.domain.api.OpenTermsUseCase
import com.example.playlistmaker.settings.domain.api.SaveThemeSettingsUseCase
import com.example.playlistmaker.settings.domain.api.ShareAppUseCase
import com.example.playlistmaker.settings.domain.impl.ApplyThemeUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.GetThemeSettingsUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.OpenSupportUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.OpenTermsUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.SaveThemeSettingsUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.ShareAppUseCaseImpl
import org.koin.dsl.module

val domainModule = module {
    // Search use cases
    single<SearchTracksUseCase> {
        SearchTracksUseCaseImpl(get())
    }

    single<GetSearchHistoryUseCase> {
        GetSearchHistoryUseCaseImpl(get())
    }

    single<SaveTrackToHistoryUseCase> {
        SaveTrackToHistoryUseCaseImpl(get())
    }

    single<ClearSearchHistoryUseCase> {
        ClearSearchHistoryUseCaseImpl(get())
    }

    // Player use cases
    single<PreparePlayerUseCase> {
        PreparePlayerUseCaseImpl(get())
    }

    single<PlayUseCase> {
        PlayUseCaseImpl(get())
    }

    single<PauseUseCase> {
        PauseUseCaseImpl(get())
    }

    single<ReleasePlayerUseCase> {
        ReleasePlayerUseCaseImpl(get())
    }

    single<GetCurrentPositionUseCase> {
        GetCurrentPositionUseCaseImpl(get())
    }

    single<IsPlayingUseCase> {
        IsPlayingUseCaseImpl(get())
    }

    // Settings use cases
    single<GetThemeSettingsUseCase> {
        GetThemeSettingsUseCaseImpl(get())
    }

    single<SaveThemeSettingsUseCase> {
        SaveThemeSettingsUseCaseImpl(get())
    }

    single<ApplyThemeUseCase> {
        ApplyThemeUseCaseImpl(get(), get())
    }

    single<ShareAppUseCase> {
        ShareAppUseCaseImpl(get())
    }

    single<OpenSupportUseCase> {
        OpenSupportUseCaseImpl(get())
    }

    single<OpenTermsUseCase> {
        OpenTermsUseCaseImpl(get())
    }
}
