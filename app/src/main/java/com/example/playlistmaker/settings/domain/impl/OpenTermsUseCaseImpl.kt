package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.NavigationRepository
import com.example.playlistmaker.settings.domain.api.OpenTermsUseCase

class OpenTermsUseCaseImpl(
    private val navigationRepository: NavigationRepository
) : OpenTermsUseCase {
    override fun execute() {
        navigationRepository.openTerms()
    }
}
