package com.example.network

import com.example.common.datastore.DataStore
import com.example.common.datastore.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideTokenInterceptor(
        dataStore: DataStore
    ): TokenInterceptor = TokenInterceptor(dataStore)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenInterceptor: TokenInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(tokenInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideChatService(retrofit: Retrofit): ChatService {
        return retrofit.create(ChatService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }


    @Provides
    @Singleton
    fun provideAiService(retrofit: Retrofit): AiService {
        return retrofit.create(AiService::class.java)
    }

}