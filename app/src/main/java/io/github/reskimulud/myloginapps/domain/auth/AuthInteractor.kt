package io.github.reskimulud.myloginapps.domain.auth

import io.github.reskimulud.myloginapps.data.auth.AuthRepository
import javax.inject.Inject

class AuthInteractor @Inject constructor(
    private val authRepository: AuthRepository
): AuthUseCase {
}