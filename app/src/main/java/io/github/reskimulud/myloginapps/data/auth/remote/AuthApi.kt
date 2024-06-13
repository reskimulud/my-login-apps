package io.github.reskimulud.myloginapps.data.auth.remote

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthApi @Inject constructor(
    private val apiClient: AuthApiClient
) {
}