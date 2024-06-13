package io.github.reskimulud.myloginapps.di.features

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import io.github.reskimulud.myloginapps.data.auth.AuthRepository
import io.github.reskimulud.myloginapps.data.auth.AuthRepositoryImplementation
import io.github.reskimulud.myloginapps.data.auth.remote.AuthApiClient
import io.github.reskimulud.myloginapps.domain.auth.AuthInteractor
import io.github.reskimulud.myloginapps.domain.auth.AuthUseCase
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class AuthProvidesModule {
    @Provides
    fun provideAuthApiClient(client: OkHttpClient): AuthApiClient {
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("")
            .build()
        return retrofit.create(AuthApiClient::class.java)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class AuthBindsModule {
    @Binds
    abstract fun provideAuthRepository(implementation: AuthRepositoryImplementation): AuthRepository

    @Binds
    @ViewModelScoped
    abstract fun provideAuthUseCase(interactor: AuthInteractor): AuthUseCase
}