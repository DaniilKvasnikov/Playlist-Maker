package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.NavigationRepository
import com.example.playlistmaker.settings.domain.api.ShareAppUseCase

class ShareAppUseCaseImpl(
    private val navigationRepository: NavigationRepository
) : ShareAppUseCase {
    override fun execute() {
        navigationRepository.shareApp()
    }
}
