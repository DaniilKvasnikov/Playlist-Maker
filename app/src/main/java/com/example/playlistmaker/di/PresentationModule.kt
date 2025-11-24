package com.example.playlistmaker.di

import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        SearchViewModel(
            searchTracksUseCase = get(),
            getSearchHistoryUseCase = get(),
            saveTrackToHistoryUseCase = get(),
            clearSearchHistoryUseCase = get()
        )
    }

    viewModel {
        AudioPlayerViewModel(
            preparePlayerUseCase = get(),
            playUseCase = get(),
            pauseUseCase = get(),
            releasePlayerUseCase = get(),
            getCurrentPositionUseCase = get(),
            isPlayingUseCase = get()
        )
    }

    viewModel {
        SettingsViewModel(
            getThemeSettingsUseCase = get(),
            saveThemeSettingsUseCase = get(),
            applyThemeUseCase = get(),
            shareAppUseCase = get(),
            openSupportUseCase = get(),
            openTermsUseCase = get()
        )
    }
}
