package io.github.reskimulud.myloginapps.data.auth

import io.github.reskimulud.myloginapps.data.auth.remote.AuthApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImplementation @Inject constructor(
    private val authApi: AuthApi
): AuthRepository {
}