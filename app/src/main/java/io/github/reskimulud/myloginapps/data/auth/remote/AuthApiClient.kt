package io.github.reskimulud.myloginapps.data.auth.remote

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiClient {
    @POST("")
    @FormUrlEncoded
    suspend fun postRegister(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("fullName") fullName: String
    )
}