package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.NavigationRepository
import com.example.playlistmaker.settings.domain.api.OpenSupportUseCase

class OpenSupportUseCaseImpl(
    private val navigationRepository: NavigationRepository
) : OpenSupportUseCase {
    override fun execute() {
        navigationRepository.openSupport()
    }
}
